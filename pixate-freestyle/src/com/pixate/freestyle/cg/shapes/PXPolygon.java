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

import java.util.Arrays;

import android.graphics.Path;
import android.graphics.PointF;

import com.pixate.freestyle.util.ObjectPool;

/**
 * A PXShape sub-class used to render open and closed polygons
 */
public class PXPolygon extends PXShape {

    private boolean closed;
    private PointF[] points;

    /**
     * Initializes a newly allocated polygon with no points.
     */
    public PXPolygon() {
    }

    /**
     * Initializes a newly allocated polygon using the specified list of points.
     * 
     * @param points The list of points describing the shape of this polygon
     */
    public PXPolygon(PointF[] points) {
        this.points = points;
    }

    /**
     * Sets the array of points describing the shape of this polygon.
     * 
     * @param points
     */
    public void setPoints(PointF[] points) {
        if (!Arrays.equals(this.points, points)) {
            this.points = points;
            clearPath();
        }
    }

    /**
     * Returns the array of points describing the shape of this polygon.
     */
    protected PointF[] getPoints() {
        return points;
    }

    /**
     * Sets the polygon's closed state. When <code>true</code>, the final point
     * of this polygon will automatically be joined to the first it's first
     * point. If you do not close this polygon and instead duplicate the first
     * point as the last, you will not get a clean connection at the start
     * point.
     * 
     * @param closed
     * @see #isClosed()
     */
    public void setClosed(boolean closed) {
        if (this.closed != closed) {
            this.closed = closed;
            clearPath();
        }
    }

    /**
     * Indicating whether this polygon should be closed or not. When
     * <code>true</code>, the final point of this polygon will automatically be
     * joined to the first it's first point. If you do not close this polygon
     * and instead duplicate the first point as the last, you will not get a
     * clean connection at the start point.
     */
    public boolean isClosed() {
        return closed;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.cg.PXShape#newPath()
     */
    @Override
    protected Path newPath() {
        Path resultPath = ObjectPool.pathPool.checkOut();
        if (points != null && points.length > 1) {
            PointF p = points[0];
            resultPath.moveTo(p.x, p.y);
            for (int i = 1; i < points.length; i++) {
                resultPath.lineTo(points[i].x, points[i].y);
            }
            if (closed) {
                resultPath.close();
            }
        }
        return resultPath;
    }
}
