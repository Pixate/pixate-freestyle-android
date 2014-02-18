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
package com.pixate.freestyle.styling.media;

import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.pixate.freestyle.cg.math.PXDimension;

/**
 * Named media expression.<br>
 * TODO - This class needs a lot of testing
 */
public class PXNamedMediaExpression implements PXMediaExpression {

    private interface PXNamedMediaExpressionHandler {
        boolean getValue(Context context, PXNamedMediaExpression expression);
    }

    private static Map<String, PXNamedMediaExpressionHandler> handlers;
    static {
        handlers = new HashMap<String, PXNamedMediaExpression.PXNamedMediaExpressionHandler>();
        handlers.put("orientation", new PXNamedMediaExpressionHandler() {

            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                        .getDefaultDisplay();
                switch (display.getRotation()) {
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        return "landscape".equals(expression.getValue().toString());
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        return "portrait".equals(expression.getValue().toString());
                    default:
                        return false;
                }
            }
        });
        handlers.put("device", new PXNamedMediaExpressionHandler() {
            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                // Seems to work well in detecting an emulator
                return Build.FINGERPRINT.indexOf("generic") == -1;
            }
        });
        handlers.put("device-width", new PXNamedMediaExpressionHandler() {
            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                // TODO - Test this and anything else here that is calling the
                // getSize
                Point size = getSize(context);
                return size.x == expression.getFloatValue(context);
            }
        });
        handlers.put("min-device-width", new PXNamedMediaExpressionHandler() {
            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                Point size = getSize(context);
                return size.y >= expression.getFloatValue(context);
            }
        });
        handlers.put("max-device-width", new PXNamedMediaExpressionHandler() {
            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                Point size = getSize(context);
                return size.x <= expression.getFloatValue(context);
            }
        });
        handlers.put("device-height", new PXNamedMediaExpressionHandler() {
            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                Point size = getSize(context);
                return size.y == expression.getFloatValue(context);
            }
        });
        handlers.put("min-device-height", new PXNamedMediaExpressionHandler() {
            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                Point size = getSize(context);
                return size.y >= expression.getFloatValue(context);
            }
        });
        handlers.put("max-device-height", new PXNamedMediaExpressionHandler() {
            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                Point size = getSize(context);
                return size.y <= expression.getFloatValue(context);
            }
        });
        handlers.put("scale", new PXNamedMediaExpressionHandler() {
            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                DisplayMetrics metrics = getDisplayMetrics(context);
                // TODO - Test this and anything else that reads the density
                return metrics.density == expression.getFloatValue(context);
            }
        });
        handlers.put("min-scale", new PXNamedMediaExpressionHandler() {
            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                DisplayMetrics metrics = getDisplayMetrics(context);
                return metrics.density >= expression.getFloatValue(context);
            }
        });
        handlers.put("max-scale", new PXNamedMediaExpressionHandler() {
            public boolean getValue(Context context, PXNamedMediaExpression expression) {
                DisplayMetrics metrics = getDisplayMetrics(context);
                return metrics.density <= expression.getFloatValue(context);
            }
        });
    }

    private String name;
    private Object value;

    /**
     * Constructs a new PXNamedMediaExpression
     * 
     * @param name
     * @param value
     */
    public PXNamedMediaExpression(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    private float getFloatValue(Context context) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof String) {
            return Float.parseFloat(value.toString());
        } else if (value instanceof PXDimension) {
            PXDimension dimension = (PXDimension) value;

            if (dimension.isLength()) {
                DisplayMetrics metrics = getDisplayMetrics(context);
                return dimension.points(metrics).getNumber();
            } else {
                return 0.0f;
            }
        } else {
            return 0.0f;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.media.PXMediaExpression#matches(android.content
     * .Context)
     */
    public boolean matches(Context context) {
        PXNamedMediaExpressionHandler handler = handlers.get(name);
        return handler != null ? handler.getValue(context, this) : false;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (value != null) {
            return String.format("(%s:%s)", name, value);
        } else {
            return String.format("(%s)", name);
        }
    }

    private static Point getSize(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getRealSizeHCMR1(display, size);
        } else {
            getRealSize(display, size);
        }
        return size;
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void getRealSize(Display display, Point size) {
        display.getRealSize(size);
    }

    private static void getRealSizeHCMR1(Display display, Point size) {
        Class<?> clazz = display.getClass();
        try {
            size.x = (Integer) clazz.getMethod("getRawWidth").invoke(display);
            size.y = (Integer) clazz.getMethod("getRawHeight").invoke(display);
            return;
        } catch (Exception e) {
            // This would be odd given our minSdkVersion requirements.
            // Fall back to the really old way to do this.
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            size.x = metrics.widthPixels;
            size.y = metrics.heightPixels;
        }

    }
}
