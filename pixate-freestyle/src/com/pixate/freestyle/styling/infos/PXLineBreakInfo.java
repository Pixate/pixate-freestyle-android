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

import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;

/**
 * For bridging iOS NSLineBreakMode to Android's more limited
 * {@link android.text.TextUtils.TruncateAt}. It could be we don't use this
 * class in the long run. We definitely need to discuss it. I'm putting it here
 * for now as I race through converting PXDeclaration to Android.
 * 
 * @author Bill Dawson
 */
public class PXLineBreakInfo {

    public enum PXLineBreakMode {
        WORD_WRAP("word-wrap", null),
        CHAR_WRAP("character-wrap", null),
        CLIP("clip", null),
        TRUNCATE_HEAD("ellipsis-head", TruncateAt.START),
        TRUNCATE_TAIL("ellipsis-tail", TruncateAt.END),
        TRUNCATE_MIDDLE("ellipsis-middle", TruncateAt.MIDDLE);

        private final String cssValue;
        private final TextUtils.TruncateAt androidValue;

        private PXLineBreakMode(String cssValue, TextUtils.TruncateAt androidValue) {
            this.cssValue = cssValue;
            this.androidValue = androidValue;
        }

        private static Map<String, PXLineBreakMode> cssValueToEnumMap;
        static {
            cssValueToEnumMap = new HashMap<String, PXLineBreakMode>(6);
            for (PXLineBreakMode mode : PXLineBreakMode.values()) {
                cssValueToEnumMap.put(mode.getCssValue(), mode);
            }
        }

        public String getCssValue() {
            return this.cssValue;
        }

        public TextUtils.TruncateAt getAndroidValue() {
            return this.androidValue;
        }

        public static PXLineBreakMode ofCssValue(String cssValue) {
            return cssValueToEnumMap.get(cssValue);
        }

    }

}
