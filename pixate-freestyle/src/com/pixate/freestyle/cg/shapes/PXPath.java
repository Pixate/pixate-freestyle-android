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

import com.pixate.freestyle.cg.math.PXEllipticalArc;
import com.pixate.freestyle.cg.math.PXVector;
import com.pixate.freestyle.cg.parsing.PathParserHelper;
import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.PXLog;

/**
 * A PXShape sub-class used to render paths
 */
public class PXPath extends PXShape {

    private static String TAG = PXPath.class.getSimpleName();

    private Path pathPath;
    private PointF lastPoint;

    public PXPath() {
        pathPath = new Path();
        lastPoint = new PointF();
    }

    /**
     * Generate a new PXPath instance using the specified data This method
     * parses the specifying data, generating calls to the path building methods
     * in this class. The data is expected to be in the form as defined by the
     * SVG 1.1 specification for the path data's d attribute.
     * 
     * @param data A string of path data
     * @returns A newly allocated PXPath instance
     */
    public static PXPath createPathFromPathData(String data) {
        PXPath pxPath = new PXPath();
        char[] charData = data.toCharArray();
        PathParserHelper helper = new PathParserHelper(charData, 0);
        helper.skipWhitespace();
        float firstX = 0, firstY = 0;
        float lastX = 0, lastY = 0;
        float lastHandleX = 0, lastHandleY = 0;
        float x1, y1, x2, y2, x3, y3;
        char command = 0;
        char prevCommand = 0;
        int length = data.length();
        while (helper.pos < length) {
            helper.skipWhitespace();
            if (helper.pos >= length) {
                break;
            }
            command = charData[helper.pos];
            switch (command) {
                case '-':
                case '+':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (prevCommand == 'm') {
                        command = 'l';
                        break;
                    } else if (prevCommand == 'M') {
                        command = 'L';
                        break;
                    } else if (prevCommand == 'Z' || prevCommand == 'z') {
                        // possibly error state or may be implied M to first
                        // point of subpath with command becoming l or L
                        break;
                    } else {
                        command = prevCommand;
                        break;
                    }
                default: {
                    helper.advance();
                    prevCommand = command;
                }
            }
            switch (command) {
                case 'M':
                    x1 = helper.nextFloat();
                    y1 = helper.nextFloat();

                    pxPath.moveTo(x1, y1);

                    lastX = x1;
                    lastY = y1;
                    firstX = x1;
                    firstY = y1;
                    break;
                case 'm':
                    x1 = helper.nextFloat();
                    y1 = helper.nextFloat();

                    x1 += lastX;
                    y1 += lastY;

                    pxPath.moveTo(x1, y1);

                    lastX = x1;
                    lastY = y1;
                    firstX = x1;
                    firstY = y1;
                    break;
                case 'L':
                    x1 = helper.nextFloat();
                    y1 = helper.nextFloat();

                    pxPath.lineTo(x1, y1);

                    lastX = x1;
                    lastY = y1;
                    break;
                case 'l':
                    x1 = helper.nextFloat();
                    y1 = helper.nextFloat();

                    x1 += lastX;
                    y1 += lastY;

                    pxPath.lineTo(x1, y1);

                    lastX = x1;
                    lastY = y1;
                    break;
                case 'C':
                    x1 = helper.nextFloat();
                    y1 = helper.nextFloat();
                    x2 = helper.nextFloat();
                    y2 = helper.nextFloat();
                    x3 = helper.nextFloat();
                    y3 = helper.nextFloat();

                    pxPath.cubicBezierTo(x1, y1, x2, y2, x3, y3);

                    lastHandleX = x2;
                    lastHandleY = y2;
                    lastX = x3;
                    lastY = y3;
                    break;
                case 'c':
                    x1 = helper.nextFloat();
                    y1 = helper.nextFloat();
                    x2 = helper.nextFloat();
                    y2 = helper.nextFloat();
                    x3 = helper.nextFloat();
                    y3 = helper.nextFloat();

                    x1 += lastX;
                    y1 += lastY;
                    x2 += lastX;
                    y2 += lastY;
                    x3 += lastX;
                    y3 += lastY;

                    pxPath.cubicBezierTo(x1, y1, x2, y2, x3, y3);

                    lastHandleX = x2;
                    lastHandleY = y2;
                    lastX = x3;
                    lastY = y3;
                    break;

                case 'H':
                    x1 = helper.nextFloat();

                    pxPath.lineTo(x1, lastY);

                    lastX = x1;
                    break;

                case 'h':
                    x1 = helper.nextFloat();

                    x1 += lastX;

                    pxPath.lineTo(x1, lastY);

                    lastX = x1;
                    break;

                case 'V':
                    y1 = helper.nextFloat();

                    pxPath.lineTo(lastX, y1);

                    lastY = y1;
                    break;

                case 'v':
                    y1 = helper.nextFloat();
                    y1 += lastY;

                    pxPath.lineTo(lastX, y1);

                    lastY = y1;
                    break;

                case 'Q':
                    x1 = helper.nextFloat();
                    y1 = helper.nextFloat();
                    x2 = helper.nextFloat();
                    y2 = helper.nextFloat();

                    pxPath.quadraticBezierToX1(x1, y1, x2, y2);

                    lastHandleX = x1;
                    lastHandleY = y1;
                    lastX = x2;
                    lastY = y2;
                    break;

                case 'q':
                    x1 = helper.nextFloat();
                    y1 = helper.nextFloat();
                    x2 = helper.nextFloat();
                    y2 = helper.nextFloat();

                    x1 += lastX;
                    y1 += lastY;
                    x2 += lastX;
                    y2 += lastY;

                    pxPath.quadraticBezierToX1(x1, y1, x2, y2);

                    lastHandleX = x1;
                    lastHandleY = y1;
                    lastX = x2;
                    lastY = y2;
                    break;

                case 'A': {
                    PXLog.w(PXPath.class.getSimpleName(), "'A' path command, not supported");

                    float rx = helper.nextFloat();
                    float ry = helper.nextFloat();
                    float xAxisRotation = helper.nextFloat();
                    float largeArcFlag = helper.nextFloat();
                    float sweepFlag = helper.nextFloat();
                    x1 = helper.nextFloat();
                    y1 = helper.nextFloat();

                    pxPath.ellipticalArcRadius(rx, ry, xAxisRotation, (largeArcFlag > 0.0),
                            (sweepFlag > 0.0), x1, y1);

                    lastX = x1;
                    lastY = y1;
                    break;
                }

                case 'a':
                    PXLog.w(PXPath.class.getSimpleName(), "'a' path command, not supported");

                    // TODO: Implement this
                    float rx = helper.nextFloat();
                    float ry = helper.nextFloat();
                    float xAxisRotation = helper.nextFloat();
                    float largeArcFlag = helper.nextFloat();
                    float sweepFlag = helper.nextFloat();
                    x1 = helper.nextFloat();
                    y1 = helper.nextFloat();

                    x1 += lastX;
                    y1 += lastY;

                    pxPath.ellipticalArcRadius(rx, ry, xAxisRotation, (largeArcFlag > 0.0),
                            (sweepFlag > 0.0), x1, y1);

                    lastX = x1;
                    lastY = y1;
                    break;
                case 'S':
                    x2 = helper.nextFloat();
                    y2 = helper.nextFloat();
                    x3 = helper.nextFloat();
                    y3 = helper.nextFloat();

                    x1 = (lastX - lastHandleX) + lastX;
                    y1 = (lastY - lastHandleY) + lastY;

                    pxPath.cubicBezierTo(x1, y1, x2, y2, x3, y3);

                    lastHandleX = x2;
                    lastHandleY = y2;
                    lastX = x3;
                    lastY = y3;
                    break;

                case 's':
                    x2 = helper.nextFloat();
                    y2 = helper.nextFloat();
                    x3 = helper.nextFloat();
                    y3 = helper.nextFloat();

                    x1 = (lastX - lastHandleX) + lastX;
                    y1 = (lastY - lastHandleY) + lastY;
                    x2 += lastX;
                    y2 += lastY;
                    x3 += lastX;
                    y3 += lastY;

                    pxPath.cubicBezierTo(x1, y1, x2, y2, x3, y3);

                    lastHandleX = x2;
                    lastHandleY = y2;
                    lastX = x3;
                    lastY = y3;
                    break;

                case 'T':
                    x2 = helper.nextFloat();
                    y2 = helper.nextFloat();

                    x1 = (lastX - lastHandleX) + lastX;
                    y1 = (lastY - lastHandleY) + lastY;

                    pxPath.quadraticBezierToX1(x1, y1, x2, y2);

                    lastHandleX = x1;
                    lastHandleY = y1;
                    lastX = x2;
                    lastY = y2;
                    break;

                case 't':
                    x2 = helper.nextFloat();
                    y2 = helper.nextFloat();

                    x1 = (lastX - lastHandleX) + lastX;
                    y1 = (lastY - lastHandleY) + lastY;
                    x2 += lastX;
                    y2 += lastY;

                    pxPath.quadraticBezierToX1(x1, y1, x2, y2);

                    lastHandleX = x1;
                    lastHandleY = y1;
                    lastX = x2;
                    lastY = y2;
                    break;

                case 'Z':
                    pxPath.close();

                    prevCommand = '\0';
                    lastX = firstX;
                    lastY = firstY;
                    break;

                case 'z':
                    pxPath.close();

                    prevCommand = '\0';
                    lastX = firstX;
                    lastY = firstY;
                    break;
                default:
                    // report error
                    // [Pixate.configuration sendParseMessage:message];
                    PXLog.e(TAG, "Unrecognized or missing path command at offset: %d", helper.pos);
                    int start = Math.max(0, helper.pos - 10);
                    int end = Math.min(length, start + 30);
                    PXLog.e(TAG,
                            data.substring(start, helper.pos) + " >>> "
                                    + data.substring(helper.pos, end));
                    // stop scanning
                    helper.pos = length;
                    break;
            }
        }

        return pxPath;
    }

