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
package com.pixate.freestyle.cg.shapes;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.strokes.PXStroke;
import com.pixate.freestyle.styling.infos.PXBorderInfo;
import com.pixate.freestyle.styling.infos.PXBorderInfo.PXBorderStyle;
import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.Size;

public class PXBoxModel extends PXShape implements PXBoundable {

    public enum PXBoxSizing {
        CONTENT_BOX,
        PADDING_BOX,
        BORDER_BOX
    }

    private PXBorderInfo borderTop;
    private PXBorderInfo borderRight;
    private PXBorderInfo borderBottom;
    private PXBorderInfo borderLeft;

    private PXPath borderPathTop;
    private PXPath borderPathRight;
    private PXPath borderPathBottom;
    private PXPath borderPathLeft;

    private Size radiusTopLeft;
    private Size radiusTopRight;
    private Size radiusBottomRight;
    private Size radiusBottomLeft;

    private RectF bounds;

    public PXBoxModel() {
        this(new RectF(0f, 0f, 0f, 0f));
    }

    public PXBoxModel(RectF bounds) {
        this.bounds = bounds;
        borderTop = new PXBorderInfo();
        borderRight = new PXBorderInfo();
        borderBottom = new PXBorderInfo();
        borderLeft = new PXBorderInfo();

        radiusTopLeft = Size.ZERO;
        radiusTopRight = Size.ZERO;
        radiusBottomRight = Size.ZERO;
        radiusBottomLeft = Size.ZERO;
    }

    // GETTERS

    public PXPaint getBorderTopPaint() {
        return borderTop.getPaint();
    }

    public PXBorderStyle getBorderTopStyle() {
        return borderTop.getStyle();
    }

    public float getBorderTopWidth() {
        return borderTop.getWidth();
    }

    public PXPaint getBorderRightPaint() {
        return borderRight.getPaint();
    }

    public PXBorderStyle getBorderRightStyle() {
        return borderRight.getStyle();
    }

    public float getBorderRightWidth() {
        return borderRight.getWidth();
    }

    public PXPaint getBorderBottomPaint() {
        return borderBottom.getPaint();
    }

    public PXBorderStyle getBorderBottomStyle() {
        return borderBottom.getStyle();
    }

    public float getBorderBottomWidth() {
        return borderBottom.getWidth();
    }

    public PXPaint getBorderLeftPaint() {
        return borderLeft.getPaint();
    }

    public PXBorderStyle getBorderLeftStyle() {
        return borderLeft.getStyle();
    }

    public float getBorderLeftWidth() {
        return borderLeft.getWidth();
    }

    public RectF getBorderBounds() {
        RectF bounds = this.bounds;

        bounds.left -= borderLeft.getWidth();
        bounds.top -= borderTop.getWidth();
        float width = bounds.width() + borderLeft.getWidth() + borderRight.getWidth();
        float height = bounds.height() + borderTop.getWidth() + borderBottom.getWidth();
        bounds.right = bounds.left + width;
        bounds.bottom = bounds.top + height;

        return bounds;
    }

    public RectF getContentBounds() {
        return bounds;
    }

    public Size getRadiusTopLeft() {
        return radiusTopLeft;
    }

    public Size getRadiusTopRight() {
        return radiusTopRight;
    }

    public Size getRadiusBottomRight() {
        return radiusBottomRight;
    }

    public Size getRadiusBottomLeft() {
        return radiusBottomLeft;
    }

    public boolean hasBorder() {
        return borderTop.hasContent() || borderRight.hasContent() || borderBottom.hasContent()
                || borderLeft.hasContent();
    }

    public boolean hasCornerRadius() {
        return Size.isNonZero(radiusTopLeft) || Size.isNonZero(radiusTopRight)
                || Size.isNonZero(radiusBottomRight) || Size.isNonZero(radiusBottomLeft);
    }

    public boolean isOpaque() {
        return !hasCornerRadius() && borderTop.isOpaque() && borderRight.isOpaque()
                && borderBottom.isOpaque() && borderLeft.isOpaque();
    }

    // SETTERS

    public void setBorderTopPaint(PXPaint paint) {
        borderTop.setPaint(paint);
        clearPath();
    }

    public void setBorderTopStyle(PXBorderStyle style) {
        borderTop.setStyle(style);
        clearPath();
    }

