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

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * A PXShape sub-class used to render collections of shapes.
 */
public class PXShapeGroup extends PXShape {

    public static enum AlignViewPortType {
        kAlignViewPortNone("none"),
        XMIN_YMIN("xMinYMin"),
        XMIN_YMID("xMinYMid"),
        XMIN_YMAX("xMinYMax"),
        XMID_YMIN("xMidYMin"),
        XMID_YMID("xMidYMid"),
        XMID_YMAX("xMidYMax"),
        XMAX_YMIN("xMaxYMin"),
        XMAX_YMID("xMaxYMid"),
        XMAX_YMAX("xMaxYMax");
        private String name;

        private AlignViewPortType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static enum CropType {
        MEET,
        SLICE
    }

    private ArrayList<PXRenderable> shapes;
    private float width;
    private float height;
    private RectF viewport;
    private AlignViewPortType viewportAlignment;
    private CropType viewportCrop;
    private Matrix viewPortTransform;

    /**
     * Constructs a renderable shapes group.
     */
    public PXShapeGroup() {
        shapes = new ArrayList<PXRenderable>(5);
        viewport = new RectF();
        viewportAlignment = AlignViewPortType.XMID_YMID;
        viewportCrop = CropType.MEET;
    }

    /**
     * Sets the viewport of this shape group.
     * 
     * @param viewport
     */
    public void setViewport(RectF viewport) {
        this.viewport = viewport;
    }

    /**
     * Returns the viewport of this shape group.
     * 
     * @return The viewport rectangle.
     */
    public RectF getViewport() {
        return viewport;
    }

    /**
     * Set the width of this shape group.
     * 
     * @param width A new width
     * @deprecated
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Returns the width of this shape group.
     * 
     * @deprecated
     */
    public float getWidth() {
        return width;
    }

    /**
     * Set the height of this shape group.
     * 
     * @param height A new height
     * @deprecated
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Returns the height of this shape group.
     * 
     * @return the height
     * @deprecated
     */
    public float getHeight() {
        return height;
    }

    /**
     * Returns how many child shapes this group contains.
     * 
     * @return The group size.
     */
    public int getShapeCount() {
        if (shapes != null) {
            return shapes.size();
        }
        return 0;
    }

    /**
     * Adds a shape to this shape group. <code>null</code> values are ignored.
     * 
     * @param shape The shape to add
     */
    public void addShape(PXRenderable shape) {
        if (shape != null) {
            // add shape to child list
            shapes.add(shape);

            // set child's parent
            shape.setParent(this);
        }
    }

    /**
     * Removes the specified shape from the shape group.
     * 
     * @param shape The shape to remove
     */
    public void removeShape(PXRenderable shape) {
        if (shape != null) {
            shapes.remove(shape);
            // TODO: verify this is in this group
            shape.setParent(null);
        }
    }

    /**
     * Returns the shape at the specified index. <code>null</code> is returned
     * for index values that are out of range.
     * 
     * @param index The index of the shape to return.
     * @returns A PXRenderable or <code>null</code>
     */
    public PXRenderable getShapeAtIndex(int index) {
        return (shapes != null && index > -1 && index <= shapes.size() - 1) ? shapes.get(index)
                : null;
    }

    /**
     * Returns the alignment to use when mapping a shape group's viewport to the
     * screen.
     */
    public AlignViewPortType getViewportAlignment() {
        return viewportAlignment;
    }

    /**
     * Sets the alignment to use when mapping a shape group's viewport to the
     * screen.
     */
    public void setViewportAlignment(AlignViewPortType viewportAlignment) {
        this.viewportAlignment = viewportAlignment;
    }

    /**
     * Returns type of crop to use when applying the shape group's viewport to
     * the screen
     */
    public CropType getViewportCrop() {
        return viewportCrop;
    }

    /**
     * Sets type of crop to use when applying the shape group's viewport to the
     * screen
     */
    public void setViewportCrop(CropType viewportCrop) {
        this.viewportCrop = viewportCrop;
    }

    /**
     * Returns the transform that would need to be applied to this shape group
     * in order for its viewport to fit within the specified shape group width
     * and height.
     * 
     * @return A {@link Matrix} transformation.
     */
    public Matrix getViewPortTransform() {
        if (viewport != null) {
            float viewportWidth = viewport.width();
            float viewportHeight = viewport.height();
            if (viewportWidth > 0 && viewportHeight > 0 && width > 0 && height > 0) {
                float ratioX = width / viewportWidth;
                float ratioY = height / viewportHeight;
                // Create the Matrix transformation
                Matrix matrix = new Matrix();
                matrix.preTranslate(viewport.left, viewport.top);

                if (viewportAlignment == AlignViewPortType.kAlignViewPortNone) {
                    matrix.preScale(ratioX, ratioY);
                } else {
                    if ((ratioX > ratioY && viewportCrop == CropType.MEET)
                            || (ratioX < ratioY && viewportCrop == CropType.SLICE)) {
                        float tx = 0;
                        float diffX = width - viewportWidth * ratioY;

                        switch (viewportAlignment) {
                            case XMID_YMIN:
                            case XMID_YMID:
                            case XMID_YMAX:
                                tx = diffX * 0.5F;
                                break;

                            case XMAX_YMIN:
                            case XMAX_YMID:
                            case XMAX_YMAX:
                                tx = diffX;
                                break;

                            default:
                                break;
                        }
                        matrix.preTranslate(tx, 0);
                        matrix.preScale(ratioY, ratioY);
                    } else if ((ratioX < ratioY && viewportCrop == CropType.MEET)
                            || (ratioX > ratioY && viewportCrop == CropType.SLICE)) {
                        float ty = 0;
                        float diffY = height - viewportHeight * ratioX;

                        switch (viewportAlignment) {
                            case XMIN_YMID:
                            case XMID_YMID:
                            case XMAX_YMID:
                                ty = diffY * 0.5F;
                                break;

                            case XMIN_YMAX:
                            case XMID_YMAX:
                            case XMAX_YMAX:
                                ty = diffY;
                                break;

                            default:
                                break;
                        }
                        matrix.preTranslate(0, ty);
                        matrix.preScale(ratioX, ratioX);
                    } else {
                        matrix.preScale(ratioX, ratioX);
                    }
                }
                viewPortTransform = matrix;
            }
        }
        return viewPortTransform;
    }

    /**
     * Render the group.
     * 
     * @param context A {@link Canvas} context.
     */
    public void renderChildren(Canvas context) {
        Matrix transform = getViewPortTransform();
        if (transform != null) {
            context.concat(transform);
        } else {
            // TODO - Do we want to log this?
            // Log.w(TAG, "PXShapeGroup#getViewPortTransform() returned null");
        }
        // Render the group
        for (PXRenderable renderable : shapes) {
            renderable.render(context, false);
        }
    }

    // TODO - We'll need to manually call this one to clear up the childern's
    // parent. I really don't want to put this in a finalize() method...
    public void dealloc() {
        if (shapes != null) {
            try {
                for (PXRenderable shape : shapes) {
                    shape.setParent(null);
                }
                shapes = null;
            } catch (Throwable t) {
                // ignore
            }
        }
    }
}
