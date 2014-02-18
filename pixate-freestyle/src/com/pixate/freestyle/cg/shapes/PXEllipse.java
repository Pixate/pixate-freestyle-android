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
import android.graphics.PointF;
import android.graphics.RectF;

import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.ObjectUtil;

/**
 * PX representation of a ellipse.
 */
public class PXEllipse extends PXShape implements PXBoundable {

    private PointF center;
    private float radiusX;
    private float radiusY;

    /**
     * Constructs an ellipse, given its center point, x-radius and y-radius.
     */
    public PXEllipse(PointF center, float radiusX, float radiusY) {
        this.center = center;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
    }

    /**
     * Constructs an ellipse.
     */
    public PXEllipse() {
        this.center = new PointF();
    }

    public RectF getBounds() {
        return new RectF(center.x - radiusX, center.y - radiusY, center.x + radiusX, center.y
                + radiusY);
    }

    public void setBounds(RectF bounds) {
        radiusX = bounds.centerX();
        radiusY = bounds.centerY();
        center.x = bounds.left + radiusX;
        center.y = bounds.top + radiusY;
        clearPath();
    }

    public void setCenter(PointF center) {
        if (!ObjectUtil.areEqual(this.center, center)) {
            this.center = center;
            clearPath();
        }
    }

    public void setRadiusX(float radius) {
        // Use positive absolute values only
        if (radius < 0) {
            radius = -radius;
        }

        if (this.radiusX != radius) {
            this.radiusX = radius;
            clearPath();
        }
    }

    public void setRadiusY(float radius) {
        // Use positive absolute values only
        if (radius < 0) {
            radius = -radius;
        }

        if (this.radiusY != radius) {
            this.radiusY = radius;
            clearPath();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.cg.PXShape#newPath()
     */
    @Override
    protected Path newPath() {
        Path path = ObjectPool.pathPool.checkOut();
        RectF rect = new RectF(center.x - radiusX, center.y - radiusY, center.x + radiusX, center.y
                + radiusY);
        path.addOval(rect, Direction.CW);
        return path;
    }
}
