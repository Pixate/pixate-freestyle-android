package com.pixate.freestyle.util;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private static final PXBitmapDownloader DEFAULT_DOWNLOADER = new PXDefaultImageDownloader();
    private static PXURLBitmapLoader instance;
    private PXBitmapDownloader downloader;

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
    }

    /**
     * Loads and returns a {@link Bitmap} for the given {@link Uri}.
     * 
     * @param uri
     * @return A {@link Bitmap}
     * @throws IOException
     */
    public static Bitmap loadBitmap(Uri uri) throws IOException {
        PXURLBitmapLoader loader = getInstance();
        return loader.doLoad(uri);
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
     * @return A Bitmap
     * @throws IOException
     */
    private Bitmap doLoad(Uri uri) throws IOException {
        downloader.getBitmap(uri);
        return null;
    }

    /**
     * Default {@link Bitmap} downloader. No caching, or any clever stuff is
     * done here.
     */
    private static class PXDefaultImageDownloader implements PXBitmapDownloader {

        /*
         * (non-Javadoc)
         * @see
         * com.pixate.freestyle.util.PXBitmapDownloader#getBitmap(android.net
         * .Uri)
         */
        @Override
        public Bitmap getBitmap(Uri uri) throws IOException {
            return BitmapFactory.decodeStream(new URL(uri.toString()).openStream());
        }
    }
}
