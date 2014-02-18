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

import android.graphics.Color;

/**
 * A Color map from SVG color names to Android Color values.
 */
public class SVGColors {

    private static Map<String, Integer> SVG_COLORS;

    /**
     * Returns an Android color value for a given SVG color string. If no match
     * is found, Color.BLACK is returned.
     * 
     * @param SVGColorString
     * @return A color
     */
    public static int get(String SVGColorString) {
        synchronized (SVGColors.class) {
            if (SVG_COLORS == null) {
                loadSVGColors();
            }
        }

        Integer result = SVG_COLORS.get(SVGColorString);
        if (result == null) {
            return Color.BLACK;
        }
        return result;

    }

    public static boolean has(String colorName) {
        synchronized (SVGColors.class) {
            if (SVG_COLORS == null) {
                loadSVGColors();
            }
        }
        return SVG_COLORS.containsKey(colorName);
    }

    /**
     * Release the memory this SVG colors class holds. Call this method only
     * when this class is no longer needed. After calling this, any call to
     * {@link #get(String)} will cause for a reload of the SVG color map.
     */
    public static void release() {
        SVG_COLORS = null;
    }

    /**
     * Load the SVG colors map.
     */
    private static void loadSVGColors() {
        SVG_COLORS = new HashMap<String, Integer>();
        SVG_COLORS.put("aliceblue", Color.rgb(240, 248, 255));
        SVG_COLORS.put("antiquewhite", Color.rgb(250, 235, 215));
        SVG_COLORS.put("aqua", Color.rgb(0, 255, 255));
        SVG_COLORS.put("aquamarine", Color.rgb(127, 255, 212));
        SVG_COLORS.put("azure", Color.rgb(240, 255, 255));
        SVG_COLORS.put("beige", Color.rgb(245, 245, 220));
        SVG_COLORS.put("bisque", Color.rgb(255, 228, 196));
        SVG_COLORS.put("black", Color.rgb(0, 0, 0));
        SVG_COLORS.put("blanchedalmond", Color.rgb(255, 235, 205));
        SVG_COLORS.put("blue", Color.rgb(0, 0, 255));
        SVG_COLORS.put("blueviolet", Color.rgb(138, 43, 226));
        SVG_COLORS.put("brown", Color.rgb(165, 42, 42));
        SVG_COLORS.put("burlywood", Color.rgb(222, 184, 135));
        SVG_COLORS.put("cadetblue", Color.rgb(95, 158, 160));
        SVG_COLORS.put("chartreuse", Color.rgb(127, 255, 0));
        SVG_COLORS.put("chocolate", Color.rgb(210, 105, 30));
        SVG_COLORS.put("coral", Color.rgb(255, 127, 80));
        SVG_COLORS.put("cornflowerblue", Color.rgb(100, 149, 237));
        SVG_COLORS.put("cornsilk", Color.rgb(255, 248, 220));
        SVG_COLORS.put("crimson", Color.rgb(220, 20, 60));
        SVG_COLORS.put("cyan", Color.rgb(0, 255, 255));
        SVG_COLORS.put("darkblue", Color.rgb(0, 0, 139));
        SVG_COLORS.put("darkcyan", Color.rgb(0, 139, 139));
        SVG_COLORS.put("darkgoldenrod", Color.rgb(184, 134, 11));
        SVG_COLORS.put("darkgray", Color.rgb(169, 169, 169));
        SVG_COLORS.put("darkgreen", Color.rgb(0, 100, 0));
        SVG_COLORS.put("darkgrey", Color.rgb(169, 169, 169));
        SVG_COLORS.put("darkkhaki", Color.rgb(189, 183, 107));
        SVG_COLORS.put("darkmagenta", Color.rgb(139, 0, 139));
        SVG_COLORS.put("darkolivegreen", Color.rgb(85, 107, 47));
        SVG_COLORS.put("darkorange", Color.rgb(255, 140, 0));
        SVG_COLORS.put("darkorchid", Color.rgb(153, 50, 204));
        SVG_COLORS.put("darkred", Color.rgb(139, 0, 0));
        SVG_COLORS.put("darksalmon", Color.rgb(233, 150, 122));
        SVG_COLORS.put("darkseagreen", Color.rgb(143, 188, 143));
        SVG_COLORS.put("darkslateblue", Color.rgb(72, 61, 139));
        SVG_COLORS.put("darkslategray", Color.rgb(47, 79, 79));
        SVG_COLORS.put("darkslategrey", Color.rgb(47, 79, 79));
        SVG_COLORS.put("darkturquoise", Color.rgb(0, 206, 209));
        SVG_COLORS.put("darkviolet", Color.rgb(148, 0, 211));
        SVG_COLORS.put("deeppink", Color.rgb(255, 20, 147));
        SVG_COLORS.put("deepskyblue", Color.rgb(0, 191, 255));
        SVG_COLORS.put("dimgray", Color.rgb(105, 105, 105));
        SVG_COLORS.put("dimgrey", Color.rgb(105, 105, 105));
        SVG_COLORS.put("dodgerblue", Color.rgb(30, 144, 255));
        SVG_COLORS.put("firebrick", Color.rgb(178, 34, 34));
        SVG_COLORS.put("floralwhite", Color.rgb(255, 250, 240));
        SVG_COLORS.put("forestgreen", Color.rgb(34, 139, 34));
        SVG_COLORS.put("fuchsia", Color.rgb(255, 0, 255));
        SVG_COLORS.put("gainsboro", Color.rgb(220, 220, 220));
        SVG_COLORS.put("ghostwhite", Color.rgb(248, 248, 255));
        SVG_COLORS.put("gold", Color.rgb(255, 215, 0));
        SVG_COLORS.put("goldenrod", Color.rgb(218, 165, 32));
        SVG_COLORS.put("gray", Color.rgb(128, 128, 128));
        SVG_COLORS.put("green", Color.rgb(0, 128, 0));
        SVG_COLORS.put("greenyellow", Color.rgb(173, 255, 47));
        SVG_COLORS.put("grey", Color.rgb(128, 128, 128));
        SVG_COLORS.put("honeydew", Color.rgb(240, 255, 240));
        SVG_COLORS.put("hotpink", Color.rgb(255, 105, 180));
        SVG_COLORS.put("indianred", Color.rgb(205, 92, 92));
        SVG_COLORS.put("indigo", Color.rgb(75, 0, 130));
        SVG_COLORS.put("ivory", Color.rgb(255, 255, 240));
        SVG_COLORS.put("khaki", Color.rgb(240, 230, 140));
        SVG_COLORS.put("lavender", Color.rgb(230, 230, 250));
        SVG_COLORS.put("lavenderblush", Color.rgb(255, 240, 245));
        SVG_COLORS.put("lawngreen", Color.rgb(124, 252, 0));
        SVG_COLORS.put("lemonchiffon", Color.rgb(255, 250, 205));
        SVG_COLORS.put("lightblue", Color.rgb(173, 216, 230));
        SVG_COLORS.put("lightcoral", Color.rgb(240, 128, 128));
        SVG_COLORS.put("lightcyan", Color.rgb(224, 255, 255));
        SVG_COLORS.put("lightgoldenrodyellow", Color.rgb(250, 250, 210));
        SVG_COLORS.put("lightgray", Color.rgb(211, 211, 211));
        SVG_COLORS.put("lightgreen", Color.rgb(144, 238, 144));
        SVG_COLORS.put("lightgrey", Color.rgb(211, 211, 211));
        SVG_COLORS.put("lightpink", Color.rgb(255, 182, 193));
        SVG_COLORS.put("lightsalmon", Color.rgb(255, 160, 122));
        SVG_COLORS.put("lightseagreen", Color.rgb(32, 178, 170));
        SVG_COLORS.put("lightskyblue", Color.rgb(135, 206, 250));
        SVG_COLORS.put("lightslategray", Color.rgb(119, 136, 153));
        SVG_COLORS.put("lightslategrey", Color.rgb(119, 136, 153));
        SVG_COLORS.put("lightsteelblue", Color.rgb(176, 196, 222));
        SVG_COLORS.put("lightyellow", Color.rgb(255, 255, 224));
        SVG_COLORS.put("lime", Color.rgb(0, 255, 0));
        SVG_COLORS.put("limegreen", Color.rgb(50, 205, 50));
        SVG_COLORS.put("linen", Color.rgb(250, 240, 230));
        SVG_COLORS.put("magenta", Color.rgb(255, 0, 255));
        SVG_COLORS.put("maroon", Color.rgb(128, 0, 0));
        SVG_COLORS.put("mediumaquamarine", Color.rgb(102, 205, 170));
        SVG_COLORS.put("mediumblue", Color.rgb(0, 0, 205));
        SVG_COLORS.put("mediumorchid", Color.rgb(186, 85, 211));
        SVG_COLORS.put("mediumpurple", Color.rgb(147, 112, 219));
        SVG_COLORS.put("mediumseagreen", Color.rgb(60, 179, 113));
        SVG_COLORS.put("mediumslateblue", Color.rgb(123, 104, 238));
        SVG_COLORS.put("mediumspringgreen", Color.rgb(0, 250, 154));
        SVG_COLORS.put("mediumturquoise", Color.rgb(72, 209, 204));
        SVG_COLORS.put("mediumvioletred", Color.rgb(199, 21, 133));
        SVG_COLORS.put("midnightblue", Color.rgb(25, 25, 112));
        SVG_COLORS.put("mintcream", Color.rgb(245, 255, 250));
        SVG_COLORS.put("mistyrose", Color.rgb(255, 228, 225));
        SVG_COLORS.put("moccasin", Color.rgb(255, 228, 181));
        SVG_COLORS.put("navajowhite", Color.rgb(255, 222, 173));
        SVG_COLORS.put("navy", Color.rgb(0, 0, 128));
        SVG_COLORS.put("oldlace", Color.rgb(253, 245, 230));
        SVG_COLORS.put("olive", Color.rgb(128, 128, 0));
        SVG_COLORS.put("olivedrab", Color.rgb(107, 142, 35));
        SVG_COLORS.put("orange", Color.rgb(255, 165, 0));
        SVG_COLORS.put("orangered", Color.rgb(255, 69, 0));
        SVG_COLORS.put("orchid", Color.rgb(218, 112, 214));
        SVG_COLORS.put("palegoldenrod", Color.rgb(238, 232, 170));
        SVG_COLORS.put("palegreen", Color.rgb(152, 251, 152));
        SVG_COLORS.put("paleturquoise", Color.rgb(175, 238, 238));
        SVG_COLORS.put("palevioletred", Color.rgb(219, 112, 147));
        SVG_COLORS.put("papayawhip", Color.rgb(255, 239, 213));
        SVG_COLORS.put("peachpuff", Color.rgb(255, 218, 185));
        SVG_COLORS.put("peru", Color.rgb(205, 133, 63));
        SVG_COLORS.put("pink", Color.rgb(255, 192, 203));
        SVG_COLORS.put("plum", Color.rgb(221, 160, 221));
        SVG_COLORS.put("powderblue", Color.rgb(176, 224, 230));
        SVG_COLORS.put("purple", Color.rgb(128, 0, 128));
        SVG_COLORS.put("red", Color.rgb(255, 0, 0));
        SVG_COLORS.put("rosybrown", Color.rgb(188, 143, 143));
        SVG_COLORS.put("royalblue", Color.rgb(65, 105, 225));
        SVG_COLORS.put("saddlebrown", Color.rgb(139, 69, 19));
        SVG_COLORS.put("salmon", Color.rgb(250, 128, 114));
        SVG_COLORS.put("sandybrown", Color.rgb(244, 164, 96));
        SVG_COLORS.put("seagreen", Color.rgb(46, 139, 87));
        SVG_COLORS.put("seashell", Color.rgb(255, 245, 238));
        SVG_COLORS.put("sienna", Color.rgb(160, 82, 45));
        SVG_COLORS.put("silver", Color.rgb(192, 192, 192));
        SVG_COLORS.put("skyblue", Color.rgb(135, 206, 235));
        SVG_COLORS.put("slateblue", Color.rgb(106, 90, 205));
        SVG_COLORS.put("slategray", Color.rgb(112, 128, 144));
        SVG_COLORS.put("slategrey", Color.rgb(112, 128, 144));
        SVG_COLORS.put("snow", Color.rgb(255, 250, 250));
        SVG_COLORS.put("springgreen", Color.rgb(0, 255, 127));
        SVG_COLORS.put("steelblue", Color.rgb(70, 130, 180));
        SVG_COLORS.put("tan", Color.rgb(210, 180, 140));
        SVG_COLORS.put("teal", Color.rgb(0, 128, 128));
        SVG_COLORS.put("thistle", Color.rgb(216, 191, 216));
        SVG_COLORS.put("tomato", Color.rgb(255, 99, 71));
        SVG_COLORS.put("turquoise", Color.rgb(64, 224, 208));
        SVG_COLORS.put("violet", Color.rgb(238, 130, 238));
        SVG_COLORS.put("wheat", Color.rgb(245, 222, 179));
        SVG_COLORS.put("white", Color.rgb(255, 255, 255));
        SVG_COLORS.put("whitesmoke", Color.rgb(245, 245, 245));
        SVG_COLORS.put("yellow", Color.rgb(255, 255, 0));
        SVG_COLORS.put("yellowgreen", Color.rgb(154, 205, 50));
    }
}
