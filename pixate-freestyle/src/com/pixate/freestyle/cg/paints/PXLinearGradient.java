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
package com.pixate.freestyle.cg.paints;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;

import com.pixate.freestyle.cg.math.PXVector;
import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.ObjectUtil;
import com.pixate.freestyle.util.PXColorUtil;
import com.pixate.freestyle.util.PXLog;

/**
 * PX linear gradient representation.
 */
public class PXLinearGradient extends PXGradient {

    public enum PXLinearGradientDirection {
        TO_TOP, TO_TOP_RIGHT, TO_RIGHT, TO_BOTTOM_RIGHT, TO_BOTTOM, TO_BOTTOM_LEFT, TO_LEFT, TO_TOP_LEFT
    };

    public enum PXAngleType {
        ANGLE, POINTS, DIRECTION
    };

    private static final String TAG = "PXLinearGradient";
    private static final PointF POINT_ZERO = new PointF();

    private float angle;
    private PXAngleType angleType;
    private PXLinearGradientDirection gradientDirection;
    private PointF p1;
    private PointF p2;

    /**
     * Constructs a new {@link PXLinearGradient} with a default top-to-bottom
     * direction, and zero start & end points.
     */
    public PXLinearGradient() {
        angle = 90.0f;
        p1 = POINT_ZERO;
        p2 = POINT_ZERO;
        angleType = PXAngleType.ANGLE;
    }

    /**
     * The angle to be used when calculating the rendering of this gradient.
     * Note that setting this value overrides any values set using points or
     * gradient directions.
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Sets the angle
     * 
     * @param angle
     */
    public void setAngle(float angle) {
        this.angle = angle;
        angleType = PXAngleType.ANGLE;
    }

    /**
     * Angles in Android and CSS differ. This is a convenience property that
     * allows angles to follow the CSS specification's definition of an angle.
     * Note that setting this value overrides any values set using points or
     * gradient directions.
     */
    public float getCssAngle() {
        return angle + 90.0f;
    }

    /**
     * Sets a CSS angle.
     * 
     * @param angle
     */
    public void setCssAngle(float angle) {
        this.angle = angle - 90.0f;
    }

    /**
     * Angles in Android and Photoshop differ. This is a convenience property
     * that allows angles to follow Photoshop's definition of an angle. Note
     * that setting this value overrides any values set using points or gradient
     * directions.
     */
    public float getPsAngle() {
        return -angle;
    }

    /**
     * Sets a Photoshop angle.
     * 
     * @param angle
     */
    public void setPsAngle(float angle) {
        this.angle = -angle;
    }

    /**
     * The first point in the gradient. Note that setting this point overrides
     * any values set by angle or gradient direction.
     */
    public PointF getP1() {
        return p1;
    }

    /**
     * Sets the first point in the gradient.
     * 
     * @param p1
     */
    public void setP1(PointF p1) {
        this.p1 = p1;
        angleType = PXAngleType.POINTS;
    }

    /**
     * The last point in the gradient. Note that setting this point overrides
     * any values set by angle or gradient direction.
     */
    public PointF getP2() {
        return p2;
    }

    /**
     * Sets the last point in the gradient.
     * 
     * @param p2
     */
    public void setP2(PointF p2) {
        this.p2 = p2;
        angleType = PXAngleType.POINTS;
    }

