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

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.pixate.freestyle.util.ObjectUtil;

/**
 * A group of {@link PXStroke}s.
 */
public class PXStrokeGroup implements PXStrokeRenderer {

    private List<PXStrokeRenderer> strokes;

    /**
     * Adds a {@link PXStrokeRenderer} to the group.
     * 
     * @param stroke
     */
    public void addStroke(PXStrokeRenderer stroke) {
        if (stroke != null) {
            if (strokes == null) {
                strokes = new ArrayList<PXStrokeRenderer>(3);
            }
            strokes.add(stroke);
        }
    }

    /**
     * Removes a {@link PXStrokeRenderer}.
     * 
     * @param stroke
     */
    public void removeStroke(PXStrokeRenderer stroke) {
        if (strokes != null) {
            strokes.remove(stroke);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.strokes.PXStrokeRenderer#isOpaque()
     */
    public boolean isOpaque() {
        for (PXStrokeRenderer stroke : strokes) {
            if (!stroke.isOpaque()) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.cg.strokes.PXStrokeRenderer#applyStrokeToPath(android
     * .graphics.Path, android.graphics.Paint, android.graphics.Canvas)
     */
    public void applyStrokeToPath(Path path, Paint paint, Canvas context) {
        if (strokes != null) {
            for (PXStrokeRenderer stroke : strokes) {
                stroke.applyStrokeToPath(path, paint, context);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof PXStrokeGroup) {
            return ObjectUtil.areEqual(((PXStrokeGroup) o).strokes, this.strokes);
        }
        return false;
    }
}
