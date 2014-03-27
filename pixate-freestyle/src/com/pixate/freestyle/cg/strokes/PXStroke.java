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
package com.pixate.freestyle.cg.strokes;

import java.util.Arrays;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;

import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.util.ObjectPool;

/**
 * A PX stroke representation.
 */
public class PXStroke implements PXStrokeRenderer {

    public enum PXStrokeType {
        CENTER,
        INNER,
        OUTER
    }

    protected PXStrokeType type;
    protected float width;
    protected PXPaint color;
    protected float[] dashArray;
    protected int dashOffset;
    protected Cap lineCap;
    protected Join lineJoin;
    protected float miterLimit;

    /**
     * Constructs a new PX stroke.
     */
    public PXStroke() {
        this.type = PXStrokeType.CENTER;
        this.width = 1.0f;
        this.dashOffset = 0;
        this.lineCap = Cap.BUTT;
        this.lineJoin = Join.MITER;
        this.miterLimit = 4.0f; // What is a reasonable default here?
    }

    public PXStroke(float width) {
        this();
        this.width = width;
    }

    /**
     * Returns a stroke {@link Paint}. Note that this method does not check into
     * the pool any Paint that may have been pulled from it. The responsibility
     * here is up to the caller.
     * 
     * @param p
     * @param useOriginal Indicate that the given {@link Paint} instance should
     *            be applied with the stroke information.
     * @return A {@link Paint} reference (a new Paint in case the useOriginal
     *         was false)
     */
    public Paint getStrokedPaint(Paint p, boolean useOriginal) {
        Paint paint = useOriginal ? p : ObjectPool.paintPool.checkOut(p);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(width);

        if (dashArray != null && dashArray.length > 0) {
            paint.setPathEffect(new DashPathEffect(dashArray, dashOffset));
        }

        paint.setStrokeCap(this.lineCap);
        paint.setStrokeJoin(this.lineJoin);
        paint.setStrokeMiter(this.miterLimit);
        return paint;
    }

    public boolean isOpaque() {
        return color != null && color.isOpaque();
    }

    public void applyStrokeToPath(Path path, Paint paint, Canvas context) {
        if (color != null && width > 0) {
            // stroke and possibly dash incoming path
            boolean paintCreated = false;
            if (paint == null) {
                paint = ObjectPool.paintPool.checkOut();
                paintCreated = true;
            }
            Paint p = getStrokedPaint(paint, paintCreated);
            // set up masking for inner/outer/center stroke
            if (type == PXStrokeType.INNER) {
                context.save();
                // clip to path
                context.clipPath(path);
            } else if (type == PXStrokeType.OUTER) {
                // TODO:
            }
            // else is center, so do nothing

            color.applyFillToPath(path, p, context);

            // reset environment
            if (type == PXStrokeType.INNER) {
                context.restore();
            } else if (type == PXStrokeType.OUTER) {
                // TODO: just move condition into above test
            }

            // Check the paint back into the pool. Make sure that we check in
            // only Paint instances that were pulled from the pool in this
            // method.
            if (paintCreated) {
                ObjectPool.paintPool.checkIn(paint);
            } else {
                ObjectPool.paintPool.checkIn(p);
            }
        }
    }

    /**
     * @return the type
     */
    public PXStrokeType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(PXStrokeType type) {
        this.type = type;
    }

    /**
     * @return the color
     */
    public PXPaint getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(PXPaint color) {
        this.color = color;
    }

    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * @return the dashArray
     */
    public float[] getDashArray() {
        return dashArray;
    }

    /**
     * @param dashArray the dashArray to set
     */
    public void setDashArray(float[] dashArray) {
        this.dashArray = dashArray;
    }

    /**
     * @return the dashOffset
     */
    public int getDashOffset() {
        return dashOffset;
    }

    /**
     * @param dashOffset the dashOffset to set
     */
    public void setDashOffset(int dashOffset) {
        this.dashOffset = dashOffset;
    }

    /**
     * @return the lineCap
     */
    public Cap getLineCap() {
        return lineCap;
    }

    /**
     * @param lineCap the lineCap to set
     */
    public void setLineCap(Cap lineCap) {
        this.lineCap = lineCap;
    }

    /**
     * @return the lineJoin
     */
    public Join getLineJoin() {
        return lineJoin;
    }

    /**
     * @param lineJoin the lineJoin to set
     */
    public void setLineJoin(Join lineJoin) {
        this.lineJoin = lineJoin;
    }

    /**
     * @return the miterLimit
     */
    public float getMiterLimit() {
        return miterLimit;
    }

    /**
     * @param miterLimit the miterLimit to set
     */
    public void setMiterLimit(float miterLimit) {
        this.miterLimit = miterLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PXStroke) {
            PXStroke other = (PXStroke) o;
            // @formatter:off
			return (this.type == other.type)
					&&  (this.color == other.color)
					&&  (this.width == other.width)
					&&  Arrays.equals(this.dashArray ,other.dashArray)
					&&  (this.dashOffset == other.dashOffset)
					&&  (this.lineCap == other.lineCap)
					&&  (this.lineJoin == other.lineJoin)
					&&  (this.miterLimit == other.miterLimit);
			// @formatter:on
        }
        return false;
    }
}
