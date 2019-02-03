package com.alibaba.metrics.reporter.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认把日志写到 ~/logs 下面。非线程安全，只允许一个线程操作。日志会自动滚动。
 *
 * TODO 在移动日志时，增加类似logback的处理，当文件处于不同Volume时，采用copy的方法
 *
 *
 */
public class RollingFileAppender implements FileAppender {
    private static final Logger logger = LoggerFactory.getLogger(RollingFileAppender.class);
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public static final int DEFAULT_MAX_FILE_SIZE = 50 * 1024 * 1024;
    public static final int DEFAULT_MAX_INDEX = 3;

    // 大部分情况下写入4K数据，可以保证数据一次性写入
    public static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

    public static final String DELETEFILESUFFIX = ".deleted";

    int bufferSize = DEFAULT_BUFFER_SIZE;

    int fileSize = DEFAULT_MAX_FILE_SIZE;

    int maxIndex = DEFAULT_MAX_INDEX;

    /**
     * 从上次setFile之后，已经写入磁盘的数据的大小。 用于保证多进程写入数据文件时，避免因为文件被rename之后，没有及时roll。
     */
    volatile long writtenSizeAfterLastSetFile = 0;
    /**
     * 记录上次setFile时，文件的大小，防止多线程下，文件被rename为index文件之后，长时间继续写。
     * 或者原来的日志文件被删除之后，还在向被删除的文件写入
     */
    volatile long fileLengthWhenLastSetFile = 0;

    static ScheduledExecutorService executor = Executors
            .newSingleThreadScheduledExecutor(new NamedThreadFactory("RollingFileAppender"));

    /**
     * 由一个独立线程去检测文件大小，看是否需要roll了
     */
    volatile boolean bNeedRolling = false;

    String path;

    BufferedOutputStream bufferedOutputStream;

    /**
     * A simple named thread factory.
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        private NamedThreadFactory(String name) {
            final SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "metrics-" + name + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    public static class Builder {
        String baseDir;
        String name;
        int maxIndex = DEFAULT_MAX_INDEX;
        int fileSize = DEFAULT_MAX_FILE_SIZE;

        /**
         * 默认baseDir 是 ~/logs
         *
         * @param dir
         * @return
         */
        public Builder baseDir(String baseDir) {
            this.baseDir = baseDir;
            return this;
        }

        /**
         * 默认情况下文件会放在 ~/logs 下，需要放在其它路径下，请设置baseDir。最终路径是
         * {@code new File(baseDir, name).getAbsolutePath() }
         *
         * @param name
         * @return
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder maxIndex(int maxIndex) {
            this.maxIndex = maxIndex;
            return this;
        }

        public Builder fileSize(int fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public RollingFileAppender build() {
            if (baseDir == null) {
                baseDir = System.getProperty("user.home", "/tmp");
                if (baseDir != null) {
                    baseDir = new File(baseDir, "logs").getAbsolutePath();
                }
            }
            return new RollingFileAppender(new File(baseDir, name).getAbsolutePath(), maxIndex, fileSize);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private RollingFileAppender(String path, int maxIndex, int fileSize) {
        this.path = path;
        this.maxIndex = maxIndex;
        this.fileSize = fileSize;

        setFile();

        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    // 检查日志是否要滚动
                    checkAndMarkRolling();
                    // 检查是否有要删除掉的日志
                    deleteNeedDeleteFile();
                } catch (Throwable e) {
                    logger.error("RollingFileAppender schedule task error.", e);
                }
            }

        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void append(String message) {
        byte[] bytes = message.getBytes(UTF_8);
        append(bytes);
    }

    @Override
    public void append(byte[] data) {
        try {
            bufferedOutputStream.write(data);
            writtenSizeAfterLastSetFile += data.length;
            rollOver();
        } catch (Throwable e) {
            logger.error("append error, path:" + path, e);
            setFile();
        }
    }

    @Override
    public void flush() throws IOException {
        if (bufferedOutputStream != null) {
            bufferedOutputStream.flush();
            File file = new File(path);
            if (file.exists()) {
                if ((file.length() - this.fileLengthWhenLastSetFile) != this.writtenSizeAfterLastSetFile) {
                    // 检测上次setFile之后，写入的数据大小和文件长度的比较，如果是多进程写入，则文件长度的变化不等于
                    // writtenSizeAfterLastSetFile。因为这里flush里，所以判断条件是严格相等
                    bNeedRolling = true;
                }
            }

        }
    }

    /**
     * 检查，并标记日志是否需要roll了，从效率考虑，由定时任务线程来调用
     */
    void checkAndMarkRolling() {
        // 检测上次setFile之后，写入的数据是否已超过文件大小的限制
        if (writtenSizeAfterLastSetFile >= this.fileSize) {
            bNeedRolling = true;
            return;
        }

        File file = new File(path);
        if (file.exists()) {
            long fileLength = file.length();
            if (fileLength >= fileSize) {
                bNeedRolling = true;
            } else if ((fileLength - this.fileLengthWhenLastSetFile) > this.writtenSizeAfterLastSetFile) {
                // 检测上次setFile之后，写入的数据大小和文件长度的比较，如果是多进程写入，则文件长度的变化大于
                // writtenSizeAfterLastSetFile
                bNeedRolling = true;
            } else {
                bNeedRolling = false;
            }
        } else {
            // 文件可能被外部程序删除掉了，这时也需要重新打开
            bNeedRolling = true;
        }
    }

