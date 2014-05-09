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
package com.pixate.freestyle.cg.shapes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.pixate.freestyle.PixateFreestyle;
import com.pixate.freestyle.cg.math.PXOffsets;
import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.parsing.PXTransformParser;
import com.pixate.freestyle.cg.shadow.PXShadowPaint;
import com.pixate.freestyle.cg.strokes.PXStrokeRenderer;
import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.ObjectUtil;

/**
 * A common base class for all shapes in ShapeKit. This class can be used to
 * cache the geometry of the shape it describes.
 */
public abstract class PXShape implements PXRenderable, PXPaintable {

    protected PXRenderable parent;
    protected PXShapeDocument owningDocument;

    protected Path path;
    protected PXStrokeRenderer stroke;
    protected PXPaint fillColor;
    protected float opacity;
    protected boolean visible;
    protected Matrix transform;
    protected PXShape clippingPath;
    protected PXShadowPaint shadow;
    protected PXOffsets padding;

    private Picture picture;

    /**
     * Constructs a PXShape
     */
    public PXShape() {
        fillColor = null;
        opacity = 1.0F;
        visible = true;
        transform = new Matrix();
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.shapes.PXPaintable#getStroke()
     */
    public PXStrokeRenderer getStroke() {
        return stroke;
    }

    /**
     * Indicate that this shape needs to be redrawn. This method is used to
     * indicate that this shape needs to be redrawn. This will effectively call
     * postInvalidate on the view that ultimately owns this shape; however, note
     * that shape geometry is cached. This will force the shape to be redrawn,
     * but it will not force the shape's geometry to be recalculated. If you
     * need to clear the cache and refresh, call clearPath.
     */
    public void setNeedsDisplay() {
        PXShapeDocument scene = getOwningDocument();
        if (scene != null && scene.getParentView() != null) {
            scene.getParentView().postInvalidate();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.cg.shapes.PXPaintable#setStroke(com.pixate.freestyle
     * .pxengine .shape.PXStrokeRenderer)
     */
    public void setStroke(PXStrokeRenderer stroke) {
        if (!ObjectUtil.areEqual(this.stroke, stroke)) {
            this.stroke = stroke;
            setNeedsDisplay();
        }
    }

    /**
     * Returns the opacity.
     * 
     * @return Opacity
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * Set an opacity.
     * 
     * @param opacity An opacity value between 0.0 to 1.0.
     */
    public void setOpacity(float opacity) {
        // clamp input to valid range
        opacity = Math.min(Math.max(0.0F, opacity), 1.0F);
        if (this.opacity != opacity) {
            this.opacity = opacity;
            setNeedsDisplay();
        }
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            setNeedsDisplay();
        }
    }

    public PXPaint getFillColor() {
        return fillColor;
    }

    public void setFillColor(PXPaint color) {
        if (!ObjectUtil.areEqual(this.fillColor, color)) {
            fillColor = color;
            setNeedsDisplay();
        }
    }

    public PXShape getClippingPath() {
        return clippingPath;
    }

    public void setClippingPath(PXShape clippingPath) {
        if (!ObjectUtil.areEqual(this.clippingPath, clippingPath)) {
            this.clippingPath = clippingPath;
            setNeedsDisplay();
        }
    }

    public PXOffsets getPadding() {
        return padding;
    }

    public void setPadding(PXOffsets padding) {
        if (!ObjectUtil.areEqual(this.padding, padding)) {
            this.padding = padding;
            setNeedsDisplay();
        }
    }

    public PXShadowPaint getShadow() {
        return shadow;
    }

    public void setShadow(PXShadowPaint shadow) {
        if (!ObjectUtil.areEqual(this.shadow, shadow)) {
            this.shadow = shadow;
            setNeedsDisplay();
        }
    }

    public PXRenderable getParent() {
        return parent;
    }

    public void setParent(PXRenderable parent) {
        if (this.parent != null && this.parent.equals(parent)) {
            return;
        }
        owningDocument = null;
        this.parent = parent;
    }

    /**
     * Returns the {@link PXShapeDocument} that owns this instance. <br>
     * This {@link PXShapeDocument} can be thought of as being analogous to the
     * W3C DOM Node#getDocument method.
     */
    public PXShapeDocument getOwningDocument() {
        if (owningDocument != null) {
            return owningDocument;
        }
        PXRenderable result = this;
        PXRenderable parent = result.getParent();
        while (parent != null) {
            result = parent;
            parent = parent.getParent();
        }
        if (result instanceof PXShapeDocument) {
            owningDocument = (PXShapeDocument) result;
            return owningDocument;
        }
        return null;
    }

    /**
     * Sets a {@link Path}.
     * 
     * @param path
     */
    public void setPath(Path path) {
        if (this.path != null) {
            clearPath();
        }
        this.path = path;
    }

    /**
     * Returns the path data associated with this shape instance. Note that when
     * this method is called , the {@link #newPath()} method may be called to
     * populate the path cache for the instance. This call may return
     * <code>null</code>, which indicates that the instance was unable to create
     * a path.
     */
    public Path getPath() {
        if (path == null) {
            path = newPath();
        }
        return path;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.cg.PXRenderable#getTransform()
     */
    public Matrix getTransform() {
        return transform;
    }

    public void setTransform(Matrix transform) {
        if (transform == null) {
            // set it to the identity transform to save us the trouble of null
            // checking all over.
            transform = PXTransformParser.IDENTITY_MATRIX;
        }
        if (!ObjectUtil.areEqual(transform, this.transform)) {
            this.transform = transform;
            setNeedsDisplay();
        }
    }

    /**
     * Clear the path cache. This method is used to clear the path cache
     * maintained by this shape. This can be useful in low memory situations,
     * but this is more likely to be used when some attribute of the shape has
     * changed which affects its geometry. At which point, the cache is
     * invalidated and will be rebuilt the next time the path property is
     * accessed.
     */
    public void clearPath() {
        if (path != null) {
            ObjectPool.pathPool.checkIn(path);
            path = null;
        }
        picture = null;
        setNeedsDisplay();
    }

    public Drawable renderToImage(RectF bounds, boolean opaque) {
        Drawable result = null;
        if (bounds != null && bounds.width() > 0 && bounds.height() > 0) {
            // Start new image context
            Bitmap bitmap = Bitmap.createBitmap((int) Math.ceil(bounds.width()),
                    (int) Math.ceil(bounds.height()), Bitmap.Config.ARGB_8888);
            // TODO - API 12 and above...
            bitmap.setHasAlpha(!opaque);
            bitmap.setDensity(PixateFreestyle.getAppContext().getResources().getDisplayMetrics().densityDpi);
            // create a Canvas context
            Canvas canvas = new Canvas(bitmap);

            // translate to bound's origin
            canvas.translate(-bounds.left, -bounds.top);

            // render this shape
            render(canvas);

            // return image as drawable
            result = new BitmapDrawable(PixateFreestyle.getAppContext().getResources(), bitmap);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.cg.shapes.PXRenderable#render(android.graphics.Canvas
     * )
     */
    public void render(Canvas context) {
        render(context, true);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.cg.shapes.PXRenderable#render(android.graphics.Canvas
     * , boolean)
     */
    public void render(Canvas context, boolean cache) {
        // TODO Auto-generated method stub
        // Don't draw if we're not visible
        if (!visible) {
            return;
        }
        // Using a Picture drawing with Hardware Acceleration on will not work,
        // so in case it's on, we use a direct rendering on the given context.
        // TODO - isHardwareAccelerated is API 11 and above
        if (cache && picture == null && !context.isHardwareAccelerated()) {
            // Create a Picture and record all drawings into it.
            // We will use this picture later to render the shape.
            picture = new Picture();
            Rect clipBounds = context.getClipBounds();
            innerRender(picture.beginRecording(clipBounds.width(), clipBounds.height()));
            picture.endRecording();
        }
        int saveCount = context.save();
        if (picture != null) {
            context.drawPicture(picture);
        } else {
            // direct draw (Cache is false, or Hardware Acceleration is on)
            innerRender(context);
        }
        context.restoreToCount(saveCount);
    }

    /**
     * A {@link PXShape} is asynchronous in case its inner {@link PXPaint} is.
     * 
     * @see com.pixate.freestyle.cg.shapes.PXRenderable#isAsynchronous()
     */
    @Override
    public boolean isAsynchronous() {
        return fillColor != null && fillColor.isAsynchronous();
    }

    /**
     * Inner render the canvas.
     * 
     * @param canvas
     */
    private void innerRender(Canvas canvas) {
        if (!transform.isIdentity()) {
            // apply transform
            canvas.concat(transform);
        }

        // apply clipping path, if we have one
        if (clippingPath != null) {
            canvas.clipPath(clippingPath.getPath());
        }

        // setup transparency layer
        int alphaSaveCount = -1;
        if (opacity < 1.0F) {
            int alpha = (int) (opacity * 255);
            alphaSaveCount = canvas.saveLayerAlpha(new RectF(canvas.getClipBounds()), alpha,
                    Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
        }

        // render content
        if (getPath() != null) {
            // Apply shadow outset
            if (shadow != null) {
                shadow.applyOutsetToPath(path, canvas);
            }
            // Set fill
            if (fillColor != null) {
                Paint fillPaint = ObjectPool.paintPool.checkOut();
                fillPaint.setStyle(Style.FILL);
                fillColor.applyFillToPath(path, fillPaint, canvas);
                // Check the paint back into the pool
                ObjectPool.paintPool.checkIn(fillPaint);
            }
            // Apply shadow insets
            if (shadow != null) {
                shadow.applyInsetToPath(path, canvas);
            }
            // Set stroke
            if (stroke != null) {
                // Passing a null paint. This will eventually be rendered as new
                // Paint with Paint.ANTI_ALIAS_FLAG.
                stroke.applyStrokeToPath(path, null, canvas);
            }
        }

        // Draw the children
        renderChildren(canvas);

        if (opacity < 1.0F) {
            canvas.restoreToCount(alphaSaveCount);
        }
        // Release the path after the rendering. Usually, this method will
        // render into a Picture, so no new Path will be created, unless a
        // complete redraw is performed after a Picture disposal.
        ObjectPool.pathPool.checkIn(path);
        path = null;
    }

    /**
     * Build's the {@link Path} that contains the geometry of this instance's
     * shape. Subclasses may override.
     * 
     * @return A {@link Path}
     */
    protected Path newPath() {
        return null;
    }

    /**
     * Render any children associated with this shape. This method is used to
     * render any child content associated with this shape. In most cases, this
     * will only be used by container classes, such as PXShapeGroup. Subclasses
     * may override.
     * 
     * @param context The context into which children of this shape should be
     *            rendered.
     */
    protected void renderChildren(Canvas context) {
        // No Op
    }
}
