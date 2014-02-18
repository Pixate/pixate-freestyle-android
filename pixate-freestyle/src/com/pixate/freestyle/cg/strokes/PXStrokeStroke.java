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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class PXStrokeStroke implements PXStrokeRenderer {

    private PXStroke strokeEffect;
    private PXStrokeRenderer strokeToApply;

    public PXStrokeStroke() {
    }

    public PXStrokeStroke(PXStroke strokeEffect, PXStrokeRenderer strokeToApply) {
        this.strokeEffect = strokeEffect;
        this.strokeToApply = strokeToApply;
    }

    public void applyStrokeToPath(Path path, Paint paint, Canvas context) {
        // iOS implementation has the stroke pattern in the Path. Android
        // also needs a Paint instance.
        if (strokeEffect != null && strokeToApply != null) {
            strokeToApply.applyStrokeToPath(path, paint, context);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.strokes.PXStrokeRenderer#isOpaque()
     */
    public boolean isOpaque() {
        return strokeToApply != null && strokeToApply.isOpaque();//
    }

    public PXStroke getStrokeEffect() {
        return strokeEffect;
    }

    public void setStrokeEffect(PXStroke strokeEffect) {
        this.strokeEffect = strokeEffect;
    }

    public PXStrokeRenderer getStrokeToApply() {
        return strokeToApply;
    }

    public void setStrokeToApply(PXStrokeRenderer strokeToApply) {
        this.strokeToApply = strokeToApply;
    }
}
