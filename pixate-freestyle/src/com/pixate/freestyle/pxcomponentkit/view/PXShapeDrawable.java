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

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.pixate.freestyle.PXConstants;
import com.pixate.freestyle.cg.parsing.PXSVGLoader;
import com.pixate.freestyle.cg.shapes.PXShapeDocument;
import com.pixate.freestyle.util.PXLog;

/**
 * PX shape drawable. This drawable can be initialized with an
 * {@link AssetManager} and an asset name. The asset will be loaded and a
 * PXScene will be created and rendered on a {@link Drawable#draw(Canvas)} call.
 */
public class PXShapeDrawable extends BasePXShapeDrawable {

	private WeakReference<AssetManager> assetManagerReference;
	private String asset;

	/**
	 * Constructs a new {@link PXShapeDrawable}.
	 * 
	 * @param assetManager
	 *            An {@link AssetManager} reference
	 */
	public PXShapeDrawable(AssetManager assetManager) {
		this(assetManager, null);
	}

	/**
	 * Constructs a new {@link PXShapeDrawable} with an {@link AssetManager} and
	 * an asset path.
	 * 
	 * @param assetManager
	 *            An {@link AssetManager} reference
	 * @param asset
	 *            an asset path
	 */
	public PXShapeDrawable(AssetManager assetManager, String asset) {
		this.assetManagerReference = new WeakReference<AssetManager>(assetManager);
		this.asset = asset;
	}

	/**
	 * Sets the asset file descriptor.
	 * 
	 * @param asset
	 *            The new asset path.
	 */
	public void setAsset(String asset) {
		if (asset == null) {
			this.scene = null;
			this.asset = null;

		} else if (!asset.equals(this.asset)) {
			this.scene = null;
			this.asset = asset;
		}
	}

	/**
	 * Parse and return a {@link PXShapeDocument}. If we already parsed it once, just
	 * return the previously parsed shape.
	 * 
	 * @return {@link PXShapeDocument}, or <code>null</code> if there was a problem.
	 */
	public PXShapeDocument loadScene() {
		AssetManager manager = assetManagerReference.get();
		if (manager == null) {
			PXLog.e(PXConstants.TAG, "AssetManager was null");
			return scene;
		}
		if (scene == null && asset != null) {
			InputStream inputStream = null;
			try {
				inputStream = manager.open(asset);
				scene = PXSVGLoader.loadFromStream(inputStream);
				if (scene != null) {
					scene.setViewport(new RectF(getBounds()));
				}
			} catch (IOException e) {
				PXLog.e(PXConstants.TAG, "Error loading an asset", e);
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException ioe) {
				}
			}
		}
		return scene;
	}
}
