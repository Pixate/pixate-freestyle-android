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
package com.pixate.freestyle.util;

import java.util.HashMap;
import java.util.Map;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import com.pixate.freestyle.styling.cache.PXStyleInfo;

public class PXColorUtil {

    // Holds all the possible Android color state names and values. Note that
    // the keys that are used in this map will omit the "state_" prefix that
    // android defines in the name of the attribute.
    private static final Map<String, Integer> STATES;
    static {
        STATES = new HashMap<String, Integer>();
        STATES.put("focused", android.R.attr.state_focused);
        STATES.put("window_focused", android.R.attr.state_window_focused);
        STATES.put("enabled", android.R.attr.state_enabled);
        STATES.put("checked", android.R.attr.state_checked);
        STATES.put("checkable", android.R.attr.state_checkable);
        STATES.put("selected", android.R.attr.state_selected);
        STATES.put("pressed", android.R.attr.state_pressed);
        // note that default is "color"
        STATES.put(PXStyleInfo.DEFAULT_STYLE, android.R.attr.color);
    }

    /**
     * Returns a map of color supported states. These states can be used with
     * the {@link ColorStateList} to set a text color on a view.
     * <ul>
     * <li>"state_focused"
     * <li>"state_window_focused"
     * <li>"state_enabled"
     * <li>"state_checked"
     * <li>"state_checkable"
     * <li>"state_selected"
     * <li>"state_pressed"
     * <li>"color" (default)
     * </ul>
     * 
     * @return A supported states map that
     */
    public static Map<String, Integer> getSupportedStates() {
        return new HashMap<String, Integer>(STATES);
    }

