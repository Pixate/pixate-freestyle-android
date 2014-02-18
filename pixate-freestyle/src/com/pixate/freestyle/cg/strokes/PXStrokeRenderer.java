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
package com.pixate.freestyle.cg.strokes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * The PXStrokeRenderer protocol specifies a method that a class must implement
 * if it is to be used to render a stroke on a contour.
 */
public interface PXStrokeRenderer {

    /**
     * This method takes in a contour defined by a {@link Path} and applies
     * whatever stroke effect it implements within the specified context
     * 
     * @param path The path contour onto which the stroke is to be applied
     *            will be anti-aliased (e.g. new Paint(Paint.ANTI_ALIAS_FLAG)).
     * @param context The context into which this stroke is to be rendered
     */
    void applyStrokeToPath(Path path, Paint paint, Canvas context);

    /**
     * Returns if this strokes requires rendering with alpha
     */
    boolean isOpaque();
}
