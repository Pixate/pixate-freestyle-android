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
package com.pixate.freestyle.pxcomponentkit.view;

import android.graphics.Rect;
import android.graphics.RectF;

import com.pixate.freestyle.cg.paints.PXSolidPaint;
import com.pixate.freestyle.cg.shapes.PXRectangle;
import com.pixate.freestyle.cg.shapes.PXShapeDocument;
import com.pixate.freestyle.cg.shapes.PXShapeGroup;

/**
 * PX swatch view that draws colored rectangles.
 */
public class PXSwatchDrawable extends PXSceneDrawable {

	private int[] colors;

	/**
	 * Constructs a new swatch drawable.
	 * 
	 * @param colors
	 *            The colors of the swatch.
	 */
	public PXSwatchDrawable(int... colors) {
		super(null);
		this.colors = colors;
	}

	/**
	 * Constructs a new swatch drawable.
	 */
	public PXSwatchDrawable() {
		super(null);
	}

	/**
	 * Set the colors for the swatch drawable.
	 * 
	 * @param colors
	 */
	public void setColors(int... colors) {
		this.colors = colors;
		scene = null;
	}

	/**
	 * Returns the colors for the swatch drawable.
	 * 
	 * @return
	 */
	public int[] getColors() {
		return colors;
	}

	/*
	 * Loads the swatch scene.
	 * 
	 * @see com.pixate.freestyle.pxengine.view.PXSceneDrawable#loadScene()
	 */
	@Override
	public PXShapeDocument loadScene() {
		PXShapeDocument swatchScene = new PXShapeDocument();
		PXShapeGroup group = new PXShapeGroup();
		Rect bounds = getBounds();
		if (colors != null && colors.length > 0 && !bounds.isEmpty()) {
			int height = bounds.height();
			int width = bounds.width();
			if (width >= height) {
				// horizontal layout
				float deltaX = (float) width / colors.length;
				float currentX = 0F;
				for (int i = 0; i < colors.length; i++, currentX += deltaX) {
					PXRectangle rectangle = new PXRectangle(new RectF(currentX + 1, 0, currentX + deltaX - 1, height));
					rectangle.setFillColor(new PXSolidPaint(colors[i]));
					group.addShape(rectangle);
				}
			} else {
				// vertical layout
				float deltaY = (float) width / colors.length;
				float currentY = 0F;
				for (int i = 0; i < colors.length; i++, currentY += deltaY) {
					PXRectangle rectangle = new PXRectangle(new RectF(0, currentY + 1, width, currentY + deltaY - 1));
					rectangle.setFillColor(new PXSolidPaint(colors[i]));
					group.addShape(rectangle);
				}
			}
		}
		swatchScene.setShape(group);
		this.scene = swatchScene;
		return this.scene;
	}
}