    public void setBorderTopWidth(float width) {
        borderTop.setWidth(width);
        clearPath();
    }

    public void setBorderRightPaint(PXPaint paint) {
        borderRight.setPaint(paint);
        clearPath();
    }

    public void setBorderRightStyle(PXBorderStyle style) {
        borderRight.setStyle(style);
        clearPath();
    }

    public void setBorderRightWidth(float width) {
        borderRight.setWidth(width);
        clearPath();
    }

    public void setBorderBottomPaint(PXPaint paint) {
        borderBottom.setPaint(paint);
        clearPath();
    }

    public void setBorderBottomStyle(PXBorderStyle style) {
        borderBottom.setStyle(style);
        clearPath();
    }

    public void setBorderBottomWidth(float width) {
        borderBottom.setWidth(width);
        clearPath();
    }

    public void setBorderLeftPaint(PXPaint paint) {
        borderLeft.setPaint(paint);
        clearPath();
    }

    public void setBorderLeftStyle(PXBorderStyle style) {
        borderLeft.setStyle(style);
        clearPath();
    }

    public void setBorderLeftWidth(float width) {
        borderLeft.setWidth(width);
        clearPath();
    }

    public void setBorderTop(PXPaint paint, float width, PXBorderStyle style) {
        setBorderTopPaint(paint);
        setBorderTopWidth(width);
        setBorderTopStyle(style);
    }

    public void setBorderRight(PXPaint paint, float width, PXBorderStyle style) {
        setBorderRightPaint(paint);
        setBorderRightWidth(width);
        setBorderRightStyle(style);
    }

    public void setBorderBottom(PXPaint paint, float width, PXBorderStyle style) {
        setBorderBottomPaint(paint);
        setBorderBottomWidth(width);
        setBorderBottomStyle(style);
    }

    public void setBorderLeft(PXPaint paint, float width, PXBorderStyle style) {
        setBorderLeftPaint(paint);
        setBorderLeftWidth(width);
        setBorderLeftStyle(style);
    }

    public void setBorderPaint(PXPaint paint) {
        setBorderTopPaint(paint);
        setBorderRightPaint(paint);
        setBorderBottomPaint(paint);
        setBorderLeftPaint(paint);
    }

    public void setBorderWidth(float width) {
        setBorderTopWidth(width);
        setBorderRightWidth(width);
        setBorderBottomWidth(width);
        setBorderLeftWidth(width);
    }

    public void setBorderStyle(PXBorderStyle style) {
        setBorderTopStyle(style);
        setBorderRightStyle(style);
        setBorderBottomStyle(style);
        setBorderLeftStyle(style);
    }

    public void setBorder(PXPaint paint, float width, PXBorderStyle style) {
        setBorderTop(paint, width, style);
        setBorderRight(paint, width, style);
        setBorderBottom(paint, width, style);
        setBorderLeft(paint, width, style);
    }

    public void setRadiusTopLeft(Size radius) {
        radiusTopLeft = radius;
        clearPath();
    }

    public void setRadiusTopRight(Size radius) {
        radiusTopRight = radius;
        clearPath();
    }

    public void setRadiusBottomRight(Size radius) {
        radiusBottomRight = radius;
        clearPath();
    }

    public void setRadiusBottomLeft(Size radius) {
        radiusBottomLeft = radius;
        clearPath();
    }

    /**
     * Set the corner radius of all corners to the specified value.
     * 
     * @param radius A corner radius
     */
    public void setCornerRadius(float radius) {
        setCornerRadii(new Size(radius, radius));
    }

    /**
     * Set the corner radius of all corners to the specified value.
     * 
     * @param radii The x and y radii.
     */
    public void setCornerRadii(Size radii) {
        radiusTopLeft = radii;
        radiusTopRight = radii;
        radiusBottomRight = radii;
        radiusBottomLeft = radii;
    }

    // IMPLEMENTATIONS

    /**
     * @link {@link PXBoundable#getBounds()}
     */
    public RectF getBounds() {
        return bounds;
    }

    /**
     * @link {@link PXBoundable#setBounds(RectF)}
     */
    public void setBounds(RectF bounds) {
        this.bounds = bounds;
    }

    // OVERRIDES

