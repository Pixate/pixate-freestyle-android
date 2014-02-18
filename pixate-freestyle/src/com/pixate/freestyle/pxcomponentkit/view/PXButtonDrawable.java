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
 * 
 */
package com.pixate.freestyle.pxcomponentkit.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.widget.Button;

import com.pixate.freestyle.cg.paints.PXLinearGradient;
import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.paints.PXSolidPaint;
import com.pixate.freestyle.cg.shapes.PXRectangle;
import com.pixate.freestyle.cg.shapes.PXShapeDocument;
import com.pixate.freestyle.cg.shapes.PXShapeGroup;
import com.pixate.freestyle.util.ObjectUtil;
import com.pixate.freestyle.util.PXColorUtil;

/**
 * A PX {@link Drawable} for rendering a {@link PXButton}. This drawable can be
 * assigned as a background {@link Drawable} for a {@link Button}.
 */
public class PXButtonDrawable extends PXSceneDrawable {

    private static final int DEFAULT_INSET_X = 2;
    private static final int DEFAULT_INSET_Y = 2;

    public static final String CORNER_RADIUS_KEY = "getCornerRadius";
    public static final String RADIUS_X_KEY = "radiusX";
    public static final String RADIUS_Y_KEY = "radiusY";

    private float _cornerRadius;
    private float _borderWidth;

    private PXRectangle background;
    private PXRectangle foreground;
    private SparseArray<PXPaint> foregroundPaints;
    private SparseArray<PXPaint> backgroundPaints;

    /**
     * Constructs a new PXButtonDrawable.
     */
    public PXButtonDrawable() {
        // Constructs with a null PXScene. The scene will be created on demand
        // when drawing.
        this(0, 0);
    }

    /**
     * Constructs a new PXButtonDrawable.
     * 
     * @param minHeight
     * @param minWidth
     */
    public PXButtonDrawable(int minHeight, int minWidth) {
        // Constructs with a null PXScene. The scene will be created on demand
        // when drawing.
        super(null, minHeight, minWidth);
        this._cornerRadius = 8;
        this._borderWidth = 1;
    }

    @Override
    public void draw(Canvas canvas) {

        loadScene();

        // update background
        Rect clipBounds = canvas.getClipBounds();
        background.setFillColor(getColorByState(backgroundPaints, getState()));
        background.setBounds(new RectF(clipBounds));
        background.setCornerRadius(_cornerRadius);

        // update foreground
        RectF forgroundBounds = new RectF(clipBounds);
        forgroundBounds.inset(DEFAULT_INSET_X, DEFAULT_INSET_Y);
        foreground.setFillColor(getColorByState(foregroundPaints, getState()));
        foreground.setBounds(forgroundBounds);
        foreground.setCornerRadius(_cornerRadius - _borderWidth);

        super.draw(canvas);
    }

    private static PXPaint getColorByState(SparseArray<PXPaint> paints, int[] state) {
        for (int i = state.length - 1; i >= 0; i--) {
            switch (state[i]) {
                case android.R.attr.state_enabled:
                case android.R.attr.state_pressed:
                case android.R.attr.state_empty:
                    return paints.get(state[i]);
            }
        }
        return null;
    }

