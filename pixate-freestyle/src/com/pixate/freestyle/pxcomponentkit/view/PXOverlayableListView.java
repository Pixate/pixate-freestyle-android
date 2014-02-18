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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * A {@link ListView} that can be overlaied with a {@link Drawable}.
 */
public class PXOverlayableListView extends ListView {

	private Drawable overlay;

	/**
	 * @param context
	 */
	public PXOverlayableListView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PXOverlayableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PXOverlayableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Sets an overlay for the list.
	 * 
	 * @param overlay
	 */
	public void setOverlay(Drawable overlay) {

		if (this.overlay != null && overlay != null) {
			// Transfer the bounds to the new overlay
			Rect bounds = this.overlay.getBounds();
			overlay.setBounds(bounds);
		}
		this.overlay = overlay;
		invalidate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (overlay != null) {
			overlay.setBounds(0, 0, w, h);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#dispatchDraw(android.graphics.Canvas)
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (overlay != null) {
			overlay.draw(canvas);
		}
	}
}
