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

import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.ObjectUtil;

/**
 * PX representation of a line.
 */
public class PXLine extends PXShape {

    private PointF p1;
    private PointF p2;

    /**
     * Constructs a line with zero coordinates.
     */
    public PXLine() {
        p1 = new PointF();
        p2 = new PointF();
    }

    /**
     * Initializes a newly allocated line using the specified x and y locations
     * 
     * @param x1 The x coordinate of the start of the line
     * @param y1 The y coordinate of the start of the line
     * @param x2 The x coordinate of the end of the line
     * @param y2 The y coordinate of the end of the line
     */
    public PXLine(float x1, float y1, float x2, float y2) {
        p1 = new PointF(x1, y1);
        p2 = new PointF(x2, y2);
    }

    /**
     * Constructs a line, given its edge coordinates.<br>
     * Note that the given {@link PointF} instances will be copied.
     * 
     * @param p1
     * @param p2
     */
    public PXLine(PointF p1, PointF p2) {
        p1 = new PointF(p1.x, p1.y);
        p2 = new PointF(p2.x, p2.y);
    }

    /**
     * Returns a point indicating the location of the start of this line.
     */
    public PointF getP1() {
        return p1;
    }

    /**
     * Sets a point indicating the location of the start of this line.
     */
    public void setP1(PointF p1) {
        if (!ObjectUtil.areEqual(this.p1, p1)) {
            this.p1 = p1;
            clearPath();
        }
    }

    /**
     * Returns a point indicating the location of the end of this line.
     */
    public PointF getP2() {
        return p2;
    }

    /**
     * Sets a point indicating the location of the end of this line.
     */
    public void setP2(PointF p2) {
        if (!ObjectUtil.areEqual(this.p2, p2)) {
            this.p2 = p2;
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
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        return path;
    }
}
