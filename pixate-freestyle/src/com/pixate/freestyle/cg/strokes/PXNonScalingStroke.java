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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

public class PXNonScalingStroke extends PXStroke {

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.cg.strokes.PXStroke#applyStrokeToPath(android.graphics
     * .Path, android.graphics.Paint, android.graphics.Canvas)
     */
    @SuppressWarnings("deprecation")
    @Override
    public void applyStrokeToPath(Path path, Paint paint, Canvas context) {
        if (this.color != null && this.width > 0f) {
            //
            // | a b 0 |
            // | c d 0 |
            // | tx ty 1 |
            // TODO We'll probably need to get the matrix from the view.
            // Canvas.getMatrix() is deprecated since api 16, in favor of
            // getting the matrix directly from the view (View.getMatrix).
            // Until and unless we re-work this to get it from view,
            // suppress the deprecation warning.
            Matrix matrix = context.getMatrix();

            // ___________
            // sx = sqrt(a^2 + c^2)
            // ___________
            // sy = sqrt( b^2 + d^2)
            //
            float[] transform = new float[9];
            matrix.getValues(transform);
            float sx = (float) Math.sqrt(transform[0] * transform[0] + transform[3] * transform[3]);
            float sy = (float) Math.sqrt(transform[1] * transform[1] + transform[4] * transform[4]);

            // uses the largest scale, in case the scale is non-homogeneous
            float maxScale = Math.max(sx, sy);

            // save scale so we can restore it
            float originalWidth = this.width;

            // scale stroke width based on max scale we calculated above
            this.width /= maxScale;

            // render
            super.applyStrokeToPath(path, paint, context);

            // restore original stroke width
            this.width = originalWidth;
        }
    }
}
