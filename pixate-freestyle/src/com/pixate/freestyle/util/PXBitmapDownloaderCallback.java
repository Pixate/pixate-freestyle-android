package com.pixate.freestyle.util;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * A callback that should be passed into the
 * {@link PXURLBitmapLoader#loadBitmap(Uri, PXBitmapDownloaderCallback)} method.
 * 
 * @author shalom
 */
public interface PXBitmapDownloaderCallback {

    /**
     * Called when a Bitmap was loaded.
     * 
     * @param bitmap
     */
    public void onBitmapLoaded(Bitmap bitmap);

    /**
     * Called on an error.
     * 
     * @param error
     */
    public void onError(Exception error);
}
