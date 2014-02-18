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

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.pixate.freestyle.cg.paints.PXLinearGradient;
import com.pixate.freestyle.cg.shapes.PXRectangle;
import com.pixate.freestyle.cg.shapes.PXShapeDocument;
import com.pixate.freestyle.pxcomponentkit.view.BasePXShapeDrawable;

/**
 * An overlay for creating a shade inside other widgets.<br>
 * To use this overlay, one should override the {@link View#dispatchDraw()} and
 * apply this drawable on the {@link Canvas}.<br>
 * 
 * <pre>
 * Usage example:
 * Create a top-to-bottom shadow effect from the color black to transparent:
 * 
 *    new PXShadeOverlay(Color.BLACK, Color.TRANSPARENT, ShadeDirection.DIRECTION_TOP_TO_BOTTOM, 120)
 * </pre>
 */
public class PXInnerShadeOverlay extends BasePXShapeDrawable {

	/**
	 * Shades direction.
	 */
	public enum ShadeDirection {
		DIRECTION_TOP_TO_BOTTOM, DIRECTION_LEFT_TO_RIGHT, DIRECTION_BOTTOM_TO_TOP, DIRECTION_RIGHT_TO_LEFT
	}

	private static final float DEFAULT_OPACITY = 0.75F;

	private int startColor;
	private int endColor;
	private ShadeDirection direction;
	private float size;
	private final float opacity;

	/**
	 * Constructs a shade overlay. A default opacity of 75% is applied.
	 * 
	 * @param startColor
	 * @param endColor
	 * @param direction
	 *            The gradient painting direction.
	 * @param size
	 *            The width/height of the shade (depends on the direction)
	 */
	public PXInnerShadeOverlay(int startColor, int endColor, ShadeDirection direction, float size) {
		this(startColor, endColor, direction, size, DEFAULT_OPACITY);
	}

	/**
	 * Constructs a shade overlay.
	 * 
	 * @param startColor
	 * @param endColor
	 * @param direction
	 *            The gradient painting direction.
	 * @param size
	 *            The width/height of the shade (depends on the direction)
	 * @param opacity
	 */
	public PXInnerShadeOverlay(int startColor, int endColor, ShadeDirection direction, float size, float opacity) {
		this.startColor = startColor;
		this.endColor = endColor;
		this.direction = direction;
		this.size = size;
		this.opacity = opacity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pixate.freestyle.pxengine.view.BasePXShapeDrawable#loadScene()
	 */
	@Override
	public PXShapeDocument loadScene() {
		if (scene != null) {
			return scene;
		}
		Rect bounds = getBounds();
		if (bounds.isEmpty()) {
			return null;
		}
		// Create a gradient
		PXLinearGradient linearGradient = PXLinearGradient.gradientFromStartColor(startColor, endColor);
		RectF rect = null;
		switch (direction) {
			case DIRECTION_TOP_TO_BOTTOM:
				linearGradient.setAngle(90);
				rect = new RectF(bounds.left, bounds.top, bounds.right, size);
				break;
			case DIRECTION_BOTTOM_TO_TOP:
				linearGradient.setAngle(270);
				rect = new RectF(bounds.left, bounds.bottom - size, bounds.right, bounds.bottom);
				break;
			case DIRECTION_LEFT_TO_RIGHT:
				linearGradient.setAngle(0);
				rect = new RectF(bounds.left, bounds.top, bounds.left + size, bounds.bottom);
				break;
			case DIRECTION_RIGHT_TO_LEFT:
				linearGradient.setAngle(180);
				rect = new RectF(bounds.right - size, bounds.top, bounds.right, bounds.bottom);
				break;
			default:
				throw new IllegalArgumentException("Unsupported direction " + direction);
		}
		PXRectangle rectangle = new PXRectangle(rect);
		rectangle.setFillColor(linearGradient);
		rectangle.setOpacity(opacity);
		PXShapeDocument aScene = new PXShapeDocument();
		aScene.setShape(rectangle);
		scene = aScene;
		return scene;
	}

}
