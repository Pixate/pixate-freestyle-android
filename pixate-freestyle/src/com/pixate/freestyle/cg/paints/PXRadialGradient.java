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
package com.pixate.freestyle.cg.paints;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;

import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.ObjectUtil;
import com.pixate.freestyle.util.PXColorUtil;
import com.pixate.freestyle.util.PXLog;

/**
 * Radial gradient representation.
 */
public class PXRadialGradient extends PXGradient {

    private static String TAG = PXRadialGradient.class.getSimpleName();

    protected PointF center;
    protected float radius;

    /**
     * Constructs a new radial gradient. The center point of this gradient is
     * (0, 0), and a radius of 0.
     */
    public PXRadialGradient() {
        center = new PointF();
    }

    /**
     * Returns the gradient's center point.
     * 
     * @return The center point.
     */
    public PointF getCenter() {
        return center;
    }

    /**
     * Sets the gradient's center point.
     * 
     * @param center
     */
    public void setCenter(PointF center) {
        this.center = center;
    }

    /**
     * Returns the radial gradient radius.
     * 
     * @return The radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets the radial gradient radius.
     * 
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.pxengine.cg.PXPaint#applyFillToPath(android.graphics
     * .Path, android.graphics.Paint, android.graphics.Canvas)
     */
    public void applyFillToPath(Path path, Paint paint, Canvas context) {
        context.save();
        // clip to path
        context.clipPath(path);

        // transform gradient space
        context.concat(transform);

        // do the gradient (note that we cannot really do a a radial gradient
        // with 2 center points, like in iOS).
        Rect bounds = new Rect();
        context.getClipBounds(bounds);
        Paint p = ObjectPool.paintPool.checkOut(paint);
        p.setAntiAlias(true);
        p.setShader(getGradient(bounds));
        // apply the blending mode
        p.setXfermode(blendingMode);
        // draw
        context.drawPaint(p);

        // restore coordinate system
        context.restore();
        // Check the paint back into the pool
        ObjectPool.paintPool.checkIn(p);
    }

    public PXPaint lightenByPercent(float percent) {
        PXRadialGradient result = createCopyWithoutColors();
        // copy and lighten colors
        for (int color : colors) {
            addColor(PXColorUtil.lightterByPercent(color, percent));
        }
        return result;
    }

    public PXPaint darkenByPercent(float percent) {

        PXRadialGradient result = createCopyWithoutColors();
        // copy and darken colors
        for (int color : colors) {
            addColor(PXColorUtil.lightterByPercent(color, percent));
        }
        return result;
    }

    private PXRadialGradient createCopyWithoutColors() {
        PXRadialGradient result = new PXRadialGradient();

        // copy properties
        result.setCenter(new PointF(center.x, center.y));
        result.setRadius(radius);

        // copy PXGradient properties, but not colors
        result.setTransform(new Matrix(transform));
        result.offsets = new ArrayList<Float>(offsets);

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.cg.PXGradient#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof PXRadialGradient && super.equals(other)) {
            PXRadialGradient gradient = (PXRadialGradient) other;
            return radius == gradient.radius && ObjectUtil.areEqual(center, gradient.center);
        }
        return false;
    }

    // Create and return the gradient.
    private RadialGradient getGradient(Rect bounds) {
        adjustGradientColors();
        int count = colors.size();
        int[] colors = new int[count];
        float[] positions = new float[count];
        for (int i = 0; i < count; i++) {
            colors[i] = this.colors.get(i);
            positions[i] = this.offsets.get(i);
        }
        try {
            float r;
            PointF c;
            if (radius == 0) {
                c = new PointF(bounds.exactCenterX(), bounds.exactCenterY());
                r = Math.min(bounds.width() * 0.5f, bounds.height() * 0.5f);
            } else if (gradientUnits == PXGradientUnits.USER_SPACE) {
                c = new PointF(center.x, center.y);
                r = radius;
            } else {
                // linear-gradient points are based on the shape's bbox, so grab
                // that
                Rect pathBounds = bounds;

                // grab the x,y offset which we will apply later
                int left = pathBounds.left;
                int top = pathBounds.top;

                // grab the positions within the bbox for each point
                float p1x = pathBounds.width() * center.x;
                float p1y = pathBounds.height() * center.y;

                // create final points by offsetting the bbox coordinates by the
                // bbox origin
                c = new PointF(left + p1x, top + p1y);

                // TODO: need rx and ry. Using width for both now
                r = pathBounds.width() * radius;
            }
            RadialGradient gradient = new RadialGradient(c.x, c.y, r, colors, positions,
                    TileMode.CLAMP);
            return gradient;
        } catch (Exception e) {
            if (PXLog.isLogging()) {
                PXLog.e(TAG, e, "Error while instantiating a RadialGradient");
            }
            return null;
        }
    }
}
