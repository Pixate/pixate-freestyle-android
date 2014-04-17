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
/**
 * Copyright (c) 2012-2013 Pixate, Inc. All rights reserved.
 */
package com.pixate.freestyle.cg.views;

import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import com.pixate.freestyle.cg.parsing.PXSVGLoader;
import com.pixate.freestyle.cg.shapes.PXShapeDocument;
import com.pixate.freestyle.util.ObjectUtil;
import com.pixate.freestyle.util.PXLog;

public class PXShapeView extends View {

    private static String TAG = PXShapeView.class.getSimpleName();

    private static final String SVG_RESOURCE_TYPE = "svg";
    private PXShapeDocument document;

    @SuppressWarnings("unused")
    private String resourcePath; // We set but never access this. Consider
                                 // deletion.

    private PointF shapeCenter;
    private PointF shapeTranslation;
    private PointF shapeScale;
    private float shapeRotation;
    private Matrix shapeTransform;

    // hold a Rect that will be re-used when drawing.
    private Rect bounds = new Rect();

    /**
     * Constructs a new PXShapeView.
     * 
     * @param context
     */
    public PXShapeView(Context context) {
        super(context);
        resetTransforms();
    }

    /**
     * Constructs a new PXShapeView.
     * 
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PXShapeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        resetTransforms();
    }

    /**
     * Constructs a new PXShapeView.
     * 
     * @param context
     * @param attrs
     */
    public PXShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        resetTransforms();
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
        if (resourcePath != null) {
            if (resourcePath.endsWith(SVG_RESOURCE_TYPE)) {
                loadDocument(resourcePath);
            } else {
                document = null;
            }
        } else {
            document = null;
        }
    }

    public void setResourceURL(String url) {
        this.resourcePath = url;
        if (url != null) {
            loadDocumentFromURL(url);
        }
    }

    public void setShapeCenter(PointF center) {
        if (!ObjectUtil.areEqual(center, this.shapeCenter)) {
            shapeCenter = center;
            applyTransform();
        }
    }

    public void setShapeTranslation(PointF translation) {
        if (!ObjectUtil.areEqual(translation, this.shapeTranslation)) {
            shapeTranslation = translation;
            applyTransform();
        }
    }

    public void setShapeScale(PointF scale) {
        if (shapeScale != scale) {
            shapeScale = scale;
            applyTransform();
        }
    }

    public void setDocument(PXShapeDocument document) {
        if (this.document != null) {
            this.document.setParentView(null);
        }
        this.document = document;
        if (this.document != null) {
            this.document.setParentView(this);
        }
        resetTransforms();
        applyBoundsToDocument();
    }

    public void setShapeRotation(float rotation) {
        if (shapeRotation != rotation) {
            shapeRotation = rotation;
            applyTransform();
        }
    }

    protected Matrix shapeTransform() {
        // TODO: consider caching to ivar if used frequently
        shapeTransform = new Matrix();

        shapeTransform.setTranslate(shapeCenter.x, shapeCenter.y);
        shapeTransform.setScale(shapeScale.x, shapeScale.y);
        shapeTransform.setRotate(shapeRotation);
        shapeTransform.setTranslate(-shapeCenter.x, -shapeCenter.y);
        shapeTransform.setTranslate(shapeTranslation.x, shapeTranslation.y);
        return shapeTransform;
    }

    protected void applyTransform() {
        document.setTransform(shapeTransform);
    }

    protected void resetTransforms() {
        shapeCenter = new PointF();
        shapeTranslation = new PointF();
        shapeScale = new PointF(1, 1);
        shapeRotation = 0f;
    }

    protected void loadDocument(String resourcePath) {
        try {
            document = PXSVGLoader.loadFromStream(getContext().getAssets().open(resourcePath));
        } catch (IOException e) {
            PXLog.e(TAG, e, "Error loading the document at " + resourcePath);
        }
    }

    protected void loadDocumentFromURL(String url) {
        try {
            document = PXSVGLoader.loadFromURL(Uri.parse(url));
        } catch (IOException e) {
            PXLog.e(TAG, e, "Error loading the document at " + url);
        }
    }

    protected void applyBoundsToDocument() {
        if (document != null) {
            // XXX Android only
            // TODO - need testing...
            if (shapeTransform != null) {
                RectF rect = new RectF(0, 0, (float) this.getWidth(), (float) getHeight());
                shapeTransform.mapRect(rect);
                rect.offset((float) this.getLeft(), (float) this.getTop());
                // in case the view is partially out of the screen
                if (getRight() < 0 || getLeft() > ((View) getParent()).getWidth()) {
                    rect = new RectF(getLeft(), getTop(), getRight(), getBottom());
                }
                document.setBounds(rect);
            } else {
                document.setBounds(new RectF(getLeft(), getTop(), getRight(), getBottom()));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getClipBounds(bounds);
        Picture picture = renderToImage(bounds);
        canvas.drawPicture(picture);
    }

    public Picture renderToImage(Canvas canvas) {
        if (canvas != null) {
            canvas.getClipBounds(bounds);
            return renderToImage(bounds);
        }
        return null;
    }

    public Picture renderToImage(Rect bounds) {
        if (document != null) {
            Picture image = new Picture();
            try {
                Canvas c = image.beginRecording(bounds.width(), bounds.height());
                document.render(c);
            } catch (Exception ioe) {
                PXLog.e(TAG, ioe, "Error rendering to image");
            } finally {
                image.endRecording();
            }
            return image;
        }
        return null;
    }
}
