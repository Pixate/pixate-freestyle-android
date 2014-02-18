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

/**
 * PXDimensionType
 */
public enum PXDimensionType {
    // TODO - missing Android-specific items like dp/dip
    EMS("em"),
    EXS("ex"),
    PIXELS("px"),
    DEVICE_PIXELS("dpx"),
    CENTIMETERS("cm"),
    MILLIMETERS("mm"),
    INCHES("in"),
    POINTS("pt"),
    PICAS("pc"),
    DEGREES("deg"),
    RADIANS("rad"),
    GRADIANS("grad"),
    MILLISECONDS("ms"),
    SECONDS("s"),
    HERTZ("Hz"),
    KILOHERTZ("kHz"),
    PERCENTAGE("%"),
    USERDEFINED("");

    private String dimType;

    private PXDimensionType(String type) {
        this.dimType = type;
    }

    @Override
    public String toString() {
        return dimType;
    }
}
