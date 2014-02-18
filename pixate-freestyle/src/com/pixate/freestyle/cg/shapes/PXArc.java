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
import android.graphics.PointF;
import android.graphics.RectF;

import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.ObjectUtil;

/**
 * A PXShape subclass used to render arcs
 */
public class PXArc extends PXShape {

    /**
     * A point indicating the location of the center of this arc.
     */
    protected PointF center;
    protected float radius;
    protected float startingAngle;
    protected float endingAngle;

    /**
     * Constructs a new arc representation.
     */
    public PXArc() {
        this.center = new PointF();
        this.radius = 0.0f;
        this.startingAngle = 0.0f;
        this.endingAngle = 360.0f;
    }

    public void setCenter(PointF center) {
        if (!ObjectUtil.areEqual(this.center, center)) {
            this.center = center;
            clearPath();
        }
    }

    /**
     * Sets the value indicating the size of the radius of this arc. This value
     * may be negative, but it will be normalized to a positive value.
     * 
     * @param radius
     */
    public void setRadius(float radius) {
        // Use positive values only
        if (radius < 0.0f) {
            radius = -radius;
        }

        if (this.radius != radius) {
            this.radius = radius;
            clearPath();
        }
    }

    /**
     * Returns the value indicating the size of the radius of this arc. This
     * value may be negative, but it will be normalized to a positive value.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets the value indicating the starting angle for this arc
     * 
     * @param startingAngle
     */
    public void setStartingAngle(float startingAngle) {
        if (this.startingAngle != startingAngle) {
            this.startingAngle = startingAngle;
            clearPath();
        }
    }

    /**
     * Returns the value indicating the starting angle for this arc
     */
    public float getStartingAngle() {
        return startingAngle;
    }

    /**
     * Sets the value indicating the ending angle for this arc
     * 
     * @param endingAngle
     */
    public void setEndingAngle(float endingAngle) {
        if (this.endingAngle != endingAngle) {
            this.endingAngle = endingAngle;
            clearPath();
        }
    }

    /**
     * Returns the value indicating the ending angle for this arc
     */
    public float getEndingAngle() {
        return endingAngle;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.shapes.PXShape#newPath()
     */
    public Path newPath() {
        Path path = ObjectPool.pathPool.checkOut();
        // Android arc is defined differently than the iOS arc.
        // TODO - Test this visually. Not so sure if the given angles are good,
        // or need a conversion.
        RectF oval = new RectF(center.x - radius, center.y + radius, center.x + radius, center.y
                - radius);
        // For Android, we need to keep those in degrees, not radians.
        path.addArc(oval, startingAngle, endingAngle);
        return path;
    }
}
