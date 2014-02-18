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
package com.pixate.freestyle.styling.stylers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.View;
import android.widget.GridView;

import com.pixate.freestyle.cg.math.PXOffsets;
import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.paints.PXPaintGroup;
import com.pixate.freestyle.cg.paints.PXSolidPaint;
import com.pixate.freestyle.cg.shadow.PXShadow;
import com.pixate.freestyle.cg.shadow.PXShadowGroup;
import com.pixate.freestyle.cg.shadow.PXShadowPaint;
import com.pixate.freestyle.cg.shapes.PXBoundable;
import com.pixate.freestyle.cg.shapes.PXBoxModel;
import com.pixate.freestyle.cg.shapes.PXRectangle;
import com.pixate.freestyle.cg.shapes.PXShape;
import com.pixate.freestyle.cg.strokes.PXStroke;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.fonts.PXFontRegistry;
import com.pixate.freestyle.styling.infos.PXAnimationInfo;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualStyleable;
import com.pixate.freestyle.util.PXDrawableUtil;
import com.pixate.freestyle.util.Size;
import com.pixate.freestyle.util.StringUtil;

/**
 * Styler context.
 */
public class PXStylerContext {

    /**
     * A class that hold fading edge styles for views that supports it while
     * scrolling. A property with a <code>null</code> value in this class
     * indicates that no CSS style value was assigned to it. This is important
     * later on when applying this edge style on a {@link View}.
     */
    public static class FadingEdgeStyle {
        public Integer edgeLength;
        public Boolean horizontalEnabled;
        public Boolean verticalEnabled;
    }

    /**
     * A class that hold overscrolling styles for views that supports it while
     * scrolling beyond their data area. A property with a <code>null</code>
     * value in this class indicates that no CSS style value was assigned to it.
     */
    public static class OverscrollStyle {
        public int distance;
        public PXPaint header;
        public PXPaint footer;
    }

    /**
     * A class that hold icon styles for views that supports it (like TextView
     * and its descendants). A property with a <code>null</code> value in this
     * class indicates that no CSS style value was assigned to it.
     */
    public static class CompoundIcons {
        public Drawable top;
        public Drawable right;
        public Drawable bottom;
        public Drawable left;
    }

    /**
     * The position of the icon that this adapter is handling.
     */
    public enum IconPosition {
        LEFT("icon-left"),
        TOP("icon-top"),
        RIGHT("icon-right"),
        BOTTOM("icon-bottom");

        private String elementName;

        private IconPosition(String elementName) {
            this.elementName = elementName;
        }

        public String getElementName() {
            return elementName;
        }
    };

    /**
     * A class that holds style properties for a grid layout (like for GridView)
     * such as the vertical and horizontal spacing, column width, etc.
     */
    public static class GridStyle {
        /**
         * See {@link android.widget.GridView#setStretchMode(int)}
         * 
         * @author Bill Dawson
         */
        public enum PXColumnStretchMode {
            NONE("none", GridView.NO_STRETCH),
            SPACING("spacing", GridView.STRETCH_SPACING),
            SPACING_UNIFORM("spacing-uniform", GridView.STRETCH_SPACING_UNIFORM),
            COLUMN_WIDTH("column-width", GridView.STRETCH_COLUMN_WIDTH);

            private final String cssValue;
            private final int androidValue;

            private PXColumnStretchMode(String cssValue, int androidValue) {
                this.cssValue = cssValue;
                this.androidValue = androidValue;
            }

            private static Map<String, PXColumnStretchMode> cssValueToEnumMap;
            static {
                cssValueToEnumMap = new HashMap<String, PXColumnStretchMode>(4);
                for (PXColumnStretchMode mode : PXColumnStretchMode.values()) {
                    cssValueToEnumMap.put(mode.getCssValue(), mode);
                }
            }

            public String getCssValue() {
                return this.cssValue;
            }

            public int getAndroidValue() {
                return this.androidValue;
            }

            public static PXColumnStretchMode ofCssValue(String cssValue) {
                return cssValueToEnumMap.get(cssValue);
            }
        }

        public int columnCount = Integer.MIN_VALUE;
        public int columnWidth = Integer.MIN_VALUE;
        public int columnGap = Integer.MIN_VALUE;
        public int columnStretchMode = Integer.MIN_VALUE;
        public int rowGap = Integer.MIN_VALUE;
    }

    private static final int NO_COLOR_VAL = Integer.MIN_VALUE;

    private Object styleable;
    private String activeStateName;

    private PXShape shape;

