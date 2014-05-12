/*******************************************************************************
 * Copyright 2012-present Pixate, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.pixate.freestyle.cg.paints;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;

import com.pixate.freestyle.PixateFreestyle;
import com.pixate.freestyle.cg.parsing.PXSVGLoader;
import com.pixate.freestyle.cg.shapes.PXShapeDocument;
import com.pixate.freestyle.pxcomponentkit.view.overlay.PXBorderOverlay;
import com.pixate.freestyle.util.LoadingCallback;
import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.PXURLBitmapLoader;
import com.pixate.freestyle.util.StringUtil;
import com.pixate.freestyle.util.UrlStreamOpener;

public class PXImagePaint extends BasePXPaint {
    private static final String TAG = PXImagePaint.class.getSimpleName();
    private static final Set<String> SUPPORTED_REMOTE_SCHEMES = new HashSet<String>(Arrays.asList(
            "http", "https"));

    public enum PXImageRepeatType {
        REPEAT,
        SPACE,
        ROUND,
        NOREPEAT
    };

    private Uri imageURL;
    private RemoteLoader<Bitmap> remoteBitmapLoader;
    private RemoteLoader<PXShapeDocument> remoteSVGLoader;

    public PXImagePaint(Uri imageURL) {
        this.imageURL = imageURL;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#isOpaque()
     */
    public boolean isOpaque() {
        // TODO
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#isAsynchronous()
     */
    @Override
    public boolean isAsynchronous() {
        return isRemote();
    }

    public boolean hasSVGImageURL() {
        if (imageURL == null) {
            return false;
        }
        String file = imageURL.getPath();
        String scheme = imageURL.getScheme();
        String resourceSpecifier = imageURL.getEncodedSchemeSpecificPart();
        return !StringUtil.isEmpty(file) && file.toLowerCase(Locale.US).endsWith(".svg")
                || !StringUtil.isEmpty(scheme) && !StringUtil.isEmpty(resourceSpecifier)
                && UrlStreamOpener.DATA_SCHEME.startsWith(scheme.toLowerCase(Locale.US))
                && resourceSpecifier.toLowerCase(Locale.US).startsWith("image/svg+xml");
    }

    public Uri getImageUrl() {
        return imageURL;
    }

    /**
     * Returns a {@link Picture} instance with the loaded {@link Bitmap} or SVG
     * graphics in it.
     * 
     * @param bounds
     * @return A {@link Picture}
     */
    public Picture imageForBounds(Rect bounds) {
        Picture image = null;
        if (imageURL != null) {
            initRemoteLoader(bounds);
            // create image
            try {
                image = new Picture();
                Canvas canvas = image.beginRecording(bounds.width(), bounds.height());
                if (hasSVGImageURL()) {
                    // for android, instead of using the PXShapeView (which
                    // requires a Context), we directly load the scene with
                    // PXSVGLoader.loadFromURL(URL)
                    PXShapeDocument document;
                    if (remoteSVGLoader != null) {
                        document = (PXShapeDocument) remoteSVGLoader.get();
                    } else {
                        document = PXSVGLoader.loadFromStream(UrlStreamOpener.open(imageURL));
                    }
                    document.setBounds(new RectF(bounds));
                    document.render(canvas);
                } else {
                    // read the data as a bitmap image
                    InputStream inputStream = null;
                    try {
                        Drawable d = null;
                        if (remoteBitmapLoader != null) {
                            d = getDrawable(remoteBitmapLoader.get());
                        } else {
                            inputStream = UrlStreamOpener.open(imageURL);
                            // Try to load this data as a NinePatchDrawable. The
                            // fallback here, in case the bitmap is not
                            // nine-patch, is BitmapDrawable. Also, when the png
                            // is loaded from the assets directory, we need to
                            // compile/encode it via the "aapt" tool first!
                            // Otherwise, it will not load the nine-patch chunk
                            // data.
                            d = NinePatchDrawable.createFromStream(inputStream, null);
                        }
                        if (d == null) {
                            d = new PXBorderOverlay(Color.RED, 2);
                        }
                        d.setBounds(bounds);
                        d.draw(canvas);
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    }
                }
            } catch (Exception e) {
                PXLog.e(TAG, e, "Error loading a PXImagePaint from " + imageURL);
            } finally {
                image.endRecording();
            }
        }
        return image;
    }

    private Drawable getDrawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Drawable d;
        byte[] chunk = bitmap.getNinePatchChunk();
        boolean isNinePatch = NinePatch.isNinePatchChunk(chunk);
        if (isNinePatch) {
            d = new NinePatchDrawable(PixateFreestyle.getAppContext().getResources(), bitmap,
                    chunk, new Rect(), null);
        } else {
            d = new BitmapDrawable(PixateFreestyle.getAppContext().getResources(), bitmap);
        }
        return d;
    }

    /**
     * Initialize the remote bitmap loader with the dimensions of the bitmap we
     * would like to load.
     * 
     * @param bounds
     */
    private void initRemoteLoader(final Rect bounds) {
        // TODO - We assume here that this PXImagePaint will only download the
        // bitmap for the first Rect bounds it gets. In case a different request
        // will arrive later, the same bitmap will be used eventually. We may
        // want to change this...

        if (isRemote()) {

            // Start a task to load the image.
            // Note that this requires INTERNET permissions in the
            // manifest.
            // <uses-permission android:name="android.permission.INTERNET"/>

            if (remoteSVGLoader == null && hasSVGImageURL()) {
                // Prepare and start a remote SVG
                remoteSVGLoader = new RemoteLoader<PXShapeDocument>() {

                    protected PXShapeDocument doLoad(Uri uri) {
                        // load SVG (TODO Eventually we'll need a callback
                        // here too)
                        try {
                            return PXSVGLoader.loadFromStream(UrlStreamOpener.open(uri.toString()));
                        } catch (Exception e) {
                            PXLog.e(TAG, e, "Error while loading remote SVG " + uri);
                        }
                        // TODO - Return an SVG 'error' shape.
                        return null;
                    }

                };
                remoteSVGLoader.execute(imageURL);
            } else {
                // Prepare and start a remote Bitmap loader
                remoteBitmapLoader = new RemoteLoader<Bitmap>() {
                    @Override
                    protected Bitmap doLoad(Uri uri) {
                        try {
                            // load bitmap
                            int width = 0;
                            int height = 0;
                            if (bounds != null) {
                                width = bounds.width();
                                height = bounds.height();
                            }
                            // Note - although we are using a callback
                            // mechanism here, we are still forcing a
                            // synchronous mode.
                            final Bitmap[] result = new Bitmap[1];
                            PXURLBitmapLoader.loadBitmap(uri, width, height,
                                    new LoadingCallback<Bitmap>() {

                                        @Override
                                        public void onError(Exception error) {
                                            PXLog.e(TAG, error, "Error while downloading a bitmap");
                                        }

                                        @Override
                                        public void onLoaded(Bitmap bm) {
                                            result[0] = bm;
                                        }
                                    }, true);
                            return result[0];
                        } catch (Exception e) {
                            PXLog.e(TAG, e, "Error while loading remote image " + uri);
                        }
                        return null;
                    }
                };
                remoteBitmapLoader.execute(imageURL);
            }
        }
    }

    /**
     * Returns <code>true</code> if this instance have an image {@link Uri} that
     * points to an http or https location.
     * 
     * @return <code>true</code> if this image-paint is pointing to a remote
     *         location.
     */
    public boolean isRemote() {
        if (imageURL != null) {
            String scheme = imageURL.getScheme();
            return (scheme != null && SUPPORTED_REMOTE_SCHEMES.contains(scheme
                    .toLowerCase(Locale.US)));
        }
        return false;
    }

    public void applyFillToPath(Path path, Paint paint, Canvas context) {
        context.save();
        // clip to path
        context.clipPath(path);
        // do the gradient
        Rect bounds = new Rect();
        context.getClipBounds(bounds);
        Picture image = imageForBounds(bounds);
        // draw
        if (image != null) {
            // TODO - Blending mode? We may need to convert the Picture to a
            // Bitmap and then call drawBitmap
            context.drawPicture(image);
        }
        context.restore();
    }

    public PXPaint lightenByPercent(float percent) {
        // TODO
        return this;
    }

    public PXPaint darkenByPercent(float percent) {
        // TODO
        return this;
    }

    /**
     * A wrapper loader that can execute as an {@link AsyncTask} when initiated
     * from the UI thread, or execute synchronously when running from a non-UI
     * thread.
     * 
     * @param <Params>
     * @param <Progress>
     * @param <Result>
     */
    private abstract class RemoteLoader<R> {
        private AsyncTask<Uri, Void, R> task;
        private Uri uri;

        /**
         * Call this execute to initiate the {@link Uri} an the an internal
         * {@link AsyncTask} in case called from the UI thread.
         * 
         * @param uri
         */
        public void execute(Uri uri) {
            this.uri = uri;
            if (Looper.getMainLooper() == Looper.myLooper()) {
                // The loader was constructed at the UI thread.
                task = new AsyncTask<Uri, Void, R>() {
                    @Override
                    protected R doInBackground(Uri... params) {
                        return doLoad(params[0]);
                    }
                };
                task.execute(uri);
            }
        }

        /**
         * Returns the loading result. This method will block until a result is
         * received.
         * 
         * @return The loading result (can be <code>null</code> in case of an
         *         error)
         */
        public R get() {
            if (task != null) {
                try {
                    return task.get();
                } catch (Exception e) {
                    PXLog.e(TAG, e, "Error loading an image/svg ('%s')", uri);
                    return null;
                }
            }
            // synchronous loading
            return doLoad(uri);
        }

        /**
         * Do the actual result loading.
         * 
         * @param uri
         * @return The loading result.
         */
        protected abstract R doLoad(Uri uri);

    }
}
