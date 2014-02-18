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
package com.pixate.freestyle.pxcomponentkit.view.overlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Rect;

import com.pixate.freestyle.cg.shapes.PXShapeDocument;
import com.pixate.freestyle.cg.shapes.PXShapeGroup;
import com.pixate.freestyle.pxcomponentkit.view.BasePXShapeDrawable;

/**
 * A composite for PX Shape Drawables that draws them one on top of the other.
 */
public class PXCompositeOverlay extends BasePXShapeDrawable {

	private List<BasePXShapeDrawable> overlays;

	/**
	 * Constructs a new composite overlay of drawables.
	 */
	public PXCompositeOverlay() {
		this((BasePXShapeDrawable[]) null);
	}

	/**
	 * Constructs a new composite overlay of drawables.
	 * 
	 * @param drawables
	 */
	public PXCompositeOverlay(BasePXShapeDrawable... overlays) {
		if (overlays == null) {
			this.overlays = new ArrayList<BasePXShapeDrawable>(3);
		} else {
			this.overlays = Arrays.asList(overlays);
		}
	}

	public void addOverlay(BasePXShapeDrawable overlay) {
		overlays.add(overlay);
	}

	public void removeOverlay(BasePXShapeDrawable overlay) {
		overlays.remove(overlay);
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		scene = null;
		for (BasePXShapeDrawable d : overlays) {
			d.setBounds(bounds);
		}
		super.onBoundsChange(bounds);
	}

	@Override
	protected boolean onLevelChange(int level) {
		scene = null;
		boolean result = false;
		for (BasePXShapeDrawable d : overlays) {
			result |= d.setLevel(level);
		}
		return super.onLevelChange(level) || result;
	}

	@Override
	protected boolean onStateChange(int[] state) {
		scene = null;
		boolean result = false;
		for (BasePXShapeDrawable d : overlays) {
			result |= d.setState(state);
		}
		return super.onStateChange(state) || result;
	}

	@Override
	public PXShapeDocument loadScene() {
		if (scene != null) {
			return scene;
		}
		PXShapeGroup group = new PXShapeGroup();
		for (BasePXShapeDrawable d : overlays) {
			PXShapeDocument loadedScene = d.loadScene();
			if (loadedScene != null) {
				// Add the content of this scene into the composite one.
				group.addShape(loadedScene.getShape());
			}
		}
		PXShapeDocument aScene = new PXShapeDocument();
		aScene.setShape(group);
		scene = aScene;
		return scene;
	}

}