    // For PXLayoutStyler
    private float top;
    private float left;
    private float width;
    private float height;
    private RectF bounds;

    private PXOffsets padding;
    private Matrix transform;

    private PXBoxModel boxModel;

    private PXPaint fill;
    private PXPaint imageFill;
    private PXPaint dividerFill;

    /*
     * Just a note: Depending on how shadows are (or will be) implemented in
     * Android, we may not need to separate inner and outer shadows. We kind of
     * get outer shadows for free in iOS, but it's a process to get inner
     * shadows. I separate them out here to make that division easier to process
     * later (Kevin)
     */

    private PXShadowGroup innerShadow;
    private PXShadowGroup outerShadow;

    private PXShadowPaint textShadow;
    private float opacity;

    private Size imageSize;
    private PXOffsets insets;

    private String text;

    private String fontName;
    private String fontStyle;
    private String fontWeight;
    private float fontSize;

    private String fontStretch;

    private List<PXAnimationInfo> transitionInfos;
    private List<PXAnimationInfo> animationInfos;

    private Map<String, Object> properties;

    private DisplayMetrics displayMetrics;
    private int styleHash;

    // TODO - What's a reasonable size for this? We may also need to overwrite
    // the sizeOf() to limit this cache to a memory size.
    private static LruCache<Integer, Drawable> IMAGE_CACHE = new LruCache<Integer, Drawable>(10);

    // Holds all fading styles, in case any was set.
    private FadingEdgeStyle fadingStyle;
    // Holds overscroll styling
    private OverscrollStyle overscroll;
    // Holds the icon styling
    private CompoundIcons compoundIcons;

    // Holds grid styling
    private GridStyle grid;

    private boolean isVirtual;

    public PXStylerContext() {
        shape = new PXRectangle(new RectF());

        setTop(setLeft(Float.MAX_VALUE));
        setHeight(0.0f);
        setWidth(0.0f);

        boxModel = new PXBoxModel();

        imageSize = new Size(0.0f, 0.0f);
        transform = new Matrix();
        opacity = 1.0f;

        // Standard name given in Android, then each implementation takes it
        // from there.
        fontName = "sans-serif";
        fontStyle = "normal";
        fontWeight = "normal";
        // fontStretch = "normal";
        fontSize = 16.0f;

    }

    /**
     * Constructs a new {@link PXStylerContext}.
     * 
     * @param styleable
     * @param stateName
     * @param styleHash A style hash that was computed from the
     *            {@link PXDeclaration}s that will be involved in the rendering.
     */
    public PXStylerContext(Object styleable, String stateName, int styleHash) {
        this();
        this.styleable = styleable;
        this.isVirtual = (styleable instanceof PXVirtualStyleable);
        this.activeStateName = stateName;
        this.styleHash = styleHash;

        if (styleable instanceof View) {
            this.displayMetrics = ((View) styleable).getContext().getResources()
                    .getDisplayMetrics();
        }
    }

    // Statics
    /**
     * Reset any drawables cache that the {@link PXStylerContext} may hold.
     */
    public static void resetCache() {
        IMAGE_CACHE.evictAll();
    }

    // Methods

    // TODO equiv? - (void)applyOuterShadowToLayer:(CALayer *)layer
    public void applyOuterShadow(View view) {

    }

    public Object getPropertyValue(String propertyName) {
        if (properties == null) {
            return null;
        }

        return properties.get(propertyName);
    }

    public void setPropertyValue(Object value, String propertyName) {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        properties.put(propertyName, value);
    }

    public boolean usesColorOnly() {
        boolean result = false;

        // this.color has a color value if we have a fill that is a solid paint
        // only.
        if (getColor() != NO_COLOR_VAL) {
            result = isRectangle() && (innerShadow == null || innerShadow.size() == 0)
                    && (boxModel == null || !(boxModel.hasCornerRadius() || boxModel.hasBorder()))
                    && imageFill == null;
        }

        return result;
    }

    public boolean usesImage() {
        return imageFill != null || (fill != null && getColor() == NO_COLOR_VAL)
                || (innerShadow != null && innerShadow.size() > 0) || !isRectangle()
                || (boxModel != null && (boxModel.hasCornerRadius() || boxModel.hasBorder()));

    }

    // Getters

    public PXBoxModel getBoxModel() {
        return boxModel;
    }

    /**
     * Returns the active state name for this context. The state name can later
     * be mapped into a {@link Drawable} state integer by using the
     * {@link PXDrawableUtil} class.
     * 
     * @return The active state name (can be <code>null</code>)
     */
    public String getActiveStateName() {
        return activeStateName;
    }