    /**
     * @link {@link PXShape#newPath()}
     */
    @Override
    protected Path newPath() {
        Path resultPath = null;

        if (!hasCornerRadius()) {
            resultPath = ObjectPool.pathPool.checkOut();
            resultPath.addRect(getBorderBounds(), Path.Direction.CW);
            createBorders();
        }

        return resultPath;
    }

    /**
     * @link {@link PXShape#renderChildren(Canvas)}
     */
    @Override
    protected void renderChildren(Canvas context) {
        if (borderPathTop != null) {
            borderPathTop.render(context);
        }

        if (borderPathRight != null) {
            borderPathRight.render(context);
        }

        if (borderPathBottom != null) {
            borderPathBottom.render(context);
        }

        if (borderPathLeft != null) {
            borderPathLeft.render(context);
        }
    }

    // PRIVATE METHODS

    private float[] buildDashArray(float length, float width) {

        float minWidth = 1.75f * width;
        float count = (int) (length / minWidth);
        float spacing = (length - (count * width)) / (count - 1.0f);

        return new float[] { width, spacing };
    }

    private void createBorders() {
        RectF borderBounds = getBorderBounds();
        float borderLeftVal = borderBounds.left;
        float borderRightVal = borderBounds.right;
        float borderTopVal = borderBounds.top;
        float borderBottomVal = borderBounds.bottom;

        float contentLeftVal = bounds.left;
        float contentRightVal = bounds.right;
        float contentTopVal = bounds.top;
        float contentBottomVal = bounds.bottom;

        // reset borders
        borderPathTop = borderPathRight = borderPathBottom = borderPathLeft = null;

        // top
        if (borderTop.hasContent()) {
            borderPathTop = new PXPath();

            switch (borderTop.getStyle()) {
                case SOLID:
                    borderPathTop.moveTo(borderLeftVal, borderTopVal)
                            .lineTo(borderRightVal, borderTopVal)
                            .lineTo(contentRightVal, contentTopVal)
                            .lineTo(contentLeftVal, contentTopVal).close().setFillColor(fillColor);
                    break;

                case DASHED: {
                    float y = (borderTopVal + contentTopVal) * 0.5f;
                    float width = borderTop.getWidth();

                    borderPathTop.moveTo(borderLeftVal, y).lineTo(borderRightVal, y);

                    PXStroke stroke = new PXStroke(width);
                    stroke.setColor(borderTop.getPaint());
                    stroke.setDashArray(buildDashArray(borderRightVal - borderLeftVal, 2.0f * width));
                    borderPathTop.setStroke(stroke);
                    break;
                }

                case DOTTED: {
                    float y = (borderTopVal + contentTopVal) * 0.5f;
                    float width = borderTop.getWidth();

                    borderPathTop.moveTo(borderLeftVal, y).lineTo(borderRightVal, y);

                    PXStroke stroke = new PXStroke(width);
                    stroke.setColor(borderTop.getPaint());
                    stroke.setDashArray(buildDashArray(borderRightVal - borderLeftVal, width));
                    borderPathTop.setStroke(stroke);
                    break;
                }

                case DOUBLE:
                    // TODO
                    break;

                case GROOVE:
                case INSET:
                case OUTSET:
                case RIDGE:
                    break;

                // NOTE: We should never hit these cases
                case NONE:
                case HIDDEN:
                default:
                    break;
            }
        }

        // right
        if (borderRight.hasContent()) {
            borderPathRight = new PXPath();

            switch (borderRight.getStyle()) {
                case SOLID:
                    borderPathRight.moveTo(borderRightVal, borderTopVal)
                            .lineTo(borderRightVal, borderBottomVal)
                            .lineTo(contentRightVal, contentBottomVal)
                            .lineTo(contentRightVal, contentTopVal).close()
                            .setFillColor(borderRight.getPaint());
                    break;

                case DASHED: {
                    float x = (borderRightVal + contentRightVal) * 0.5f;
                    float width = borderRight.getWidth();

                    borderPathRight.moveTo(x, borderTopVal).lineTo(x, borderBottomVal);

                    PXStroke stroke = new PXStroke(width);
                    stroke.setColor(borderRight.getPaint());
                    stroke.setDashArray(buildDashArray(borderBottomVal - borderTopVal, 2.0f * width));
                    borderPathRight.setStroke(stroke);
                    break;
                }

                case DOTTED: {
                    float x = (borderRightVal + contentRightVal) * 0.5f;
                    float width = borderRight.getWidth();

                    borderPathRight.moveTo(x, borderTopVal).lineTo(x, borderBottomVal);

                    PXStroke stroke = new PXStroke(width);
                    stroke.setColor(borderRight.getPaint());
                    stroke.setDashArray(buildDashArray(borderBottomVal - borderTopVal, width));
                    borderPathRight.setStroke(stroke);
                    break;
                }

                case DOUBLE:
                case GROOVE:
                case INSET:
                case OUTSET:
                case RIDGE:
                    break;

                // NOTE: We should never hit these cases
                case NONE:
                case HIDDEN:
                default:
                    break;
            }
        }

        // bottom

        if (borderBottom.hasContent()) {
            borderPathBottom = new PXPath();

            switch (borderBottom.getStyle()) {
                case SOLID:
                    borderPathBottom.moveTo(contentRightVal, contentBottomVal)
                            .lineTo(borderRightVal, borderBottomVal)
                            .lineTo(borderLeftVal, borderBottomVal)
                            .lineTo(contentLeftVal, contentBottomVal).close()
                            .setFillColor(borderBottom.getPaint());

                case DASHED: {
                    float y = (borderBottomVal + contentBottomVal) * 0.5f;
                    float width = borderBottom.getWidth();

                    borderPathBottom.moveTo(borderLeftVal, y).lineTo(borderRightVal, y);

                    PXStroke stroke = new PXStroke(width);
                    stroke.setColor(borderBottom.getPaint());
                    stroke.setDashArray(buildDashArray(borderRightVal - borderLeftVal, 2.0f * width));
                    borderPathBottom.setStroke(stroke);
                    break;
                }

                case DOTTED: {
                    float y = (borderBottomVal + contentBottomVal) * 0.5f;
                    float width = borderBottom.getWidth();

                    borderPathBottom.moveTo(borderLeftVal, y).lineTo(borderRightVal, y);

                    PXStroke stroke = new PXStroke(width);
                    stroke.setColor(borderBottom.getPaint());
                    stroke.setDashArray(buildDashArray(borderRightVal - borderLeftVal, width));
                    borderPathBottom.setStroke(stroke);
                    break;
                }

                case DOUBLE:
                case GROOVE:
                case INSET:
                case OUTSET:
                case RIDGE:
                    break;

                // NOTE: We should never hit these cases
                case NONE:
                case HIDDEN:
                default:
                    break;
            }
        }

        // left
        if (borderLeft.hasContent()) {
            borderPathLeft = new PXPath();

            switch (borderLeft.getStyle()) {
                case SOLID:
                    borderPathLeft.moveTo(contentLeftVal, contentTopVal)
                            .lineTo(contentLeftVal, contentBottomVal)
                            .lineTo(borderLeftVal, borderBottomVal)
                            .lineTo(borderLeftVal, borderTopVal).close()
                            .setFillColor(borderLeft.getPaint());
                    break;

                case DASHED: {
                    float x = (borderLeftVal + contentLeftVal) * 0.5f;
                    float width = borderLeft.getWidth();

                    borderPathLeft.moveTo(x, borderTopVal).lineTo(x, borderBottomVal);

                    PXStroke stroke = new PXStroke(width);
                    stroke.setColor(borderLeft.getPaint());
                    stroke.setDashArray(buildDashArray(borderBottomVal - borderTopVal, 2.0f * width));
                    borderPathLeft.setStroke(stroke);
                    break;
                }

                case DOTTED: {
                    float x = (borderLeftVal + contentLeftVal) * 0.5f;
                    float width = borderLeft.getWidth();

                    borderPathLeft.moveTo(x, borderTopVal).lineTo(x, borderBottomVal);

                    PXStroke stroke = new PXStroke(width);
                    stroke.setColor(borderLeft.getPaint());
                    stroke.setDashArray(buildDashArray(borderBottomVal - borderTopVal, width));
                    borderPathLeft.setStroke(stroke);
                    break;
                }

                case DOUBLE:
                case GROOVE:
                case INSET:
                case OUTSET:
                case RIDGE:
                    break;

                // NOTE: We should never hit these cases
                case NONE:
                case HIDDEN:
                default:
                    break;
            }
        }

    }

}
