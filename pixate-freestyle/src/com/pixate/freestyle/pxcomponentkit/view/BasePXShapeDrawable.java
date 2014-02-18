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
 * 
 */
package com.pixate.freestyle.pxcomponentkit.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.pixate.freestyle.cg.shapes.PXShapeDocument;

/**
 * Base class for PX drawables.
 */
public abstract class BasePXShapeDrawable extends Drawable {

    protected PXShapeDocument scene;
    private int minHeight;
    private int minWeight;

    public BasePXShapeDrawable(int minHeight, int minWeight) {
        this.minHeight = minHeight;
        this.minWeight = minWeight;
    }

    public BasePXShapeDrawable() {
    }

    /*
     * (non-Javadoc)
     * @see android.graphics.drawable.Drawable#draw(android.graphics.Canvas)
     */
    @Override
    public void draw(Canvas canvas) {
        loadScene();
        if (scene != null) {
            // Call renderAll that will draw into a Picture for better
            // performance.
            scene.render(canvas);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.graphics.drawable.Drawable#setAlpha(int)
     */
    @Override
    public void setAlpha(int alpha) {
        loadScene();
        if (scene != null) {
            scene.setOpacity(alpha / 255.0F);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.graphics.drawable.Drawable#setColorFilter(android.graphics.
     * ColorFilter)
     */
    @Override
    public void setColorFilter(ColorFilter cf) {
        // not supported
    }

    /*
     * (non-Javadoc)
     * @see android.graphics.drawable.Drawable#getOpacity()
     */
    @Override
    public int getOpacity() {
        if (scene == null) {
            loadScene();
        }
        if (scene != null) {
            return (int) (scene.getOpacity() * 255);
        }
        return 0;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (scene == null) {
            loadScene();
        }
        if (scene != null) {
            scene.setViewport(new RectF(bounds));
        }
    }

    @Override
    public int getMinimumHeight() {
        return Math.max(minHeight, super.getMinimumHeight());
    }

    @Override
    public int getMinimumWidth() {
        return Math.max(minWeight, super.getMinimumWidth());
    }

    @Override
    public int getIntrinsicHeight() {
        if (scene == null) {
            loadScene();
        }
        if (scene != null) {
            return (int) Math.max(1, scene.getHeight());
        }
        return -1;
    }

    @Override
    public int getIntrinsicWidth() {
        if (scene == null) {
            loadScene();
        }
        if (scene != null) {
            return (int) Math.max(1, scene.getWidth());
        }
        return -1;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        // update the scene bounds
        if (scene != null) {
            scene.setBounds(new RectF(left, top, right, bottom));
        }
        super.setBounds(left, top, right, bottom);
    }

    /**
     * Reset the drawing scene that this drawable draws. The next call to the
     * {@link #draw(Canvas)} will try to recreate this scene.
     */
    public void resetScene() {
        scene = null;
    }

    /**
     * Returns a {@link PXShapeDocument} to be rendered on the
     * {@link #draw(Canvas)} call. A scene may be cashed, or loaded on every
     * call.
     * 
     * @return A {@link PXShapeDocument}
     */
    public abstract PXShapeDocument loadScene();
}
