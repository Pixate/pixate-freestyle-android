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

import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;

import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.ObjectUtil;
import com.pixate.freestyle.util.Size;

/**
 * A PXShape sub-class used to render rectangles.
 */
public class PXRectangle extends PXShape implements PXBoundable {

    private RectF bounds;

    /**
     * The radii of the top-left corner of this rectangle
     */
    protected Size radiusTopLeft;

    /**
     * The radii of the top-right corner of this rectangle
     */
    protected Size radiusTopRight;

    /**
     * The radii of the bottom-right corner of this rectangle
     */
    protected Size radiusBottomRight;

    /**
     * The radii of the bottom-left corner of this rectangle
     */
    protected Size radiusBottomLeft;

    /**
     * Constructs a new {@link PXRectangle}.
     * 
     * @param rect
     */
    public PXRectangle(RectF rect) {
        this.bounds = rect;
        setCornerRadii(Size.ZERO);
    }

    /**
     * Returns the rectangle's X.
     * 
     * @return The X
     */
    public float getX() {
        return bounds.right;
    }

    /**
     * Sets the rectangle's X.
     * 
     * @param x
     */
    public void setX(float x) {
        if (bounds.right != x) {
            bounds.right = x;
            clearPath();
        }
    }

    /**
     * Returns the rectangle's Y.
     * 
     * @return The Y
     */
    public float getY() {
        return bounds.top;
    }

    /**
     * Sets the rectangle's Y.
     * 
     * @param y
     */
    public void setY(float y) {
        if (bounds.top != y) {
            bounds.top = y;
            clearPath();
        }
    }

    /**
     * Returns the rectangle's width.
     * 
     * @return The width
     */
    public float getWidth() {
        return bounds.width();
    }

    /**
     * Sets the rectangle's width.
     * 
     * @param width
     */
    public void setWidth(float width) {
        if (bounds.width() != width) {
            bounds.right = bounds.left + width;
            clearPath();
        }
    }

    /**
     * Returns the rectangle's height.
     * 
     * @return The height
     */
    public float getHeight() {
        return bounds.height();
    }

    /**
     * Sets the rectangle's height.
     * 
     * @param height
     */
    public void setHeight(float height) {
        if (bounds.height() != height) {
            bounds.bottom = bounds.top + height;
            clearPath();
        }
    }

    //
    // /**
    // * Returns the rectangle's corner radius X.
    // *
    // * @return The radius on the X axis
    // */
    // public float getRadiusX() {
    // return radii.x;
    // }
    //
    // /**
    // * Sets the rectangle's corner radius X.
    // *
    // * @param x
    // */
    // public void setRadiusX(float x) {
    // if (radii.x != x) {
    // radii.x = x;
    // clearPath();
    // }
    // }
    //
    // /**
    // * Returns the rectangle's corner radius Y.
    // *
    // * @return The radius on the Y axis
    // */
    // public float getRadiusY() {
    // return radii.y;
    // }
    //
    // /**
    // * Sets the rectangle's corner radius Y.
    // *
    // * @param y
    // */
    // public void setRadiusY(float y) {
    // if (radii.y != y) {
    // radii.y = y;
    // clearPath();
    // }
    // }

    /**
     * Returns the bounds of this rectangle.
     * 
     * @return the bounds
     */
    public RectF getBounds() {
        return bounds;
    }

    /**
     * Sets the bounds of this rectangle.
     * 
     * @param bounds the bounds to set
     */
    public void setBounds(RectF bounds) {
        if (!ObjectUtil.areEqual(bounds, this.bounds)) {
            this.bounds = bounds;
            clearPath();
        }
    }

    public void setRadiusTopLeft(Size radiusTopLeft) {
        if (!ObjectUtil.areEqual(this.radiusTopLeft, radiusTopLeft)) {
            this.radiusTopLeft = radiusTopLeft;
            clearPath();
        }
    }

    public void setRadiusTopRight(Size radiusTopRight) {
        if (!ObjectUtil.areEqual(this.radiusTopRight, radiusTopRight)) {
            this.radiusTopRight = radiusTopRight;
            clearPath();
        }
    }

    public void setRadiusBottomRight(Size radiusBottomRight) {
        if (!ObjectUtil.areEqual(this.radiusBottomRight, radiusBottomRight)) {
            this.radiusBottomRight = radiusBottomRight;
            clearPath();
        }
    }

    public void setRadiusBottomLeft(Size radiusBottomLeft) {
        if (!ObjectUtil.areEqual(this.radiusBottomLeft, radiusBottomLeft)) {
            this.radiusBottomLeft = radiusBottomLeft;
            clearPath();
        }
    }

    public void setCornerRadius(float radius) {
        setCornerRadii(new Size(radius, radius));
    }

    public void setCornerRadii(Size radii) {
        radiusTopLeft = radii;
        radiusTopRight = radii;
        radiusBottomRight = radii;
        radiusBottomLeft = radii;
    }

    protected boolean hasRoundedCorners() {
        return !ObjectUtil.areEqual(this.radiusTopLeft, Size.ZERO)
                || !ObjectUtil.areEqual(this.radiusTopRight, Size.ZERO)
                || !ObjectUtil.areEqual(this.radiusBottomRight, Size.ZERO)
                || !ObjectUtil.areEqual(this.radiusBottomLeft, Size.ZERO);
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.cg.PXShape#newPath()
     */
    @Override
    protected Path newPath() {
        Path resultPath = ObjectPool.pathPool.checkOut();
        if (!hasRoundedCorners()) {
            resultPath.addRect(bounds, Direction.CW);
        } else {
            resultPath.addRoundRect(bounds, new float[] { radiusTopLeft.width,
                    radiusTopLeft.height, radiusTopRight.width, radiusTopRight.height,
                    radiusBottomRight.width, radiusBottomRight.height, radiusBottomLeft.width,
                    radiusBottomLeft.height }, Direction.CW);
        }
        return resultPath;
    }
}
