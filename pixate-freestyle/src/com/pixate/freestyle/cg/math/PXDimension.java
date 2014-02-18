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

package com.pixate.freestyle.cg.math;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import android.util.DisplayMetrics;

/**
 * Pixate Dimension.
 */
public class PXDimension {
    // TODO: move enum-related logic into PXDimensionType enum
    private static Map<String, PXDimensionType> dimensionMap;
    static {
        dimensionMap = new HashMap<String, PXDimensionType>(16);
        for (PXDimensionType type : EnumSet.allOf(PXDimensionType.class)) {
            dimensionMap.put(type.toString(), type);
        }
    }

    private float number;
    private String dimension;
    private PXDimensionType type;

    /**
     * Constructs a new dimension.
     * 
     * @param number
     * @param dimension
     */
    public PXDimension(float number, String dimension) {
        this.number = number;
        this.dimension = dimension;
        this.type = dimensionMap.get(dimension);
        if (this.type == null) {
            this.type = PXDimensionType.USERDEFINED;
        }
    }

    public boolean isLength() {
        switch (type) {
            case PIXELS:
            case DEVICE_PIXELS:
            case CENTIMETERS:
            case MILLIMETERS:
            case INCHES:
            case POINTS:
            case PICAS:
                return true;
            default:
                return false;
        }
    }

    public boolean isAngle() {
        switch (type) {
            case DEGREES:
            case RADIANS:
            case GRADIANS:
                return true;
            default:
                return false;
        }
    }

    public boolean isTime() {
        switch (type) {
            case MILLISECONDS:
            case SECONDS:
                return true;
            default:
                return false;
        }
    }

    public boolean isMilliseconds() {
        return type == PXDimensionType.MILLISECONDS;
    }

    public boolean isSeconds() {
        return type == PXDimensionType.SECONDS;
    }

    public boolean isFrequency() {
        switch (type) {
            case HERTZ:
            case KILOHERTZ:
                return true;
            default:
                return false;
        }
    }

    public boolean isPercentage() {
        return (type.equals(PXDimensionType.PERCENTAGE));
    }

    public boolean isUserDefined() {
        return (type.equals(PXDimensionType.USERDEFINED));
    }

    public float getNumber() {
        return number;
    }

    public PXDimensionType getType() {
        return type;
    }

    public PXDimension degrees() {
        float result = 0.0f;
        switch (type) {
            case DEGREES:
                result = number;
                break;
            case GRADIANS:
                result = number * 0.9f;
                break;
            case RADIANS:
                result = (float) Math.toDegrees(number);
                break;
            default:
                break;
        }
        return new PXDimension(result, PXDimensionType.DEGREES.toString());
    }

    public PXDimension points(DisplayMetrics displayMetrics) {
        float result = 0.0f;
        switch (type) {
            case PIXELS:
                result = number;
                break;
            case DEVICE_PIXELS:
                // TODO - In that equivalent to [UIScreen mainScreen].scale;
                result = number / displayMetrics.density;
                break;
            case CENTIMETERS:
                result = (float) (number * 28.346456692913);
                break;

            case MILLIMETERS:
                result = (float) (number * 2.8346456692913);
                break;
            case INCHES:
                result = number * 72;
                break;
            case POINTS:
                result = number;
                break;
            case PICAS:
                result = number * 12;
                break;
            default:
                break;
        }
        return new PXDimension(result, PXDimensionType.POINTS.toString());
    }

    public PXDimension radians() {
        float result = 0.0f;

        switch (type) {
            case DEGREES:
                result = (float) Math.toRadians(number);
                break;
            case GRADIANS:
                result = number * 0.015707963267949f;
                break;
            case RADIANS:
                result = number;
                break;

            default:
                break;
        }
        return new PXDimension(result, PXDimensionType.RADIANS.toString());
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + dimension.hashCode();
        hash = hash * 31 + (int) (1000 * number);
        hash = hash * 13 + type.ordinal();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PXDimension) {
            PXDimension other = (PXDimension) o;
            return number == other.number && type == other.type
                    && dimension.equals(other.dimension);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s%s", number, dimension);
    }
}