    public void setGradientDirection(PXLinearGradientDirection gradientDirection) {
        this.gradientDirection = gradientDirection;
        angleType = PXAngleType.DIRECTION;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.cg.paints.PXPaint#applyFillToPath(android.graphics
     * .Path, android.graphics.Paint, android.graphics.Canvas)
     */
    public void applyFillToPath(Path path, Paint paint, Canvas context) {
        context.save();
        // TODO Clip to path? (may cause gradient distortions on the edges?)
        // transform gradient space
        context.concat(transform);

        // placeholders for gradient points
        PointF point1, point2;

        if (angleType == PXAngleType.POINTS) {
            if (gradientUnits == PXGradientUnits.USER_SPACE) {
                point1 = this.p1;
                point2 = this.p2;
            } else {
                // linear-gradient points are based on the shape's bbox, so grab
                // that

                RectF pathBounds = new RectF();
                path.computeBounds(pathBounds, true);
                // grab the x,y offset which we will apply later
                float left = pathBounds.left;
                float top = pathBounds.top;

                // grab the positions within the bbox for each point
                float p1x = pathBounds.width() * p1.x;
                float p1y = pathBounds.height() * p1.y;
                float p2x = pathBounds.width() * p2.x;
                float p2y = pathBounds.height() * p2.y;

                // create final points by offsetting the bbox coordinates by the
                // bbox origin
                point1 = new PointF(left + p1x, top + p1y);
                point2 = new PointF(left + p2x, top + p2y);
            }
        } else {
            RectF pathBounds = new RectF();
            path.computeBounds(pathBounds, true);
            float angle = this.angle;

            if (angleType == PXAngleType.DIRECTION) {
                switch (gradientDirection) {
                    case TO_TOP:
                        angle = 270.0f;
                        break;

                    case TO_TOP_RIGHT: {
                        PXVector toBottomRight = PXVector.vectorWithStartPoint(new PointF(
                                pathBounds.left, pathBounds.top), new PointF(pathBounds.right,
                                pathBounds.bottom));
                        angle = (float) (Math.toDegrees(toBottomRight.angle()) - 90.0f);
                        break;
                    }

                    case TO_RIGHT:
                        angle = 0.0f;
                        break;

                    case TO_BOTTOM_RIGHT: {
                        PXVector toBottomLeft = PXVector.vectorWithStartPoint(new PointF(
                                pathBounds.right, pathBounds.top), new PointF(pathBounds.left,
                                pathBounds.bottom));
                        angle = (float) (Math.toDegrees(toBottomLeft.angle()) - 90.0f);
                        break;
                    }

                    case TO_BOTTOM:
                        angle = 90.0f;
                        break;

                    case TO_BOTTOM_LEFT: {
                        PXVector toTopLeft = PXVector.vectorWithStartPoint(new PointF(
                                pathBounds.right, pathBounds.bottom), new PointF(pathBounds.left,
                                pathBounds.top));
                        angle = (float) (Math.toDegrees(toTopLeft.angle()) - 90.0f);
                        break;
                    }

                    case TO_LEFT:
                        angle = 180.0f;
                        break;

                    case TO_TOP_LEFT: {
                        PXVector toTopRight = PXVector.vectorWithStartPoint(new PointF(
                                pathBounds.left, pathBounds.bottom), new PointF(pathBounds.right,
                                pathBounds.top));
                        angle = (float) (Math.toDegrees(toTopRight.angle()) - 90.0f);
                        break;
                    }
                }
            }

            // normalize between 0 and 360
            angle = angle % 360.0f;

            while (angle < 0.0f) {
                angle += 360.0f;
            }

            // calculate end points of gradient based on angle
            if (angle == 0) {
                point1 = new PointF(pathBounds.left, pathBounds.top);
                point2 = new PointF(pathBounds.right, pathBounds.top);
            } else if (angle == 90) {
                point1 = new PointF(pathBounds.left, pathBounds.top);
                point2 = new PointF(pathBounds.left, pathBounds.bottom);
            } else if (angle == 180) {
                point1 = new PointF(pathBounds.right, pathBounds.top);
                point2 = new PointF(pathBounds.left, pathBounds.top);
            } else if (angle == 270) {
                point1 = new PointF(pathBounds.left, pathBounds.bottom);
                point2 = new PointF(pathBounds.left, pathBounds.top);
            } else {
                // find active corner and it's opposite
                PointF endCorner = new PointF();

                // NOTE: assumes angle is in half-open interval [0,360)
                if (0.0f <= angle && angle < 90.0f) {
                    // top-left
                    endCorner = new PointF(pathBounds.left, pathBounds.top);
                } else if (90.0f <= angle && angle < 180.0f) {
                    // top-right
                    endCorner = new PointF(pathBounds.right, pathBounds.top);
                } else if (180.0f <= angle && angle < 270.0f) {
                    // bottom-right
                    endCorner = new PointF(pathBounds.right, pathBounds.bottom);
                } else if (270.0f <= angle && angle < 360.0f) {
                    // bottom-left
                    endCorner = new PointF(pathBounds.left, pathBounds.bottom);
                } else {
                    // error
                    PXLog.e(TAG, "Angle not within the half-closed interval [0,360): %f", angle);
                }

                // find center
                PointF center = new PointF(pathBounds.centerX(), pathBounds.centerY());

                // get corner and angle vectors
                float radians = (float) Math.toRadians(angle);
                PXVector cornerVector = PXVector.vectorWithStartPoint(center, endCorner);
                PXVector angleVector = PXVector.vectorWithStartPoint(new PointF(), new PointF(
                        (float) Math.cos(radians), (float) Math.sin(radians)));

                // project corner vector onto angle vector
                PXVector projection = cornerVector.projectOnto(angleVector);

                // apply results
                point1 = new PointF(center.x + projection.getX(), center.y + projection.getY());
                point2 = new PointF(center.x - projection.getX(), center.y - projection.getY());
            }
        }

        // do the gradient
        Paint p = ObjectPool.paintPool.checkOut(paint);
        p.setAntiAlias(true);
        p.setShader(getGradient(point1, point2));
        // apply the blending mode
        p.setXfermode(blendingMode);
        // draw
        context.drawPath(path, p);
        // restore coordinate system
        context.restore();
        // Check the paint back into the pool
        ObjectPool.paintPool.checkIn(p);
    }

