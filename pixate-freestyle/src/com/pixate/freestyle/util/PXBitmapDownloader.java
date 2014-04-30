package com.pixate.freestyle.util;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * An image downloader interface that is used when grabbing a {@link Bitmap}
 * through the {@link PXURLBitmapLoader}.
 * 
 * @author shalom
 */
public interface PXBitmapDownloader {

    /**
     * Returns the {@link Bitmap} downloaded from the given {@link Uri}.
     * 
     * @param uri The bitmap {@link Uri}
     * @param width The desired width of the {@link Bitmap}, or smaller than 0
     *            to not scale the bitmap.
     * @param height The desired height of the {@link Bitmap}, or smaller than 0
     *            to not scale the bitmap.
     * @param callback A callback that will be informed when the bitmap is
     *            loaded, or when an error occurred.
     * @param synchronous In case <code>true</code>, this call will block until
     *            the download is completed (or an error occurred).
     */
    public void downloadBitmap(Uri uri, int width, int height, LoadingCallback<Bitmap> callback,
            boolean synchronous);
}
