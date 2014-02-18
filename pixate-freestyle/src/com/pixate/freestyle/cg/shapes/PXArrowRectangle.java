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
import android.graphics.RectF;

import com.pixate.freestyle.cg.math.PXEllipticalArc;
import com.pixate.freestyle.util.ObjectPool;

/**
 * Pixate arrow rectangle
 */
public class PXArrowRectangle extends PXRectangle {

    public enum PXArrowRectangleDirection {
        LEFT,
        RIGHT
    };

    private PXArrowRectangleDirection direction;

    public PXArrowRectangle(PXArrowRectangleDirection direction) {
        this(new RectF(), direction);
    }

    public PXArrowRectangle(RectF bounds, PXArrowRectangleDirection direction) {
        super(bounds);
        this.direction = direction;
    }

    @Override
    protected Path newPath() {

        float handleOffset = 2.5f;
        float arrowWidth = 12.0f;

        RectF bounds = getBounds();
        float left = bounds.left;
        float top = bounds.top;
        float right = bounds.right;
        float bottom = bounds.bottom;
        float arrowLeftY = (top + bottom) * 0.5f;

        // create path
        Path path = ObjectPool.pathPool.checkOut();
        if (!hasRoundedCorners()) {
            if (direction == PXArrowRectangleDirection.LEFT) {
                float arrowRightX = Math.min(right, left + arrowWidth);

                path.moveTo(left, arrowLeftY);
                // Move & quad - equivalent to CGPathAddCurveToPoint
                path.moveTo(left, arrowLeftY);
                path.quadTo(arrowRightX - handleOffset, bottom, arrowRightX, bottom);
                path.lineTo(right, bottom);
                path.lineTo(right, top);
                path.lineTo(arrowRightX, top);
                path.moveTo(arrowRightX, top);
                path.quadTo(arrowRightX - handleOffset, top, left, arrowLeftY);
                path.close();
            } else {
                float arrowRightX = Math.max(left, right - arrowWidth);

                path.moveTo(right, arrowLeftY);
                path.moveTo(right, arrowLeftY);
                path.quadTo(arrowRightX + handleOffset, bottom, arrowRightX, bottom);
                path.lineTo(left, bottom);
                path.lineTo(left, top);
                path.lineTo(arrowRightX, top);
                path.moveTo(arrowRightX, top);
                path.quadTo(arrowRightX + handleOffset, top, right, arrowLeftY);
                path.close();
            }
        } else {
            // top points
            float topLeftX = left + radiusTopLeft.width;
            float topRightX = right - radiusTopRight.width;

            // right points
            float rightTopY = top + radiusTopRight.height;
            float rightBottomY = bottom - radiusBottomRight.height;

            // bottom points
            float bottomLeftX = left + radiusBottomLeft.width;
            float bottomRightX = right - radiusBottomRight.width;

            // left points
            float leftTopY = top + radiusTopLeft.height;
            float leftBottomY = bottom - radiusBottomLeft.height;

            // create path

            if (direction == PXArrowRectangleDirection.LEFT) {
                float arrowRightX = Math.min(right, left + arrowWidth);

                path.moveTo(left, arrowLeftY);
                path.quadTo(arrowRightX - handleOffset, bottom, arrowRightX, bottom);

                // add right and bottom-right corner
                if (radiusBottomRight.width > 0.0f && radiusBottomRight.height > 0.0f) {
                    path.lineTo(bottomRightX, bottom);
                    PXEllipticalArc.pathAddEllipticalArc(path, null, bottomRightX, rightBottomY,
                            radiusBottomRight.width, radiusBottomRight.height, -3.0f
                                    * PXEllipticalArc.M_PI_2, 0.0f);
                } else {
                    path.lineTo(right, bottom);
                }

                // add top and top-right corner
                if (radiusTopRight.width > 0.0f && radiusTopRight.height > 0.0f) {
                    path.lineTo(right, rightTopY);
                    PXEllipticalArc.pathAddEllipticalArc(path, null, topRightX, rightTopY,
                            radiusTopRight.width, radiusTopRight.height, 0.0f, -PXEllipticalArc.M_PI_2);
                } else {
                    path.lineTo(right, top);
                }

                path.lineTo(arrowRightX, top);
                path.moveTo(arrowRightX, top);
                path.quadTo(arrowRightX - handleOffset, top, left, arrowLeftY);
                path.close();
            } else {
                float arrowRightX = Math.max(left, right - arrowWidth);

                path.moveTo(right, arrowLeftY);
                path.quadTo(arrowRightX + handleOffset, bottom, arrowRightX, bottom);

                // add bottom and bottom-left corner
                if (radiusBottomLeft.width > 0.0f && radiusBottomLeft.height > 0.0f) {
                    path.lineTo(bottomLeftX, bottom);
                    path.moveTo(bottomLeftX, leftBottomY);
                    path.quadTo(radiusBottomLeft.width, radiusBottomLeft.height, PXEllipticalArc.M_PI_2,
                            (float) -Math.PI);
                } else {
                    path.lineTo(left, bottom);
                }

                // add left and top-left corner
                if (radiusTopLeft.width > 0.0f && radiusTopLeft.height > 0.0f) {
                    path.lineTo(left, leftTopY);
                    path.moveTo(topLeftX, leftTopY);
                    path.quadTo(radiusTopLeft.width, radiusTopLeft.height, (float) Math.PI,
                            3.0f * PXEllipticalArc.M_PI_2);
                } else {
                    path.lineTo(left, top);
                }

                path.lineTo(arrowRightX, top);
                path.moveTo(arrowRightX, top);
                path.quadTo(arrowRightX + handleOffset, top, right, arrowLeftY);
                path.close();
            }
        }
        return path;
    }
}
