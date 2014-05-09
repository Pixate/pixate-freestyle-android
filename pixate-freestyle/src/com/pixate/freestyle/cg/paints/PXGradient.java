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
 * Copyright (c) 2012 Pixate, Inc. All rights reserved.
 */
package com.pixate.freestyle.cg.paints;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.Matrix;

import com.pixate.freestyle.cg.parsing.PXTransformParser;
import com.pixate.freestyle.util.ObjectUtil;

/**
 * Base PX gradient representation.
 */
public abstract class PXGradient extends BasePXPaint {

    public enum PXGradientUnits {
        BOUNDING_BOX,
        USER_SPACE
    };

    protected List<Integer> colors;
    protected List<Float> offsets;
    protected Matrix transform;
    protected PXGradientUnits gradientUnits;

    /**
     * Constructs a default gradient.
     */
    public PXGradient() {
        colors = new ArrayList<Integer>(3);
        offsets = new ArrayList<Float>(3);
        transform = new Matrix();
        gradientUnits = PXGradientUnits.BOUNDING_BOX;
    }

    /**
     * Adds an offset to the gradient.
     * 
     * @param offset
     */
    public void addOffset(float offset) {
        offsets.add(offset);
    }

    /**
     * Sets the gradient units. (e.g. if points for the gradient are in user
     * coordinates of if they are relative to the bounding box)
     */
    public void setGradientUnits(PXGradientUnits gradientUnits) {
        this.gradientUnits = gradientUnits;
    }

    /**
     * Returns the gradient units specification. (e.g. if points for the
     * gradient are in user coordinates of if they are relative to the bounding
     * box)
     */
    public PXGradientUnits getGradientUnits() {
        return gradientUnits;
    }

    /**
     * Sets the {@link Matrix} transform.
     * 
     * @param transform
     */
    public void setTransform(Matrix transform) {
        // set it to the identity transform to save us the trouble of null
        // checks all over.
        this.transform = (transform == null) ? PXTransformParser.IDENTITY_MATRIX : transform;
    }

    /**
     * Adds a color to the gradient.
     * 
     * @param color
     */
    public void addColor(int color) {
        colors.add(color);
    }

    /**
     * Adds a color and an offset. In case the offset already exists, the
     * assigned color for the offset is replaced with the new color.
     * 
     * @param color
     * @param offset
     */
    public void addColor(int color, float offset) {
        int index = -1;

        for (int i = 0; i < offsets.size(); i++) {
            if (offsets.get(i) == offset) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            colors.add(color);
            offsets.add(offset);
        } else {
            colors.set(index, color);
            offsets.set(index, offset);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#isOpaque()
     */
    public boolean isOpaque() {
        for (int color : colors) {
            if (Color.alpha(color) != 255) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#isAsynchronous()
     */
    @Override
    public boolean isAsynchronous() {
        return false;
    }

    /**
     * if color count and offset count don't match, then evenly distribute all
     * colors from 0 to 1
     */
    protected void adjustGradientColors() {

        int colorsSize = colors.size();
        if (colorsSize != offsets.size()) {
            offsets.clear();

            for (int i = 0; i < colorsSize; i++) {
                offsets.add(((float) i) / (colorsSize - 1));
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other != null && other instanceof PXGradient) {
            PXGradient gradient = (PXGradient) other;
            return ObjectUtil.areEqual(blendingMode, gradient.blendingMode)
                    && ObjectUtil.areEqual(transform, gradient.transform)
                    && offsets.equals(gradient.offsets) && colors.equals(gradient.colors);
        }
        return false;
    }
}
