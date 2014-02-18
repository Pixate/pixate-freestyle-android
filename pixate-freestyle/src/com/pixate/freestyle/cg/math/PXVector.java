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
 * Copyright (c) 2012 Pixate, Inc. All rights reserved.
 */
package com.pixate.freestyle.cg.math;

import java.text.MessageFormat;

import android.graphics.PointF;

public class PXVector {

    protected float x;
    protected float y;

    public PXVector() {
    }

    public PXVector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static PXVector vectorWithStartPoint(PointF p1, PointF p2) {
        return new PXVector(p2.x - p1.x, p2.y - p1.y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float angle() {
        double result = Math.atan2(y, x);
        return (result >= 0) ? (float) result : (float) (result + 2 * Math.PI);
    }

    public float length() {
        return (float) Math.sqrt(magnitude());
    }

    public float magnitude() {
        return x * x + y * y;
    }

    public PXVector perp() {
        return new PXVector(-y, x);
    }

    public PXVector unit() {
        return divide(length());
    }

    public float angleBetweenVector(PXVector that) {
        // float cosTheta = dot:that / (self.magnitude * that.magnitude);
        // return acosf(cosTheta);
        return (float) (Math.atan2(that.y, that.x) - Math.atan2(this.y, this.x));
    }

    public float dot(PXVector that) {
        return this.x * that.x + this.y * that.y;
    }

    public float cross(PXVector that) {
        return this.x * that.y - this.y * that.x;
    }

    public PXVector add(PXVector that) {
        return new PXVector(this.x + that.x, this.y + that.y);
    }

    public PXVector subtract(PXVector that) {
        return new PXVector(this.x - that.x, this.y - that.y);
    }

    public PXVector divide(float scalar) {
        return new PXVector(this.x / scalar, this.y / scalar);
    }

    public PXVector multiply(float scalar) {
        return new PXVector(this.x * scalar, this.y * scalar);
    }

    public PXVector perpendicular(PXVector that) {
        return subtract(projectOnto(that));
    }

    public PXVector projectOnto(PXVector that) {
        float percent = dot(that) / that.magnitude();

        return that.multiply(percent);
    }

    @Override
    public String toString() {
        return MessageFormat.format("Vector(x={0},y={1})", x, y);
    }
}