    /**
     * Returns <code>true</code> for the {@link PXButtonDrawable}.
     * 
     * @see android.graphics.drawable.Drawable#isStateful()
     */
    @Override
    public boolean isStateful() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.view.BasePXShapeDrawable#onBoundsChange(android
     * .graphics.Rect)
     */
    @Override
    protected void onBoundsChange(Rect bounds) {
        scene = null;
        super.onBoundsChange(bounds);
    }

    @Override
    protected boolean onStateChange(int[] state) {
        scene = null;
        // FIXME - This is a temporary patch to a problem we have when setting
        // the drawable on a button, and the button does not visually react to
        // clicks. Calling button.setTextColor seems to effect the behavior, but
        // for now this will do.
        invalidateSelf();
        return true;
    }

    @Override
    public PXShapeDocument loadScene() {
        if (scene != null) {
            return scene;
        }
        // build fills
        if (foregroundPaints == null || backgroundPaints == null) {
            buildDefaultFills();
        }

        Rect clipBounds = getBounds();

        // create background rectangle
        background = new PXRectangle(new RectF(clipBounds));

        // create foreground rectangle
        Rect forgroundBounds = new Rect(clipBounds);
        forgroundBounds.inset(DEFAULT_INSET_X, DEFAULT_INSET_Y);
        foreground = new PXRectangle(new RectF(forgroundBounds));

        // build group of rectangles
        PXShapeGroup group = new PXShapeGroup();
        group.addShape(background);
        group.addShape(foreground);

        // build scene for group
        scene = new PXShapeDocument();
        scene.setShape(group);
        return scene;
    }

    private void buildDefaultFills() {
        // *** Foreground Paints
        // normal/selected
        PXLinearGradient gradient = new PXLinearGradient();
        gradient.addColor(Color.HSVToColor(255, new float[] { 0F, 0F, 1F }));
        gradient.addColor(Color.HSVToColor(255, new float[] { 0F, 0F, 0.63F })); // TODO
                                                                                 // withOffset:1
        setForegroundPaint(gradient, android.R.attr.state_enabled);

        // highlighted
        gradient = new PXLinearGradient();
        gradient.addColor(Color.HSVToColor(255, new float[] { 0F, 0F, 0.421F }));
        gradient.addColor(Color.HSVToColor(255, new float[] { 0F, 0F, 0.39F })); // TODO
                                                                                 // withOffset:1
        // TODO - test if this is equivalent to UIControlStateHighlighted
        setForegroundPaint(gradient, android.R.attr.state_pressed);

        // disabled
        gradient = new PXLinearGradient();
        gradient.addColor(Color.HSVToColor(255, new float[] { 0F, 0F, 0.5F }));
        gradient.addColor(Color.HSVToColor(255, new float[] { 0F, 0.63F, 0.5F })); // TODO
                                                                                   // withOffset:1
        // TODO - test if this can be used as equivalent to
        // UIControlStateDisabled
        setForegroundPaint(gradient, android.R.attr.state_empty);

        // *** Background Paints
        PXSolidPaint solid = new PXSolidPaint(Color.HSVToColor(255, new float[] { 0, 0, 0.734F }));
        setBackgroundPaint(solid, android.R.attr.state_enabled); // UIControlStateNormal
        setBackgroundPaint(solid, android.R.attr.state_pressed);
        setBackgroundPaint(solid, android.R.attr.state_empty);
    }

    public void setBackgroundPaint(PXPaint paint, int state) {
        if (backgroundPaints == null) {
            backgroundPaints = new SparseArray<PXPaint>(3);
        }
        PXPaint prevPaint = backgroundPaints.get(state);
        if (!ObjectUtil.areEqual(prevPaint, paint)) {
            backgroundPaints.put(state, paint);
            invalidateSelf();
        }
    }

    public void setForegroundPaint(PXPaint paint, int state) {
        if (foregroundPaints == null) {
            foregroundPaints = new SparseArray<PXPaint>(3);
        }
        PXPaint prevPaint = foregroundPaints.get(state);
        if (!ObjectUtil.areEqual(prevPaint, paint)) {
            foregroundPaints.put(state, paint);
            invalidateSelf();
        }
    }

    /**
     * @return the corner radius
     */
    public float getCornerRadius() {
        return _cornerRadius;
    }

    /**
     * @param cornerRadius the corner radius to set
     */
    public void setCornerRadius(float cornerRadius) {
        if (cornerRadius != this._cornerRadius) {
            this._cornerRadius = cornerRadius;
            this.scene = null;
            invalidateSelf();
        }
    }

    /**
     * @return the border width
     */
    public float getBorderWidth() {
        return _borderWidth;
    }

    /**
     * @param border width the border width to set
     */
    public void setBorderWidth(float borderWidth) {
        if (borderWidth != this._borderWidth) {
            this._borderWidth = borderWidth;
            this.scene = null;
            invalidateSelf();
        }
    }

    // Utility functions
    /**
     * Set tint color on a given {@link PXButton}
     * 
     * @param button
     * @param color
     */
    public static void setTintColor(PXButton button, int color) {
        setTintColor(button, (PXButtonDrawable) button.getBackground(), color);
    }

    /**
     * Set tint color on a given {@link Button}. This method will set an
     * instance of this {@link PXButtonDrawable} as the {@link Button}s
     * background drawable.
     * 
     * @param button A {@link Button}. In case the button does not have a
     *            {@link PXButtonDrawable} as a background, this method will
     *            attach one and theme it.
     * @param color
     */
    @SuppressWarnings("deprecation")
    public static void setTintColor(Button button, int color) {
        Drawable drawable = button.getBackground();
        if (drawable instanceof PXButtonDrawable) {
            setTintColor(button, (PXButtonDrawable) drawable, color);
        } else {
            Drawable prevDrawable = button.getBackground();
            PXButtonDrawable pxDrawable;
            if (prevDrawable != null) {
                pxDrawable = new PXButtonDrawable(prevDrawable.getMinimumHeight(),
                        prevDrawable.getMinimumWidth());
            } else {
                // TODO: API 16 supports getMinimunHeight and getMinimumWidth
                pxDrawable = new PXButtonDrawable(button.getHeight(), button.getWidth());
            }
            button.setBackgroundDrawable(pxDrawable);
            setTintColor(button, pxDrawable, color);
        }
    }

    /**
     * Set tint color on a given {@link Button}, using an instance of this
     * drawable and a color.
     * 
     * @param button
     * @param drawable
     * @param color
     */
    public static void setTintColor(Button button, PXButtonDrawable drawable, int color) {

        int alpha = Color.alpha(color);
        float saturationDelta = .06F;
        float lightnessDelta = .11F;

        float[] hsl = new float[3];
        PXColorUtil.colorToHsl(color, hsl);

        int bottomColor = PXColorUtil.hslToColor(alpha, hsl[0], hsl[1] - saturationDelta, hsl[2]
                - lightnessDelta);
        int topColor = PXColorUtil.hslToColor(alpha, hsl[0], hsl[1] + saturationDelta, hsl[2]
                + lightnessDelta);
        int borderColorTop = PXColorUtil.hslToColor(alpha, hsl[0], hsl[1] - saturationDelta, hsl[2]
                - lightnessDelta * 1.3F);
        int borderColorBottom = PXColorUtil.hslToColor(alpha, hsl[0], hsl[1] - saturationDelta,
                hsl[2] * .5F);

        // Force a new scene on the next redraw.
        drawable.resetScene();
        // Set the colors for the states.
        drawable.setBackgroundPaint(
                PXLinearGradient.gradientFromStartColor(borderColorTop, borderColorBottom),
                android.R.attr.state_enabled);
        drawable.setForegroundPaint(PXLinearGradient.gradientFromStartColor(topColor, bottomColor),
                android.R.attr.state_enabled);

        // set the button's text color
        if (hsl[2] < 0.5) {
            // Cannot use Color.WHITE. Once we use it, the button's clicking
            // stops working!...
            button.setTextColor(PXColorUtil.createColorStateList(Color.argb(255, 254, 254, 254)));
        } else {
            button.setTextColor(PXColorUtil.createColorStateList(Color.BLACK));
        }
    }
}
