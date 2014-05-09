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
package com.pixate.freestyle.cg.paints;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Xfermode;

/**
 * The PXPaint protocol specifies the properties and methods required for a
 * class to be used for filling of a contour
 */
public interface PXPaint {

    /**
     * A method used to apply the implementations fill to the specified
     * {@link Path} in the given {@link Canvas} context.
     * 
     * @param path The path to which the fill is to be applied
     * @param context The {@link Canvas} context within which the fill is to be
     *            rendered
     * @see #isAsynchronous()
     */
    void applyFillToPath(Path path, Paint paint, Canvas context);

    /**
     * Sets the blend mode to use when applying this fill
     */
    void setBleningMode(Xfermode mode);

    /**
     * Returns the blend mode to use when applying this fill.
     * 
     * @return {@link Xfermode} May be <code>null</code>
     */
    Xfermode getBleningMode();

    /**
     * Create a copy of this paint with colors lightened by the specified
     * percent
     * 
     * @param percent A float between 0 and 100 inclusive
     */
    PXPaint lightenByPercent(float percent);

    /**
     * Create a copy of this paint with colors darkened by the specified percent
     * 
     * @param percent A float between 0 and 100 inclusive
     */
    PXPaint darkenByPercent(float percent);

    /**
     * Determine if this paint requires rendering with alpha
     */
    boolean isOpaque();

    /**
     * Returns <code>true</code> if this paint should be loaded asynchronously.
     * This will be true, for example, when the paint is loading a remote image.
     * Asynchronous {@link PXPaint} instances in a group will cause the whole
     * painting process to run in an asynchronous way, displaying an
     * intermediate content until it's done.
     * 
     * @return <code>true</code> if this {@link PXPaint} is asynchronous;
     *         <code>false</code> otherwise.
     */
    boolean isAsynchronous();
}
