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
import java.util.Set;

public class PXAnimationInfo {

    public enum PXAnimationTimingFunction {
        UNDEFINED(null),
        EASE("ease"), // ease (default)
        LINEAR("linear"), // linear
        EASE_IN("ease-in"), // ease-in
        EASE_OUT("ease-out"), // ease-out
        EASE_IN_OUT("ease-in-out"), // ease-in-out
        STEP_START("step-start"), // step-start
        STEP_END("step-end"); // step-end
        // steps(<integer>[, [ start | end ] ]?)
        // cubic-bezier(<number>, <number>, <number>, <number>)

        private final String cssValue;
        private static final Map<String, PXAnimationTimingFunction> cssValueToEnum;

        static {
            cssValueToEnum = new HashMap<String, PXAnimationTimingFunction>(4);
            for (PXAnimationTimingFunction value : PXAnimationTimingFunction.values()) {
                if (value.cssValue != null) {
                    cssValueToEnum.put(value.cssValue, value);
                }
            }
        }

        private PXAnimationTimingFunction(String cssValue) {
            this.cssValue = cssValue;
        }

        public static PXAnimationTimingFunction ofCssValue(String cssValue) {
            return cssValueToEnum.get(cssValue);
        }

        public static Set<String> getCssValueSet() {
            return cssValueToEnum.keySet();
        }

    }

    public enum PXAnimationDirection {
        UNDEFINED(null),
        NORMAL("normal"), // normal (default)
        REVERSE("reverse"), // reverse
        ALTERNATE("alternate"), // alternate
        ALTERNATE_REVERSE("alternate-reverse"); // alternate-reverse

        private final String cssValue;
        private static final Map<String, PXAnimationDirection> cssValueToEnum;

        static {
            cssValueToEnum = new HashMap<String, PXAnimationDirection>(4);
            for (PXAnimationDirection value : PXAnimationDirection.values()) {
                if (value.cssValue != null) {
                    cssValueToEnum.put(value.cssValue, value);
                }
            }
        }

        private PXAnimationDirection(String cssValue) {
            this.cssValue = cssValue;
        }

        public static PXAnimationDirection ofCssValue(String cssValue) {
            return cssValueToEnum.get(cssValue);
        }

        public static Set<String> getCssValueSet() {
            return cssValueToEnum.keySet();
        }
    }

    public enum PXAnimationPlayState {
        UNDEFINED(null),
        RUNNING("running"), // running (default)
        PAUSED("paused");

        private final String cssValue;
        private static final Map<String, PXAnimationPlayState> cssValueToEnum;

        static {
            cssValueToEnum = new HashMap<String, PXAnimationPlayState>(2);
            for (PXAnimationPlayState value : PXAnimationPlayState.values()) {
                if (value.cssValue != null) {
                    cssValueToEnum.put(value.cssValue, value);
                }
            }
        }

        private PXAnimationPlayState(String cssValue) {
            this.cssValue = cssValue;
        }

        public static PXAnimationPlayState ofCssValue(String cssValue) {
            return cssValueToEnum.get(cssValue);
        }

        public static Set<String> getCssValueSet() {
            return cssValueToEnum.keySet();
        }

    }

    public enum PXAnimationFillMode {
        UNDEFINED(null),
        NONE("none"), // none [default]
        FORWARDS("forwards"), // forwards
        BACKWARDS("backwords"), // backwards
        BOTH("both");

        private final String cssValue;
        private static final Map<String, PXAnimationFillMode> cssValueToEnum;

        static {
            cssValueToEnum = new HashMap<String, PXAnimationFillMode>(4);
            for (PXAnimationFillMode value : PXAnimationFillMode.values()) {
                if (value.cssValue != null) {
                    cssValueToEnum.put(value.cssValue, value);
                }
            }
        }

        private PXAnimationFillMode(String cssValue) {
            this.cssValue = cssValue;
        }

        public static PXAnimationFillMode ofCssValue(String cssValue) {
            return cssValueToEnum.get(cssValue);
        }

        public static Set<String> getCssValueSet() {
            return cssValueToEnum.keySet();
        }
    }

    public String animationName;
    public float animationDuration;
    public PXAnimationTimingFunction animationTimingFunction;
    public int animationIterationCount;
    public PXAnimationDirection animationDirection;
    public PXAnimationPlayState animationPlayState;
    public float animationDelay;
    public PXAnimationFillMode animationFillMode;

    public PXAnimationInfo() {
        this(false);
    }

    public PXAnimationInfo(boolean withCSSDefaults) {
        if (withCSSDefaults) {
            animationName = null;
            animationDuration = animationDelay = 0f;
            animationTimingFunction = PXAnimationTimingFunction.EASE;
            animationIterationCount = 0;
            animationDirection = PXAnimationDirection.NORMAL;
            animationPlayState = PXAnimationPlayState.RUNNING;
            animationFillMode = PXAnimationFillMode.NONE;

        } else {
            animationName = null;
            animationDuration = animationDelay = Float.MAX_VALUE;
            animationTimingFunction = PXAnimationTimingFunction.UNDEFINED;
            animationIterationCount = Integer.MAX_VALUE;
            animationDirection = PXAnimationDirection.UNDEFINED;
            animationPlayState = PXAnimationPlayState.UNDEFINED;
            animationFillMode = PXAnimationFillMode.UNDEFINED;
        }
    }

    public boolean isValid() {
        // @formatter:off
        return animationDuration != Float.MAX_VALUE
                && animationTimingFunction != PXAnimationTimingFunction.UNDEFINED
                && animationIterationCount != Integer.MAX_VALUE
                && animationDirection != PXAnimationDirection.UNDEFINED
                && animationPlayState != PXAnimationPlayState.UNDEFINED
                && animationDelay != Float.MAX_VALUE
                && animationFillMode != PXAnimationFillMode.UNDEFINED;
        // @formatter:on
    }

    public void setUndefinedProperties(PXAnimationInfo source) {
        // Skip animationName

        if (animationDuration == Float.MAX_VALUE) {
            animationDuration = source.animationDuration;
        }

        if (animationTimingFunction == null || animationTimingFunction == PXAnimationTimingFunction.UNDEFINED) {
            animationTimingFunction = source.animationTimingFunction;
        }

        if (animationIterationCount == Integer.MAX_VALUE) {
            animationIterationCount = source.animationIterationCount;
        }

        if (animationDirection == null || animationDirection == PXAnimationDirection.UNDEFINED) {
            animationDirection = source.animationDirection;
        }

        if (animationPlayState == null || animationPlayState == PXAnimationPlayState.UNDEFINED) {
            animationPlayState = source.animationPlayState;
        }

        if (animationDelay == Float.MAX_VALUE) {
            animationDelay = source.animationDelay;
        }

        if (animationFillMode == null || animationFillMode == PXAnimationFillMode.UNDEFINED) {
            animationFillMode = source.animationFillMode;
        }
    }

}
