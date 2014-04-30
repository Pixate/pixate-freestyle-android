package com.pixate.freestyle.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;

/**
 * Handles {@link Bitmap} retrieval from a remote location. The default behavior
 * of this class is very simple. A bitmap will be decoded and returned via a
 * {@link BitmapFactory} class. To attach advanced functionality, such as
 * caching and authentication, provide your own {@link PXBitmapDownloader} via
 * the {@link #setDownloader(PXBitmapDownloader)} method.
 * 
 * @author shalom
 */
public class PXURLBitmapLoader {
    public static final long IMAGE_DOWNLOAD_TIMEOUT = 30000;
    private static final PXBitmapDownloader DEFAULT_DOWNLOADER = new PXDefaultImageDownloader();
    private static PXURLBitmapLoader instance;
    private PXBitmapDownloader downloader;
    private long imageDownloadTimeout;

    /**
     * Returns an instance of this bitmap loader.
     * 
     * @return A {@link PXURLBitmapLoader} instance.
     */
    public static PXURLBitmapLoader getInstance() {
        synchronized (PXURLBitmapLoader.class) {
            if (instance == null) {
                instance = new PXURLBitmapLoader();
            }
        }
        return instance;
    }

    // private constructor
    private PXURLBitmapLoader() {
        this.downloader = DEFAULT_DOWNLOADER;
        this.imageDownloadTimeout = IMAGE_DOWNLOAD_TIMEOUT;
    }

    /**
     * Sets the timeout for an image download when the
     * {@link #loadBitmap(Uri, int, int, LoadingCallback, boolean)} is called
     * with a <code>synchronous</code> flag. The default value is
     * {@value #IMAGE_DOWNLOAD_TIMEOUT}ms.
     * 
     * @param timeout The timeout in milliseconds
     */
    public void setImageDownloadTimeout(long timeout) {
        this.imageDownloadTimeout = timeout;
    }

    /**
     * Returns the timeout for an image download when the
     * {@link #loadBitmap(Uri, int, int, LoadingCallback, boolean)} is called
     * with a <code>synchronous</code> flag.
     * 
     * @return The timeout in milliseconds
     */
    public long getImageDownloadTimeout() {
        return imageDownloadTimeout;
    }

    /**
     * Loads a {@link Bitmap} from the given {@link Uri}. This call is
     * asynchronous, and when the loading is completed the provided callback
     * will be informed with a {@link Bitmap} reference.
     * 
     * @param uri
     * @param callback a callback implementation that will be informed when the
     *            bitmap is loaded.
     * @param width The bitmap's requested width
     * @param height The bitmap's requested height
     * @return A {@link Bitmap}
     * @see #loadBitmap(Uri, LoadingCallback, boolean)
     */
    public static void loadBitmap(Uri uri, int width, int height, LoadingCallback<Bitmap> callback) {
        loadBitmap(uri, width, height, callback, false);
    }

    /**
     * Loads a {@link Bitmap} from the given {@link Uri}. This call is can be
     * made synchronous and wait until the loading is complete before it
     * returns.
     * 
     * @param uri
     * @param width The bitmap's requested width
     * @param height The bitmap's requested height
     * @param callback a callback implementation that will be informed when the
     *            bitmap is loaded.
     * @return A {@link Bitmap}
     * @see #loadBitmap(Uri, LoadingCallback)
     */
    public static void loadBitmap(Uri uri, int width, int height, LoadingCallback<Bitmap> callback,
            boolean synchronous) {
        PXURLBitmapLoader loader = getInstance();
        loader.doLoad(uri, width, height, callback, synchronous);
    }

    /**
     * Sets a custom {@link PXBitmapDownloader} that will handle the
     * {@link Bitmap} downloading (retrieval, caching, etc.).
     * 
     * @param downloader A {@link PXBitmapDownloader}. In case <code>null</code>
     *            , a default downloader will be used.
     */
    public void setDownloader(PXBitmapDownloader downloader) {
        if (downloader == null) {
            this.downloader = DEFAULT_DOWNLOADER;
        } else {
            this.downloader = downloader;
        }
    }

    /**
     * Do the actual {@link Bitmap} loading.
     * 
     * @param uri
     * @param width
     * @param height
     * @param callback
     * @param synchronous
     * @throws IOException
     */
    private void doLoad(final Uri uri, int width, int height,
            final LoadingCallback<Bitmap> callback, boolean synchronous) {
        downloader.downloadBitmap(uri, width, height, callback, synchronous);
    }

    /**
     * Default {@link Bitmap} downloader. No caching, or any clever stuff is
     * done here.
     */
    private static class PXDefaultImageDownloader implements PXBitmapDownloader {

        /*
         * (non-Javadoc)
         * @see
         * com.pixate.freestyle.util.PXBitmapDownloader#downloadBitmap(android
         * .net.Uri, int, int,
         * com.pixate.freestyle.util.PXBitmapDownloaderCallback, boolean)
         */
        @Override
        public void downloadBitmap(Uri uri, final int width, final int height,
                final LoadingCallback<Bitmap> callback, boolean synchronous) {
            Thread downloadThread = new Thread(new DownloadRunnable(uri, width, height, callback));
            downloadThread.start();
            if (synchronous) {
                try {
                    downloadThread.join(PXURLBitmapLoader.getInstance().getImageDownloadTimeout());
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        }
    }

    /**
     * Calculate the bitmap sample size before we download and store it in
     * memory.
     * 
     * @param options
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static int calculateSampleSize(BitmapFactory.Options options, int newWidth, int newHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > newHeight || width > newWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > newHeight && (halfWidth / inSampleSize) > newWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * A {@link Runnable} that handles the bitmap downloading.
     */
    private static class DownloadRunnable implements Runnable {
        private Bitmap bitmap;
        private Exception error;
        private Uri uri;
        private int width;
        private int height;
        private LoadingCallback<Bitmap> callback;

        /**
         * Constructs a new download runnable.
         * 
         * @param uri
         * @param width
         * @param height
         * @param callback
         */
        private DownloadRunnable(Uri uri, int width, int height, LoadingCallback<Bitmap> callback) {
            this.uri = uri;
            this.width = width;
            this.height = height;
            this.callback = callback;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            InputStream is = null;
            try {
                URL url = new URL(uri.toString());
                if (height > 0 && width > 0) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    is = url.openStream();
                    BitmapFactory.decodeStream(is, new Rect(), options);

                    try {
                        is.close();
                    } catch (IOException e) {
                    }

                    // calculates the sample size
                    options.inSampleSize = calculateSampleSize(options, width, height);
                    options.inJustDecodeBounds = false;

                    // grab the bitmap in the requested size
                    is = url.openStream();
                    bitmap = BitmapFactory.decodeStream(is, new Rect(), options);
                } else {
                    // no size information, so we just grab the bitmap
                    // as is.
                    is = url.openStream();
                    bitmap = BitmapFactory.decodeStream(is);
                }

            } catch (Exception e) {
                error = e;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
            // Notify the callback
            if (bitmap != null) {
                callback.onLoaded(bitmap);
            } else if (error != null) {
                callback.onError(error);
            } else {

            }
        }
    }
}
