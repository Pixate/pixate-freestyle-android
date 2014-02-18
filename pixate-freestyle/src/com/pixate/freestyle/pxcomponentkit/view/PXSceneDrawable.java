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

import android.graphics.drawable.Drawable;

import com.pixate.freestyle.cg.shapes.PXShapeDocument;

/**
 * A {@link PXSceneDrawable} wraps a {@link PXShapeDocument} and renders it on a
 * {@link Drawable#draw(android.graphics.Canvas)} calls.
 */
public class PXSceneDrawable extends BasePXShapeDrawable {

	/**
	 * Constructs a new {@link PXSceneDrawable} with a given {@link PXShapeDocument}.
	 * 
	 * @param scene
	 *            A {@link PXShapeDocument} to draw.
	 */
	public PXSceneDrawable(PXShapeDocument scene) {
		this(scene, 0, 0);
	}

	/**
	 * Constructs a new {@link PXSceneDrawable} with a given {@link PXShapeDocument}.
	 * 
	 * @param scene
	 *            A {@link PXShapeDocument} to draw.
	 * @param minHeight
	 * @param minWeight
	 */
	public PXSceneDrawable(PXShapeDocument scene, int minHeight, int minWeight) {
		super(minHeight, minWeight);
		this.scene = scene;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pixate.freestyle.pxengine.view.BasePXShapeDrawable#getScene()
	 */
	@Override
	public PXShapeDocument loadScene() {
		return scene;
	}
}
