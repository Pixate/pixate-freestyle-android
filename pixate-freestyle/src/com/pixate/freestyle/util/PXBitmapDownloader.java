package com.pixate.freestyle.util;

import java.io.IOException;

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
     * @param uri
     * @return A {@link Bitmap}. <code>null</code> in case of a problem.
     * @throws IOException
     */
    public Bitmap getBitmap(Uri uri) throws IOException;
}