    /**
     * Add a close command to the current path
     * 
     * @return This instance for method chaining.
     */
    protected PXPath close() {
        pathPath.close();
        clearPath();
        return this;
    }

    /**
     * Add an arc of an ellipse to the current path
     * 
     * @param x The x-coordinate of the center of the ellipse
     * @param y The y-coordinate of the center of the ellipse
     * @param radiusX The x-radius of the ellipse
     * @param radiusY The y-radius of the ellipse
     * @param startAngle The starting angle of the arc
     * @param endAngle The ending angle of the arc
     * @return This instance for method chaining.
     */
    void ellipticalArc(float x, float y, float radiusX, float radiusY, float startAngle,
            float endAngle) {
        PointF lastPathPoint = PXEllipticalArc.pathAddEllipticalArc(pathPath, null, x, y, radiusX,
                radiusY, startAngle, endAngle);
        lastPoint.set(lastPathPoint);
    }

    private void ellipticalArcRadius(float radiusX, float radiusY, float xAxisRotation,
            boolean largeArcFlag, boolean sweepFlag, float x, float y) {
        if (radiusX != 0.0 || radiusY != 0.0) {
            float cx, cy;
            float startAngle, sweepAngle, endAngle;

            float halfDx = (lastPoint.x - x) * 0.5f;
            float halfDy = (lastPoint.y - y) * 0.5f;
            float radians = (float) Math.toRadians(xAxisRotation);
            float cosine = (float) Math.cos(radians);
            float sine = (float) Math.sin(radians);
            float x1p = halfDx * cosine + halfDy * sine;
            float y1p = halfDx * -sine + halfDy * cosine;
            float x1px1p = x1p * x1p;
            float y1py1p = y1p * y1p;
            float lambda = (x1px1p / (radiusX * radiusX)) + (y1py1p / (radiusY * radiusY));

            // it may be impossible for the specified radii to describe
            // an ellipse passing through the previous point and end point.
            // Adjust radii, if necessary, so ellipse can pass through those
            // points.
            if (lambda > 1.0) {
                float factor = (float) Math.sqrt(lambda);

                radiusX *= factor;
                radiusY *= factor;
            }

            float rxrx = radiusX * radiusX;
            float ryry = radiusY * radiusY;
            float rxrxryry = rxrx * ryry;
            float rxrxy1py1p = rxrx * y1py1p;
            float ryryx1px1p = ryry * x1px1p;
            float numerator = rxrxryry - rxrxy1py1p - ryryx1px1p;
            float s;

            if (numerator < 1e-6) {
                s = 0.0f;
            } else {
                s = (float) Math.sqrt(numerator / (rxrxy1py1p + ryryx1px1p));
            }
            if (largeArcFlag == sweepFlag) {
                s = -s;
            }
            float cxp = s * radiusX * y1p / radiusY;
            float cyp = s * -radiusY * x1p / radiusX;
            cx = cxp * cosine - cyp * sine + (lastPoint.x + x) * 0.5f;
            cy = cxp * sine + cyp * cosine + (lastPoint.y + y) * 0.5f;

            PXVector u = new PXVector(1f, 0f);
            // NOTE: SVG spec divides x-component by rx and y-component by ry
            PXVector v = new PXVector(x1p - cxp, y1p - cyp);
            PXVector w = new PXVector(-x1p - cxp, -y1p - cyp);

            startAngle = u.angleBetweenVector(v);
            sweepAngle = v.angleBetweenVector(w);

            if (!sweepFlag && sweepAngle > 0.0) {
                sweepAngle -= PXEllipticalArc.TWO_PI;
            } else if (sweepFlag && sweepAngle < 0.0) {
                sweepAngle += PXEllipticalArc.TWO_PI;
            }

            endAngle = startAngle + sweepAngle;

            ellipticalArc(cx, cy, radiusX, radiusY, startAngle, endAngle);
        }
    }

