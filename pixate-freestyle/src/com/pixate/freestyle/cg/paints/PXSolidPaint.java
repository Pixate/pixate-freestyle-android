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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.ObjectUtil;
import com.pixate.freestyle.util.PXColorUtil;

/**
 * PX solid paint representation.
 */
public class PXSolidPaint extends BasePXPaint {

    private int color;

    /**
     * Constructs a new solid black paint.
     */
    public PXSolidPaint() {
        this.color = Color.BLACK;
    }

    /**
     * Constructs a new solid color representation.
     * 
     * @param color The color data (alpha, r, g, b)
     */
    public PXSolidPaint(int color) {
        this.color = color;
    }

    /**
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(int color) {
        this.color = color;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#isOpaque()
     */
    public boolean isOpaque() {
        return Color.alpha(color) == 255;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#isAsynchronous()
     */
    @Override
    public boolean isAsynchronous() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.pxengine.PXPaint#applyFillToPath(android.graphics
     * .Path, android.graphics.Paint, android.graphics.Canvas)
     */
    public void applyFillToPath(Path path, Paint paint, Canvas context) {
        Paint p = ObjectPool.paintPool.checkOut(paint);
        p.setAntiAlias(true);
        p.setColor(color);
        p.setXfermode(blendingMode);
        context.drawPath(path, p);
        // Check the paint back into the pool
        ObjectPool.paintPool.checkIn(p);
    }

    /**
     * Create a solid paint with a given color.
     * 
     * @param color
     * @return A new instance of PXSolidPaint.
     */
    public static PXPaint createPaintWithColor(int color) {
        return new PXSolidPaint(color);
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#lightenByPercent(float)
     */
    public PXPaint lightenByPercent(float percent) {
        return new PXSolidPaint(PXColorUtil.lightterByPercent(color, percent));
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#darkenByPercent(float)
     */
    public PXPaint darkenByPercent(float percent) {
        return new PXSolidPaint(PXColorUtil.darkenByPercent(color, percent));
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof PXSolidPaint) {
            PXSolidPaint otherPaint = (PXSolidPaint) other;
            return color == otherPaint.color
                    && ObjectUtil.areEqual(blendingMode, otherPaint.blendingMode);
        }
        return false;
    }
}