    /**
     * 删除所有以 .delete 结尾的文件，从效率考虑，由定时任务线程来调用，因为大文件删除可能比较慢
     */
    void deleteNeedDeleteFile() {
        File logFile = new File(path);
        File parent = logFile.getParentFile();
        if (parent != null) {
            if (parent.isDirectory()) {
                File[] listFiles = parent.listFiles();
                if (listFiles != null) {
                    for (File file : listFiles) {
                        if (file.isFile() && file.getName().startsWith(logFile.getName())
                                && file.getName().endsWith(DELETEFILESUFFIX)) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    private void setFile() {
        writtenSizeAfterLastSetFile = 0;

        File file = new File(path);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
        }

        if (bufferedOutputStream != null) {
            IOUtils.closeQuietly(bufferedOutputStream);
        }

        try {
            fileLengthWhenLastSetFile = file.length();
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file, true), bufferSize);
        } catch (FileNotFoundException e) {
            logger.error("can not open file:" + path, e);
        }
    }

    private void rollOver() {
        if (bNeedRolling) {
            // 获取文件锁
            final String lockFilePath = path + ".lock";
            final File lockFile = new File(lockFilePath);

            RandomAccessFile raf = null;
            FileLock fileLock = null;
            try {
                try {
                    raf = new RandomAccessFile(lockFile, "rw");
                } catch (FileNotFoundException e) {
                    // 在windows下面多个进程同时以rw模式打开文件，会抛出FileNotFoundException异常
                    // ignore
                    return;
                }

                fileLock = raf.getChannel().tryLock();
                if (fileLock == null) {
                    return;
                }

                // 获取到锁之后，检查文件大小，是否需要滚动
                File logFile = new File(path);
                long logFileLength = logFile.length();

                // 当文件不存在时，长度为0，这时需要重新打开文件
                if (logFileLength == 0) {
                    setFile();
                }

                if (logFileLength < fileSize) {
                    return;
                }

                rollIndexFile();

                // 尝试把日志文件rename为 log.1 文件
                File index1File = indexFile(1);
                // 要先close，否则在windows下rename失败
                closeFile();
                logFile.renameTo(index1File);

            } catch (IOException e) {
                logger.error("try to roll log file error. file:" + lockFile.getAbsolutePath(), e);
            } finally {
                IOUtils.closeQuietly(fileLock);
                IOUtils.closeQuietly(raf);

                // 重新打开日志文件。因为原文件可能被删除掉了，所以当需要rolling时，都重新打开文件。 #28457
                setFile();
            }
        }
    }

    /**
     * 把已存在的index文件编号依次滚动增大
     */
    private void rollIndexFile() {
        int currentMaxIndex = 0;
        // 先从最小的index查找，看哪个index文件是不存在的
        for (int i = 1; i <= maxIndex; ++i) {
            File indexFile = indexFile(i);
            if (!indexFile.exists() || (i == maxIndex)) {
                currentMaxIndex = i;
                break;
            }
        }

        // 如果当前的最大index文件已经是 maxIndex，则需要删除掉
        if (currentMaxIndex == maxIndex) {
            // 先尝试把最大index的文件重命名为 .deleted 文件，在windows下有可能失败
            // .delete 后缀文件会被异步定时清理
            // 如果失败，则尝试直接删除
            File maxIndexFile = indexFile(maxIndex);
            if (maxIndexFile.exists()) {
                if (!maxIndexFile.renameTo(new File(maxIndexFile.getAbsolutePath() + DELETEFILESUFFIX))) {
                    if (!maxIndexFile.delete()) {
                        logger.error("can not delete file:" + maxIndexFile.getAbsolutePath());
                    }
                }
            }
        }

        // 尝试把index文件的index编号依次加大
        for (int index = currentMaxIndex - 1; index > 0; --index) {
            File indexFile = indexFile(index);
            if (indexFile.exists()) {
                File destFile = indexFile(index + 1);
                if (!indexFile.renameTo(destFile)) {
                    logger.error("can not rename file. src:" + indexFile.getAbsolutePath() + ", dest:"
                            + destFile.getAbsolutePath());
                    // 当rename 失败时，尝试删除掉index文件，保证日志可以滚动写
                    if (!indexFile.delete()) {
                        logger.error("can not delete file:" + indexFile.getAbsolutePath());
                    }
                }
            }
        }
    }

    private void closeFile() {
        if (bufferedOutputStream != null) {
            try {
                bufferedOutputStream.close();
            } catch (Exception e) {
                logger.error("can not close file:" + path, e);
            }
            bufferedOutputStream = null;
        }
    }

    private File indexFile(int index) {
        return new File(path + "." + index);
    }

}
