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

import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.ObjectUtil;

/**
 * PX representation of a circle.
 */
public class PXCircle extends PXShape {

    private PointF center;
    private float radius;

    /**
     * Constructs a circle, given its center point and radius.
     */
    public PXCircle(PointF center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    /**
     * Sets the center of the circle.
     * 
     * @param center
     */
    public void setCenter(PointF center) {
        if (!ObjectUtil.areEqual(center, this.center)) {
            this.center = center;
            clearPath();
        }
    }

    /**
     * Sets the radius of the circle.
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

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.cg.PXShape#newPath()
     */
    @Override
    protected Path newPath() {
        Path path = ObjectPool.pathPool.checkOut();
        path.addCircle(center.x, center.y, radius, Direction.CW);
        return path;
    }
}