    public PXPaint lightenByPercent(float percent) {
        PXLinearGradient result = createCopyWithoutColors();
        // copy and lighten colors
        for (int color : colors) {
            result.addColor(PXColorUtil.lightterByPercent(color, percent));
        }
        return result;
    }

    public PXPaint darkenByPercent(float percent) {
        PXLinearGradient result = createCopyWithoutColors();
        // copy and darken colors
        for (int color : colors) {
            result.addColor(PXColorUtil.darkenByPercent(color, percent));
        }
        return result;
    }

    private PXLinearGradient createCopyWithoutColors() {
        PXLinearGradient result = new PXLinearGradient();

        // copy properties
        result.setAngle(angle);
        result.setP1(p1);
        result.setP2(p2);
        result.setGradientDirection(gradientDirection);

        // copy PXGradient properties, but not colors
        result.setTransform(new Matrix(transform));
        result.offsets = new ArrayList<Float>(offsets);

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXGradient#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof PXLinearGradient && super.equals(other)) {
            PXLinearGradient gradient = (PXLinearGradient) other;
            if (gradient.angleType == angleType) {
                if (angleType == PXAngleType.POINTS) {
                    return ObjectUtil.areEqual(p1, gradient.p1)
                            && ObjectUtil.areEqual(p2, gradient.p2);
                } else {
                    return angle == gradient.angle;
                }
            }
        }
        return false;
    }

    /**
     * Allocate and initialize a new linear gradient using the specified
     * starting and ending colors
     * 
     * @param startColor The starting color of this gradient
     * @param endColor The ending color of this gradient
     */
    public static PXLinearGradient gradientFromStartColor(int startColor, int endcolor) {
        PXLinearGradient lg = new PXLinearGradient();
        lg.addColor(startColor); // TODO: withOffset:0.0f?
        lg.addColor(endcolor); // TODO: withOffset:1.0f?
        return lg;
    }

    private LinearGradient getGradient(PointF point1, PointF point2) {
        adjustGradientColors();
        int[] colors = new int[this.colors.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = this.colors.get(i);
        }
        float[] positions = null;
        if (!offsets.isEmpty()) {
            positions = new float[this.offsets.size()];
            for (int i = 0; i < positions.length; i++) {
                positions[i] = this.offsets.get(i);
            }
        }
        try {
            LinearGradient gradient = new LinearGradient(point1.x, point1.y, point2.x, point2.y,
                    colors, positions, TileMode.CLAMP);
            return gradient;
        } catch (Exception e) {
            if (PXLog.isLogging()) {
                PXLog.e(TAG, e, "Error while instantiating a LinearGradient");
            }
            return null;
        }
    }
}
