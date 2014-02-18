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
package com.pixate.freestyle.cg.shadow;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;

import com.pixate.freestyle.util.Size;

// TODO very incomplete implementation.
public class PXShadow implements PXShadowPaint {

    @SuppressWarnings("unused")
    private PorterDuff.Mode blendMode;
    private boolean inset;
    private float horizontalOffset;
    private float verticalOffset;
    private float blurDistance;
    private float spreadDistance;
    private int color = Integer.MIN_VALUE;

    public PXShadow() {
        blendMode = Mode.SRC_OVER;
    }

    // Overrides

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (inset) {
            sb.append("inset ");
        }

        sb.append(String.format("%f ", horizontalOffset));
        sb.append(String.format("%f ", verticalOffset));
        sb.append(String.format("%f ", blurDistance));
        sb.append(String.format("%f ", spreadDistance));
        sb.append("rgb(");
        sb.append(Color.red(color));
        sb.append(",");
        sb.append(Color.green(color));
        sb.append(",");
        sb.append(Color.blue(color));
        sb.append(")");
        return sb.toString();
    }

    public void applyOutsetToPath(Path path, Canvas context) {
        if (!inset) {
            @SuppressWarnings("unused")
            Size offset = new Size(horizontalOffset, verticalOffset);

            if (color != Integer.MIN_VALUE) {
                // TODO - Obj-C:
                // CGContextSetShadowWithColor(context, offset, _blurDistance,
                // _color.CGColor);
            } else {
                // TODO - Obj-C:
                // CGContextSetShadow(context, offset, _blurDistance);
            }

            // TODO - Obj-C:
            // CGContextAddPath(context, path);

            // // set blending mode
            // CGContextSetBlendMode(context, self.blendMode);

            // CGContextFillPath(context);
        }
    }

    public void applyInsetToPath(Path path, Canvas context) {
        if (inset) {

            // TODO see Obj-C
        }

    }

    // Getters

    public boolean isInset() {
        return inset;
    }

    public int getColor() {
        return color;
    }

    public float getHorizontalOffset() {
        return horizontalOffset;
    }

    public float getVerticalOffset() {
        return verticalOffset;
    }

   // Setters

    public void setIsInset(boolean inset) {
        this.inset = inset;
    }

    public void setHorizontalOffset(float value) {
        horizontalOffset = value;
    }

    public void setVerticalOffset(float value) {
        verticalOffset = value;
    }

    public void setColor(int value) {
        color = value;
    }

    public void setSpreadDistance(float value) {
        spreadDistance = value;
    }

    public void setBlurDistance(float value) {
        blurDistance = value;
    }

}