    public PXPaint getCombinedPaints() {
        if (fill != null && imageFill != null) {
            PXPaintGroup group = new PXPaintGroup(fill, imageFill);

            return group;
        } else {
            return imageFill != null ? imageFill : fill;
        }
    }

    public Drawable getBackgroundImage(RectF bounds) {
        this.bounds = bounds;
        return getBackgroundImage();
    }

    public Drawable getBackgroundImage() {
        // Check the cache first!
        Drawable cachedDrawable = IMAGE_CACHE.get(styleHash);
        if (cachedDrawable != null) {
            return cachedDrawable;
        }

        // No luck with the cache... compute the drawable.
        // Update bounds
        if (Size.isNonZero(imageSize)) {
            bounds = new RectF(0.0f, 0.0f, imageSize.width, imageSize.height);

        } else if (bounds == null || bounds.isEmpty()) {
            PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(styleable);
            bounds = styleAdapter.getBounds(styleable);

            if (bounds == null || bounds.isEmpty()) {
                // Set default size to 32, 32 if zero.
                bounds = new RectF(0.0f, 0.0f, 32.0f, 32.0f);
            }
        }

        // apply bounds
        // NOTE: This updates the bounds of the underlying geometry used to draw
        // the background image.
        // This does not resize the styleable. (Note taken from Obj-C).
        if (shape instanceof PXBoundable) {
            PXBoundable boundable = (PXBoundable) shape;
            boundable.setBounds(bounds);
        }

        // apply fill
        shape.setFillColor(getCombinedPaints());

        // apply stroke and possibly modify geoometry bounds
        if (boxModel.hasBorder()) {

            // NOTE: We're using top border since we set all borders
            // the same right now.
            float strokeWidth = boxModel.getBorderTopWidth();
            PXPaint strokeColor = boxModel.getBorderTopPaint();
            PXStroke stroke = new PXStroke(strokeWidth);

            if (strokeColor != null) {
                stroke.setColor(strokeColor);
            }

            this.shape.setStroke(stroke);

            // shrink bounds by half of the stroke width
            if (shape instanceof PXBoundable) {
                PXBoundable boundable = (PXBoundable) shape;

                float insetDelta = 0.5f * strokeWidth;
                RectF insetBounds = new RectF(bounds);
                insetBounds.inset(insetDelta, insetDelta);
                boundable.setBounds(insetBounds);
            }
        }

        // set corner radius
        if (shape instanceof PXRectangle) {
            PXRectangle rect = (PXRectangle) shape;

            rect.setRadiusTopLeft(boxModel.getRadiusTopLeft());
            rect.setRadiusTopRight(boxModel.getRadiusTopRight());
            rect.setRadiusBottomRight(boxModel.getRadiusBottomRight());
            rect.setRadiusBottomLeft(boxModel.getRadiusBottomLeft());
        }

        // apply inner shadows
        if (innerShadow != null && innerShadow.size() > 0) {
            shape.setShadow(innerShadow);
        }

        // generate image
        boolean isOpaque = this.isOpaque();
        Drawable result = shape.renderToImage(bounds, isOpaque);

        if (padding != null && padding.hasOffset()) {
            // Wrap the result with an InsetDrawable that will hold the padding
            // information (TODO - The padding should actually go in between the
            // border and the content. This implementation just match what we
            // have on the iOS side, which is also pending a fix)
            result = new InsetDrawable(result, (int) padding.getLeft(), (int) padding.getTop(),
                    (int) padding.getRight(), (int) padding.getBottom());
        }

        // apply insets, if we have any (similar to the padding)
        if (insets != null && insets.hasOffset()) {
            // TODO Nine-patch. This code doesn't work, unfortunately. We'll
            // need to investigate more now to render the bitmap with cap
            // insets like in iOS.
            /* @formatter:off
            Bitmap bitmap;
            if (result instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) result).getBitmap();
            } else {
                bitmap = Bitmap.createBitmap(result.getIntrinsicWidth(),
                        result.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                result.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                result.draw(canvas);
            }
            result = NinePatchUtil.createNinePatch(Pixate.getAppContext().getResources(), bitmap,
                    insets, null);
            */
            // @formatter:on
        }
        // Cache the result and return
        IMAGE_CACHE.put(styleHash, result);

        return result;
    }

    public int getStyleHash() {
        return styleHash;
    }

    public boolean isOpaque() {
        return opacity == 1.0f && boxModel.isOpaque() && (fill != null && fill.isOpaque())
                && (imageFill != null && imageFill.isOpaque());
    }

