package com.alibaba.metrics.reporter.file;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileLock;

public class IOUtils {

    public static void closeQuietly(final InputStream input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(final OutputStream output) {
        closeQuietly((Closeable) output);
    }

    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
            ioe.printStackTrace();
        }
    }

    public static void closeQuietly(FileLock fileLock) {
        try {
            if (fileLock != null) {
                fileLock.release();
            }
        } catch (final IOException ioe) {
            // ignore
            ioe.printStackTrace();
        }

    }

}
