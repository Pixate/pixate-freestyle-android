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
package com.pixate.freestyle.pxcomponentkit.view.overlay;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import com.pixate.freestyle.cg.paints.PXSolidPaint;
import com.pixate.freestyle.cg.shapes.PXRectangle;
import com.pixate.freestyle.cg.shapes.PXShapeDocument;
import com.pixate.freestyle.cg.strokes.PXStroke;
import com.pixate.freestyle.pxcomponentkit.view.BasePXShapeDrawable;

/**
 * A border overlay that draws a border on the target canvas.
 */
public class PXBorderOverlay extends BasePXShapeDrawable {

	private int color;
	private float size;

	/**
	 * Constructs a default border overlay. A default border is black, and with
	 * a size of 1.
	 */
	public PXBorderOverlay() {
		this(Color.BLACK, 1);
	}

	/**
	 * Constructs a border overlay.
	 * 
	 * @param color
	 * @param size
	 */
	public PXBorderOverlay(int color, float size) {
		this.color = color;
		this.size = size;
	}

	/**
	 * Set a color for the border.
	 * 
	 * @param color
	 */
	public void setColor(int color) {
		if (this.color != color) {
			this.color = color;
			scene = null;
		}
	}

	/**
	 * Set the border's size.
	 * 
	 * @param size
	 */
	public void setSize(float size) {
		if (this.size != size) {
			this.size = size;
			scene = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pixate.freestyle.pxengine.view.BasePXShapeDrawable#loadScene()
	 */
	@Override
	public PXShapeDocument loadScene() {
		Rect bounds = getBounds();
		if (bounds.isEmpty()) {
			scene = null;
			return scene;
		}
		if (scene != null) {
			return scene;
		}
		PXRectangle rectangle = new PXRectangle(new RectF(bounds));
		PXStroke stroke = new PXStroke();
		stroke.setColor(new PXSolidPaint(color));
		stroke.setWidth(size);
		rectangle.setStroke(stroke);

		PXShapeDocument aScene = new PXShapeDocument();
		aScene.setShape(rectangle);
		scene = aScene;
		return scene;
	}
}
