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
package com.pixate.freestyle.cg.shapes;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.pixate.freestyle.cg.math.PXOffsets;

/**
 * The PXRenderable holds properties needed when describing the structure of
 * content rendered to a canvas (context).
 */
public interface PXRenderable {

    /**
     * Returns the PXRenderable that this shape belongs to
     * 
     * @return A {@link PXRenderable} (can be <code>null</code>)
     */
    PXRenderable getParent();

    /**
     * Sets a renderable parent.
     * 
     * @param parent A {@link PXRenderable}
     */
    void setParent(PXRenderable parent);

    /**
     * Returns a transformation {@link Matrix} to be applied to this shape
     * during rendering.
     * 
     * @return A {@link Matrix}
     */
    Matrix getTransform();

    /**
     * Returns the padding to be applied to this instance during rendering
     */
    PXOffsets getPadding();

    /**
     * Sets the padding to be applied to this instance during rendering
     */
    void setPadding(PXOffsets padding);

    /**
     * The method responsible for painting this shape to the specified
     * {@link Canvas} context. By default, calling this render will cache a
     * {@link Picture} that will hold the drawing instructions. This method is
     * like calling {@link #render(Canvas, boolean)} with
     * <code>cache=true</code>.
     * 
     * @param context A {@link Canvas}
     */
    void render(Canvas context);

    /**
     * The method responsible for painting this shape to the specified
     * {@link Canvas} context.
     * 
     * @param context A {@link Canvas}
     * @param cache In case true, a new {@link Picture} will be created and the
     *            rendering will be done by writing into it.
     */
    void render(Canvas context, boolean cache);

    /**
     * TODO<br>
     * Render this shape within the specified bounds and return that as a
     * UIImage
     * 
     * @param bounds The bounds which establishes the view bounds and the
     *            resulting image size
     * @param opaque Determine if the resulting image should have an alph
     *            channel or not
     * @returns A UIImage of the rendered shape
     */
    public Drawable renderToImage(RectF bounds, boolean opaque);

    /**
     * Returns <code>true</code> if this {@link PXRectangle} should be loaded
     * asynchronously. This will be true, for example, when the
     * {@link PXRectangle} is loading a remote image.
     * 
     * @return <code>true</code> if this {@link PXRectangle} is asynchronous;
     *         <code>false</code> otherwise.
     */
    boolean isAsynchronous();

}
