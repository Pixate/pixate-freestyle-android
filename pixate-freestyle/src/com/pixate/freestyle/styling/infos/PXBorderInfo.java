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
package com.pixate.freestyle.styling.infos;

import java.util.HashMap;
import java.util.Map;

import com.pixate.freestyle.cg.paints.PXPaint;

public class PXBorderInfo {

    public enum PXBorderStyle {
        NONE,
        HIDDEN,
        DOTTED,
        DASHED,
        SOLID,
        DOUBLE,
        GROOVE,
        RIDGE,
        INSET,
        OUTSET
    }

    public enum PXTextBorderStyle {
        NONE(null),
        LINE("line"),
        BEZEL("bezel"),
        ROUNDED_RECT("rounded-rect");

        private final String cssValue;
        private static Map<String, PXTextBorderStyle> cssValueToEnum;

        static {
            cssValueToEnum = new HashMap<String, PXTextBorderStyle>(3);
            for (PXTextBorderStyle style : PXTextBorderStyle.values()) {
                if (style.cssValue != null) {
                    cssValueToEnum.put(style.cssValue, style);
                }
            }
        }

        private PXTextBorderStyle(String cssValue) {
            this.cssValue = cssValue;
        }

        public static PXTextBorderStyle ofCssValue(String cssValue) {
            return cssValueToEnum.get(cssValue);
        }

    }

    private float width;
    private PXBorderStyle style;
    private PXPaint paint;

    public PXBorderInfo() {
    }

    public void setWidth(float value) {
        width = value;
    }

    public float getWidth() {
        return width;
    }

    public void setStyle(PXBorderStyle value) {
        style = value;
    }

    public PXBorderStyle getStyle() {
        return style;
    }

    public void setPaint(PXPaint value) {
        paint = value;
    }

    public PXPaint getPaint() {
        return paint;
    }

    public boolean hasContent() {
        return width > 0.0f && style != PXBorderStyle.NONE && style != PXBorderStyle.HIDDEN && paint != null;
    }

    public boolean isOpaque() {
        // TODO: Take border style into account (comment in Xcode)

        return width == 0.0f || (paint != null && paint.isOpaque());
    }

}
