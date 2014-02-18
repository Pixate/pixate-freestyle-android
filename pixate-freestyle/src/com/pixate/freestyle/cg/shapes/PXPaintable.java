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

import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.shadow.PXShadowPaint;
import com.pixate.freestyle.cg.strokes.PXStrokeRenderer;

/**
 * The PXPaintable interface declares properties needed when rendering content
 * to a CGContext.
 */
public interface PXPaintable {

    /**
     * Returns a PXStrokeRenderer. This renderer is used to paint the outline of
     * a contour
     */
    PXStrokeRenderer getStroke();

    /**
     * Returns a PXPaint fill color. This renderer is used to paint the interior
     * of a contour
     */
    PXPaint getFillColor();

    /**
     * Returns the opacity value of a shape. This value is in the closed
     * interval [0,1] where 0 is transparent and 1 is opaque.
     */
    float getOpacity();

    /**
     * Indicating if this shape is visible or not.
     */
    boolean getVisible();

    /**
     * Returns a PXShape to be used as a clipping path on this shape
     */
    PXShape getClippingPath();

    /**
     * Returns a {@link PXShadowPaint} to be used to case inner and outer
     * shadows
     */
    PXShadowPaint getShadow();

    /**
     * Sets a PXStrokeRenderer. This renderer is used to paint the outline of a
     * contour
     */
    void setStroke(PXStrokeRenderer stroke);

    /**
     * Sets a PXPaint fill color. This renderer is used to paint the interior of
     * a contour
     */
    void setFillColor(PXPaint color);

    /**
     * Sets the opacity value of a shape. This value is in the closed interval
     * [0,1] where 0 is transparent and 1 is opaque.
     */
    void setOpacity(float opacity);

    /**
     * Sets if this shape is visible or not.
     */
    void setVisible(boolean visible);

    /**
     * Sets a PXShape to be used as a clipping path on this shape.
     */
    void setClippingPath(PXShape clippingPath);

    /**
     * Sets a {@link PXShadowPaint} to be used to case inner and outer shadows
     */
    void setShadow(PXShadowPaint shadow);
}