    public Typeface getFont() {
        Typeface result = null;

        if (!StringUtil.isEmpty(fontName)) {
            result = PXFontRegistry.getTypeface(fontName, fontWeight, fontStyle);
        }

        return result;
    }

    // Since Typeface won't include size, here's a getter for that.
    public float getFontSize() {
        return fontSize;
    }

    // The background color, if specified.
    public int getColor() {
        if (fill instanceof PXSolidPaint) {
            return ((PXSolidPaint) fill).getColor();
        } else {
            return NO_COLOR_VAL;
        }
    }

    public float getTop() {
        return top;
    }

    public float getLeft() {
        return left;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Object getStyleable() {
        // in case the styleable is virtual, return the view that this styleable
        // is nested in.
        if (isVirtual && styleable != null) {
            return ((PXVirtualStyleable) styleable).getParent();
        }
        return styleable;
    }

    public PXPaint getFill() {
        return fill;
    }

    public PXPaint getDividerFill() {
        return dividerFill;
    }

    public PXOffsets getInsets() {
        if (insets == null) {
            insets = new PXOffsets();
        }
        return insets;
    }

    public PXOffsets getPadding() {
        if (padding == null) {
            padding = new PXOffsets();
        }
        return padding;
    }

    public Matrix getTransform() {
        return transform;
    }

    public float getOpacity() {
        return opacity;
    }

    public PXShape getShape() {
        return shape;
    }

    public String getText() {
        return text;
    }

    public PXShadowPaint getTextShadow() {
        return textShadow;
    }

    public List<PXAnimationInfo> getTransitionInfos() {
        return transitionInfos;
    }

    public List<PXAnimationInfo> getAnimationInfos() {
        return animationInfos;
    }

    public DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public String getFontName() {
        return fontName;
    }

    public String getFontStretch() {
        return fontStretch;
    }

    // SETTERS

    public void setShadow(PXShadowPaint shadow) {

        if (shadow != null) {
            innerShadow = new PXShadowGroup();
            outerShadow = new PXShadowGroup();

            if (shadow instanceof PXShadow) {
                PXShadow _shadow = (PXShadow) shadow;

                if (_shadow.isInset()) {
                    innerShadow.add(_shadow);
                } else {
                    outerShadow.add(_shadow);
                }

            } else if (shadow instanceof PXShadowGroup) {
                PXShadowGroup shadowGroup = (PXShadowGroup) shadow;

                for (PXShadowPaint shadowPaint : shadowGroup) {
                    if (shadowPaint instanceof PXShadow) {
                        PXShadow _shadow = (PXShadow) shadowPaint;

                        if (_shadow.isInset()) {
                            innerShadow.add(_shadow);
                        } else {
                            outerShadow.add(_shadow);
                        }
                    }
                }
            }

        } else {
            innerShadow = outerShadow = null;
        }
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float setLeft(float left) {
        this.left = left;
        return left;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setFill(PXPaint paint) {
        fill = paint;
    }

    public void setImageSize(Size size) {
        imageSize = size;
    }

    public void setInsets(PXOffsets insets) {
        this.insets = insets;
    }

    public void setImageFill(PXPaint paint) {
        this.imageFill = paint;
    }

    public void setDividerFill(PXPaint paint) {
        this.dividerFill = paint;
    }

    public void setPadding(PXOffsets padding) {
        this.padding = padding;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public void setFontSize(float size) {
        this.fontSize = size;
    }

    public void setFontStyle(String style) {
        this.fontStyle = style;
    }

    public void setFontWeight(String weight) {
        this.fontWeight = weight;
    }

    public void setFontStretch(String stretch) {
        this.fontStretch = stretch;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public void setShape(PXShape shape) {
        this.shape = shape;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextShadow(PXShadowPaint textShadow) {
        this.textShadow = textShadow;
    }

    public void setTransform(Matrix transform) {
        this.transform = transform;
    }

    public void setTransitionInfos(List<PXAnimationInfo> transitionInfos) {
        this.transitionInfos = transitionInfos;
    }

    public void setAnimationInfos(List<PXAnimationInfo> animationInfos) {
        this.animationInfos = animationInfos;
    }

    // Fading edge attributes
    public void setFadingEdgeLength(int fadingEdgeLength) {
        if (fadingStyle == null) {
            fadingStyle = new FadingEdgeStyle();
        }
        fadingStyle.edgeLength = fadingEdgeLength;
    }

    public void setHorizontalFadingEdgeEnabled(boolean enabled) {
        if (fadingStyle == null) {
            fadingStyle = new FadingEdgeStyle();
        }
        fadingStyle.horizontalEnabled = enabled;
    }

    public void setVerticalFadingEdgeEnabled(boolean enabled) {
        if (fadingStyle == null) {
            fadingStyle = new FadingEdgeStyle();
        }
        fadingStyle.verticalEnabled = enabled;
    }

    /**
     * Returns the fading edge style. <code>null</code> in case no modifications
     * to the original View style were made.
     * 
     * @return A {@link FadingEdgeStyle}. Can be <code>null</code>.
     */
    public FadingEdgeStyle getFadingStyle() {
        return fadingStyle;
    }

    // Overscroll styling

    public void setOverscrollDistance(float distance) {
        if (overscroll == null) {
            overscroll = new OverscrollStyle();
        }
        overscroll.distance = (int) Math.ceil(distance);
    }

    public void setOverscrollHeader(PXPaint paint) {
        if (overscroll == null) {
            overscroll = new OverscrollStyle();
        }
        overscroll.header = paint;
    }

    public void setOverscrollFooter(PXPaint paint) {
        if (overscroll == null) {
            overscroll = new OverscrollStyle();
        }
        overscroll.footer = paint;
    }

    /**
     * Returns the overscroll style. <code>null</code> in case no modifications
     * to the original View style were made.
     * 
     * @return A {@link OverscrollStyle}. Can be <code>null</code>.
     */
    public OverscrollStyle getOverscrollStyle() {
        return overscroll;
    }

    // Compound Icons

    /**
     * Sets the {@link Drawable} to one of the four icons that can be defined
     * for the View.
     * 
     * @param position The {@link IconPosition}
     * @param drawable
     */
    public void setCompoundIcon(IconPosition position, Drawable drawable) {
        if (compoundIcons == null) {
            compoundIcons = new CompoundIcons();
        }
        switch (position) {
            case TOP:
                compoundIcons.top = drawable;
                break;
            case RIGHT:
                compoundIcons.right = drawable;
                break;
            case BOTTOM:
                compoundIcons.bottom = drawable;
                break;
            default:
                compoundIcons.left = drawable;
                break;
        }
    }

    /**
     * Returns the {@link CompoundIcons} instance in case at least one of its
     * icons was defined. <code>null</code> in case no modifications to the
     * original View style were made. The icon style can be applied for views
     * that support it (like TextView and its descendants)
     * 
     * @return A {@link CompoundIcons}. Can be <code>null</code>.
     */
    public CompoundIcons getCompoundIcons() {
        return compoundIcons;
    }

    // Grid

    /**
     * Number of columns to show in grid.
     * 
     * @param columnCount
     */
    public void setColumnCount(int numColumns) {
        if (grid == null) {
            grid = new GridStyle();
        }
        grid.columnCount = numColumns;
    }

    /**
     * Fixed width for columns.
     * 
     * @param width
     */
    public void setColumnWidth(int width) {
        if (grid == null) {
            grid = new GridStyle();
        }

        grid.columnWidth = width;
    }

    /**
     * How columns stretch to fill space.
     * 
     * @param mode
     */
    public void setColumnStretchMode(int mode) {
        if (grid == null) {
            grid = new GridStyle();
        }

        grid.columnStretchMode = mode;
    }

    /**
     * Default horizontal spacing between columns.
     * 
     * @param spacing
     */
    public void setColumnGap(int spacing) {
        if (grid == null) {
            grid = new GridStyle();
        }

        grid.columnGap = spacing;
    }

    /**
     * Default vertical spacing between rows.
     * 
     * @param spacing
     */
    public void setRowGap(int spacing) {
        if (grid == null) {
            grid = new GridStyle();
        }

        grid.rowGap = spacing;
    }

    /**
     * How columns should stretch to fill available space, if at all.
     * 
     * @param mode See {@link android.widget.GridView#setStretchMode} for
     *            values.
     */
    public void setStretchMode(int mode) {
        if (grid == null) {
            grid = new GridStyle();
        }

        grid.columnStretchMode = mode;
    }

    /**
     * Returns the style properties to set for GridViews and any subclasses
     * thereof. <code>null</code> in case no modifications to the original View
     * style were made.
     * 
     * @return A {@link GridStyle}. Can be <code>null</code>.
     */
    public GridStyle getGridStyle() {
        return grid;
    }

    // PRIVATE
    private boolean isRectangle() {
        return shape == null || (shape instanceof PXRectangle);
    }
}
