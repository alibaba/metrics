package com.alibaba.metrics.os.utils;


import com.alibaba.metrics.os.linux.CpuUsageGaugeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;

public class SystemInfoUtils {

    private static final Logger logger = LoggerFactory.getLogger(CpuUsageGaugeSet.class);

    public static String MAC_FILE_NAME = "libsigar-universal64-macosx.dylib";
    public static String WINDOWS_FILE_NAME = "sigar-amd64-winnt.dll";

    public static org.hyperic.sigar.Sigar sigar = null;

    public static void init(){

        if (needLoad()){

            try {
                loadLib();
                sigar = new org.hyperic.sigar.Sigar();
            } catch (Throwable e) {
                logger.error("Load dll error!", e);
            }
        }

    }

    public static boolean needLoad(){

        boolean os = false;
        boolean arch = false;

        String osName = System.getProperty("os.name");
        if (osName != null){
            if (osName.contains("Windows") || osName.contains("Mac")){
                os = true;
            }
        }

        String osArch = System.getProperty("os.arch");
        if (osArch != null){
            arch = osArch.contains("64");
        }

        return os & arch;
    }

    public static void loadLib() throws IOException{

        String osName = System.getProperty("os.name");
        String resource = "/";

        if (osName.contains("Windows")){
            resource = resource + WINDOWS_FILE_NAME;
        }else if (osName.contains("Mac")) {
            resource = resource + MAC_FILE_NAME;
        }


        InputStream input = SystemInfoUtils.class.getResourceAsStream(resource);

        URL res = SystemInfoUtils.class.getResource(resource);

        String path = res.getPath();
        if (path.indexOf("!") > 0)  {
            path = path.substring(0, path.substring(0, path.indexOf("!")).lastIndexOf("/")) + resource;
        }
        if (path.indexOf(":") > 0) {
            path = path.substring(path.indexOf(":") + 1);
        }

        File file = new File(path);

        if (!file.exists()) {
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[8192];

            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        }

        if (file != null){
            System.setProperty("org.hyperic.sigar.path", file.getParent());
            System.load(file.getPath());
        }
    }


    public static void addLibraryDir(String libraryPath) throws IOException {
        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (libraryPath.equals(paths[i])) {
                    return;
                }
            }

            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = libraryPath;
            field.set(null, tmp);
        } catch (IllegalAccessException e) {
            throw new IOException(
                    "Failedto get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException(
                    "Failedto get field handle to set library path");
        }
    }

    public static void main(String[] args) {
        SystemInfoUtils.init();
        System.out.println(System.getProperty("java.library.path"));
        System.out.println(SystemInfoUtils.sigar);;
    }
}