    /**
     * Returns the integer state value that is mapped to the given state name.
     * In case none can be mapped, the method returns {@link Integer#MIN_VALUE}.
     * 
     * @param stateName
     * @return The color state integer value; {@link Integer#MIN_VALUE} in case
     *         the given state name cannot be matched.
     */
    public static int getStateValue(String stateName) {
        if (STATES.containsKey(stateName)) {
            return STATES.get(stateName);
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Returns a color from a color-hex string.
     * 
     * @param hex
     * @return A color.
     */
    public static int colorFromHexString(String hex) {
        return colorFromHexString(hex, 1.0F);
    }

    /**
     * Returns a color from a color-hex string and an alpha value.
     * 
     * @param hex
     * @param alpha
     * @return A color.
     */
    public static int colorFromHexString(String hex, float alpha) {
        if (hex == null || hex.length() == 0) {
            throw new IllegalArgumentException("Hex color was null or empty");
        }
        int color = 0;
        if (hex.charAt(0) != '#') {
            color = Color.parseColor('#' + hex);
        } else {
            color = Color.parseColor(hex);
        }
        return Color.argb((int) (alpha * 255), Color.red(color), Color.green(color),
                Color.blue(color));
    }

    /**
     * Convert HSL to color.
     * 
     * @param alpha
     * @param hue
     * @param saturation
     * @param lightness
     * @return
     */
    public static int hslToColor(int alpha, float hue, float saturation, float lightness) {
        float hh = hue;
        float ss = saturation;
        float ll = lightness;

        float h, s, v;

        h = hh;
        ll *= 2;
        ss *= (ll <= 1) ? ll : 2 - ll;
        v = (ll + ss) / 2;
        s = ((ll + ss) != 0) ? (2 * ss) / (ll + ss) : 0;
        return Color.HSVToColor(alpha, new float[] { h, s, v });
    }

    /**
     * Convert a color to a HSL array.
     * 
     * @param color The color to convert.
     * @param hsl A size-3 array to load with the HSL values.
     */
    public static void colorToHsl(int color, float[] hsl) {
        float r = ((0x00ff0000 & color) >> 16) / 255.0F;
        float g = ((0x0000ff00 & color) >> 8) / 255.0F;
        float b = ((0x000000ff & color)) / 255.0F;
        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);
        float c = max - min;

        float hTemp = 0.0F;
        if (c == 0) {
            hTemp = 0;
        } else if (max == r) {
            hTemp = (float) (g - b) / c;
            if (hTemp < 0)
                hTemp += 6.0F;
        } else if (max == g) {
            hTemp = (float) (b - r) / c + 2.0F;
        } else if (max == b) {
            hTemp = (float) (r - g) / c + 4.0F;
        }
        float h = 60.0F * hTemp;

        float l = (max + min) * 0.5F;

        float s;
        if (c == 0) {
            s = 0.0F;
        } else {
            s = c / (1 - Math.abs(2.0F * l - 1.0F));
        }

        hsl[0] = h;
        hsl[1] = s;
        hsl[2] = l;
    }

    /**
     * Creates a color states-list.
     * 
     * @param color
     * @return A state-list
     */
    public static ColorStateList createColorStateList(int color) {
        // @formatter:off
		// FIXME - This is buggy. The minute we set the color in, the button is no longer clickable....
		//	[[-16842910], [16842908, -16842910], [16842919], [16842913], [16842908], []]
		//  [-2147483648,      -2147483648,          -1,         -1,        -1,   -16777216]
		return new ColorStateList(
				new int[][] { 
						new int[] { -android.R.attr.state_enabled },
						new int[] { android.R.attr.state_focused, -android.R.attr.state_enabled},
						new int[] { android.R.attr.state_pressed },
						new int[] { android.R.attr.state_selected },
						new int[] { android.R.attr.state_focused},
						new int[0]
				}, 
				new int[] {
						Integer.MIN_VALUE, // !enabled
						Integer.MIN_VALUE, // focused & !enabled
						Color.WHITE, // pressed
						Color.WHITE, // selected
						Color.WHITE, // focused
						color
				});
		
		// @formatter:on
    }

    /**
     * Returns a color that match the given SVG color string.<br>
     * This is a convenient method for accessing the {@link SVGColors}. You can
     * also call the {@link SVGColors#get(String)} directly. Also, make sure you
     * call {@link SVGColors#release()} when there is no need for immediate
     * mapping of SVG color string to color values.
     * 
     * @param SVGColorName
     * @return A color
     * @see SVGColors#get(String)
     * @see SVGColors#release()
     */
    public static int getColorFromSVGName(String SVGColorString) {
        return SVGColors.get(SVGColorString);
    }

    /**
     * Darken a color by percent.
     * 
     * @param color
     * @param percent 0.0 - 1.0
     * @return A new, darker color.
     */
    public static int darkenByPercent(int color, float percent) {
        // TODO We may try an HSV approach...
        // float[] hsv = new float[3];
        // Color.colorToHSV(color, hsv);
        // hsv[2] *= percent;
        // return Color.HSVToColor(hsv);
        float r = Color.red(color) * percent;
        float g = Color.green(color) * percent;
        float b = Color.blue(color) * percent;
        int ir = Math.min(255, (int) r);
        int ig = Math.min(255, (int) g);
        int ib = Math.min(255, (int) b);
        int ia = Color.alpha(color);
        return (Color.argb(ia, ir, ig, ib));
    }

    /**
     * Lighten a color by percent.
     * 
     * @param color
     * @param percent 0.0 - 1.0
     * @return A new, lighter color.
     */
    public static int lightterByPercent(int color, float percent) {
        // TODO We may try an HSV approach...
        // float[] hsv = new float[3];
        // Color.colorToHSV(color, hsv);
        // hsv[2] *= (1 + percent);
        // return Color.HSVToColor(hsv);
        float r = Color.red(color) * (1 + percent);
        float g = Color.green(color) * (1 + percent);
        float b = Color.blue(color) * (1 + percent);
        int ir = Math.min(255, (int) r);
        int ig = Math.min(255, (int) g);
        int ib = Math.min(255, (int) b);
        int ia = Color.alpha(color);
        return (Color.argb(ia, ir, ig, ib));
    }

    /**
     * Append an alpha value to the given color.
     * 
     * @param color
     * @param alpha a 0.0 to a 1.0 alpha value (will be translated to 0-255)
     * @return The new color value.
     */
    public static int colorWithAlpha(int color, float alpha) {
        if (alpha < 0f) {
            alpha = 0f;
        } else if (alpha > 1f) {
            alpha = 1f;
        }
        return Color.argb((int) (alpha * 255), Color.red(color), Color.green(color),
                Color.blue(color));
    }

    /**
     * Sets the Hue value on a view that has a colored background. In case the
     * view's background is not a {@link ColorDrawable}, or does not contain one
     * in a {@link LayerDrawable}, nothing will be applied.
     * 
     * @param view
     * @param hue
     */
    public static void setHue(View view, float hue) {
        ColorDrawable colorDrawable = getColorDrawableBackground(view);
        if (colorDrawable != null) {
            int color = colorDrawable.getColor();
            float[] hsl = new float[3];
            PXColorUtil.colorToHsl(color, hsl);
            colorDrawable.setColor(PXColorUtil.hslToColor(Color.alpha(color), hue, hsl[1], hsl[2]));
        }
    }

    /**
     * Sets the Saturation value on a view that has a colored background. In
     * case the view's background is not a {@link ColorDrawable}, or does not
     * contain one in a {@link LayerDrawable}, nothing will be applied.
     * 
     * @param view
     * @param saturation
     */
    public static void setSaturation(View view, float saturation) {
        ColorDrawable colorDrawable = getColorDrawableBackground(view);
        if (colorDrawable != null) {
            int color = colorDrawable.getColor();
            float[] hsl = new float[3];
            PXColorUtil.colorToHsl(color, hsl);
            colorDrawable.setColor(PXColorUtil.hslToColor(Color.alpha(color), hsl[0], saturation,
                    hsl[2]));
        }
    }

    /**
     * Sets the Brightness value on a view that has a colored background. In
     * case the view's background is not a {@link ColorDrawable}, or does not
     * contain one in a {@link LayerDrawable}, nothing will be applied.
     * 
     * @param view
     * @param brightness
     */
    public static void setBrightness(View view, float brightness) {
        ColorDrawable colorDrawable = getColorDrawableBackground(view);
        if (colorDrawable != null) {
            int color = colorDrawable.getColor();
            float[] hsl = new float[3];
            PXColorUtil.colorToHsl(color, hsl);
            colorDrawable.setColor(PXColorUtil.hslToColor(Color.alpha(color), hsl[0], hsl[1],
                    brightness));
        }
    }

    /**
     * Returns the HSL value of a view that has a colored background. In case
     * the view's background is not a {@link ColorDrawable}, or does not contain
     * a color-drawable in one of its layers, the return value is
     * <code>null</code>
     * 
     * @param view
     * @return The hue value (<code>null</code> in case the background is not a
     *         {@link ColorDrawable})
     */
    public static float[] getHSL(View view) {
        ColorDrawable colorDrawable = getColorDrawableBackground(view);
        if (colorDrawable != null) {
            int color = colorDrawable.getColor();
            float[] hsl = new float[3];
            PXColorUtil.colorToHsl(color, hsl);
            return hsl;
        }
        return null;
    }

    /**
     * Sets the color for a view with a colored background. In case the view's
     * background is not a {@link ColorDrawable}, or does not contain a
     * color-drawable in one of its layers, nothing happens.
     * 
     * @param view
     * @param color
     */
    public static void setColor(View view, int color) {
        ColorDrawable colorDrawable = getColorDrawableBackground(view);
        if (colorDrawable != null) {
            colorDrawable.setColor(color);
        }
    }

    /**
     * Returns the background color value for a View that have a
     * {@link ColorDrawable} background, or a {@link LayerDrawable} background
     * that contains one.
     * 
     * @param view
     * @return The view's background color. -1 in case the view does not have a
     *         ColorDrawable background.
     */
    public static int getColor(View view) {
        ColorDrawable colorDrawable = getColorDrawableBackground(view);
        if (colorDrawable != null) {
            return colorDrawable.getColor();
        }
        return -1;
    }

    /**
     * Returns the View's {@link ColorDrawable} background in case it has one.
     * The {@link ColorDrawable} may be set directly as the View's background,
     * or nested within a {@link LayerDrawable}. In case of a
     * {@link LayerDrawable}, the method will return the first color-drawable it
     * finds.
     * 
     * @param view
     * @return A {@link ColorDrawable}, or <code>null</code> in case not found.
     */
    private static ColorDrawable getColorDrawableBackground(View view) {
        if (view != null) {
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable) {
                return (ColorDrawable) background;
            }
            if (background instanceof LayerDrawable) {
                LayerDrawable layeredBG = (LayerDrawable) background;
                int numberOfLayers = layeredBG.getNumberOfLayers();
                for (int i = 0; i < numberOfLayers; i++) {
                    if (layeredBG.getDrawable(i) instanceof ColorDrawable) {
                        return (ColorDrawable) layeredBG.getDrawable(i);
                    }
                }
            }
        }
        return null;
    }
}