    /**
     * Add a qcurveto command to the current path
     * 
     * @param x1 The x-coordinate of the first handle of the quadratic bezier
     *            curve being added
     * @param y1 The y-coordinate of the first handle of the quadratic bezier
     *            curve being added
     * @param x2 The x-coordinate of the second handle of the quadratic bezier
     *            curve being added
     * @param y2 The y-coordinate of the second handle of the quadratic bezier
     *            curve being added
     * @return This instance for method chaining.
     */
    protected PXPath quadraticBezierToX1(float x1, float y1, float x2, float y2) {
        pathPath.quadTo(x1, y1, x2, y2);
        lastPoint.set(x2, y2);
        clearPath();
        return this;
    }

    /**
     * Add a curveto command to the current path
     * 
     * @param x1 The x-coordinate of the first handle of the cubic bezier curve
     *            being added
     * @param y1 The y-coordinate of the first handle of the cubic bezier curve
     *            being added
     * @param x2 The x-coordinate of the second handle of the cubic bezier curve
     *            being added
     * @param y2 The y-coordinate of the second handle of the cubic bezier curve
     *            being added
     * @param x3 The x-coordinate of the third handle of the cubic bezier curve
     *            being added
     * @param y3 The y-coordinate of the third handle of the cubic bezier curve
     *            being added
     * @return This instance for method chaining.
     */
    protected PXPath cubicBezierTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        pathPath.cubicTo(x1, y1, x2, y2, x3, y3);
        lastPoint.set(x3, y3);
        clearPath();
        return this;
    }

    /**
     * Add a lineto command to the current path
     * 
     * @param x The x-coordinate of the line being added
     * @param y The y-coordinate of the line being added
     * @return This instance for method chaining.
     */
    protected PXPath lineTo(float x, float y) {
        pathPath.lineTo(x, y);
        lastPoint.set(x, y);
        clearPath();
        return this;
    }

    /**
     * Add a moveto command to the current path
     * 
     * @param x The x-coordinate of the new position to move to within this path
     * @param y The y-coordinate of the new position to move to within this path
     * @return This instance for method chaining.
     */
    protected PXPath moveTo(float x, float y) {
        pathPath.moveTo(x, y);
        lastPoint.set(x, y);

        clearPath();
        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.cg.PXShape#newPath()
     */
    @Override
    protected Path newPath() {
        return ObjectPool.pathPool.checkOut(pathPath);
    }
}
