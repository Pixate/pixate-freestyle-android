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

import java.util.HashMap;
import java.util.Map;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.pixate.freestyle.cg.math.PXOffsets;
import com.pixate.freestyle.cg.views.PXShapeView;
import com.pixate.freestyle.util.ObjectUtil;

/**
 * A top-level container for PXShapes. This is used to define the bounds of a
 * set of PXShapes. It also is a centralized container for all shape ids
 * allowing you to retrieve shapes in a scene by their id.
 */
/*
 * In several places herein we call methods that we have ourselves deprecated.
 * Hopefully we can change these. Until then, we suppress the warnings.
 */
@SuppressWarnings("deprecation")
public class PXShapeDocument implements PXRenderable {

    protected Map<String, PXRenderable> nameToShape;
    /**
     * The top-level shape rendered by this scene. If a collection of shapes are
     * needed, then this shape will need to be a PXShapeGroup
     */
    protected PXRenderable shape;
    /**
     * The bounds of the shapes in this scene
     */
    protected RectF bounds;
    /**
     * The top-level view that this scene belongs to
     */
    protected PXShapeView parentView;
    protected Matrix transform;
    protected PXOffsets padding;

    /**
     * Constructs a new PXScene
     */
    public PXShapeDocument() {
        transform = new Matrix();
    }

    /**
     * Sets the bounds of the shapes in this scene.
     * 
     * @param bounds the bounds to set
     */
    public void setBounds(RectF bounds) {
        if (ObjectUtil.areEqual(bounds, this.bounds)) {
            return;
        }
        this.bounds = bounds;
        // TODO: handle top-left as well
        if (shape instanceof PXShapeGroup) {
            PXShapeGroup group = (PXShapeGroup) shape;

            group.setWidth(bounds.width());
            group.setHeight(bounds.height());
        }
    }

    /**
     * Returns the bounds of the shapes in this scene.
     * 
     * @return The bounds of the scene.
     */
    public RectF getBounds() {
        return bounds;
    }

    /**
     * Returns a <code>null</code> parent for the scene.
     * 
     * @see com.pixate.freestyle.cg.shapes.PXRenderable#getParent()
     */
    public PXRenderable getParent() {
        return null;
    }

    public PXOffsets getPadding() {
        return padding;
    }

    public void setPadding(PXOffsets padding) {
        if (!ObjectUtil.areEqual(padding, this.padding)) {
            this.padding = padding;
        }
        // TODO Should we propagate the padding to the children?
    }

    /**
     * Does nothing since scenes are always the top-most parent.
     * 
     * @see com.pixate.freestyle.cg.shapes.PXRenderable#setParent(com.pixate.freestyle.cg.shapes.PXRenderable)
     */
    public void setParent(PXRenderable parent) {
        // No Op - scenes are always the top-most parent
    }

    /**
     * Sets the top-level shape rendered by this scene. If a collection of
     * shapes are needed, then this shape will need to be a PXShapeGroup
     * 
     * @param shape A {@link PXRenderable} shape instance.
     */
    public void setShape(PXRenderable shape) {
        if (!ObjectUtil.areEqual(shape, this.shape)) {
            // disconnect parent from old shape
            if (this.shape != null) {
                this.shape.setParent(null);
            }

            this.shape = shape;

            // connect parent on new shape
            if (this.shape != null) {
                this.shape.setParent(this);
                // XXX - Android only
                if (this.shape instanceof PXShapeGroup) {
                    PXShapeGroup group = (PXShapeGroup) this.shape;
                    setBounds(new RectF(0, 0, group.getWidth(), group.getHeight()));
                }
            }
        }
    }

    /**
     * Returns the top-level shape rendered by this scene. If a collection of
     * shapes exists in the scene, then this shape will be a PXShapeGroup.
     */
    public PXRenderable getShape() {
        return shape;
    }

    /**
     * Return the shape in this scene with the specified name.
     * 
     * @param name The name of the shape
     * @returns A {@link PXRenderable} or <code>null</code>.
     */
    public PXRenderable getShape(String name) {
        if (nameToShape != null && name != null) {
            return nameToShape.get(name);
        }
        return null;
    }

    /**
     * Register a shape with the specified name with this scene.
     * 
     * @param shape The shape to register
     * @param name The shape's name
     */
    public void addShape(String name, PXRenderable shape) {
        if (name != null && shape != null) {
            if (nameToShape == null) {
                nameToShape = new HashMap<String, PXRenderable>(6);
            }
            nameToShape.put(name, shape);
        }
    }

    /**
     * Returns the top-level view that this scene belongs to.
     */
    public PXShapeView getParentView() {
        return parentView;
    }

    /**
     * Sets the top-level view that this scene belongs to.
     * 
     * @param parentView
     */
    public void setParentView(PXShapeView parentView) {
        this.parentView = parentView;
    }

    /**
     * Sets the opacity for the scene. In case a shape was not set prior to this
     * call, nothing happens.
     * 
     * @param opacity An opacity value between 0.0 to 1.0.
     */
    public void setOpacity(float opacity) {
        if (shape instanceof PXShapeGroup) {
            ((PXShapeGroup) shape).setOpacity(opacity);
        }
    }

    /**
     * Returns the scene opacity. In case a shape was not set prior to this
     * call, return 1.0F.
     * 
     * @return The scene's opacity
     */
    public float getOpacity() {
        if (shape instanceof PXShapeGroup) {
            return ((PXShapeGroup) shape).getOpacity();
        }
        return 1F;
    }

    public float getHeight() {
        if (shape instanceof PXShapeGroup) {
            return ((PXShapeGroup) shape).getHeight();
        }
        return 0F;
    }

    public float getWidth() {
        if (shape instanceof PXShapeGroup) {
            return ((PXShapeGroup) shape).getWidth();
        }
        return 0F;
    }

    public void setViewport(RectF viewport) {
        if (shape instanceof PXShapeGroup) {
            ((PXShapeGroup) shape).setViewport(viewport);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.cg.PXRenderable#getTransform()
     */
    public Matrix getTransform() {
        return transform;
    }

    /**
     * @param transform the transform to set. In case <code>null</code>, nothing
     *            will be set.
     */
    public void setTransform(Matrix transform) {
        if (transform != null) {
            this.transform = transform;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.pxengine.cg.PXRenderable#render(android.graphics
     * .Canvas)
     */
    public void render(Canvas context) {
        if (shape != null) {
            if (transform != null) {
                context.concat(transform);
            }
            shape.render(context);
        }
    }

    /**
     * This implementation for render in the {@link PXShapeDocument} does
     * exactly what the {@link #render(Canvas)} does.
     * 
     * @see com.pixate.freestyle.cg.shapes.PXRenderable#render(android.graphics.Canvas,
     *      boolean)
     * @see #render(Canvas)
     */
    public void render(Canvas context, boolean cache) {
        render(context);
    }

    public Drawable renderToImage(RectF bounds, boolean opaque) {
        if (shape != null) {
            return shape.renderToImage(bounds, opaque);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.shapes.PXRenderable#isAsynchronous()
     */
    @Override
    public boolean isAsynchronous() {
        return shape != null && shape.isAsynchronous();
    }
}
