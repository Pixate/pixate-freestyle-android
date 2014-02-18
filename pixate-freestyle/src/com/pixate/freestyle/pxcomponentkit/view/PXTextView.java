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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.paints.PXSolidPaint;
import com.pixate.freestyle.cg.shapes.PXText;
import com.pixate.freestyle.cg.strokes.PXStroke;

/**
 * A Pixate text view.
 */
public class PXTextView extends TextView {

	private PXText pxText;
	private Rect clipBounds = new Rect();
	private PointF origin = new PointF();

	/**
	 * @param context
	 */
	public PXTextView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PXTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PXTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		PXText pxText = getPXText();
		pxText.setText(text.toString());
		super.setText(text, type);
	}

	@Override
	public void setTextSize(float size) {
		PXText pxText = getPXText();
		pxText.setTextSize(size);
		super.setTextSize(size);
	}

	@Override
	public void setGravity(int gravity) {
		super.setGravity(gravity);
		PXText pxText = getPXText();

		if ((gravity & Gravity.CENTER) == Gravity.CENTER) {
			pxText.setTextAlign(Paint.Align.CENTER);
		} else if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
			pxText.setTextAlign(Paint.Align.LEFT);
		} else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
			pxText.setTextAlign(Paint.Align.RIGHT);
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (isInEditMode()) {
			super.onDraw(canvas);
			return;
		}
		PXText pxText = getPXText();
		pxText.setTextSize(getTextSize());
		pxText.setTypeface(getTypeface());
		canvas.getClipBounds(clipBounds);
		origin.x = clipBounds.left;
		origin.y = clipBounds.top;
		pxText.setOrigin(origin);
		pxText.render(canvas);
	}

	/**
	 * Set the text's stroke color.
	 * 
	 * @param stroke
	 */
	public void setStroke(PXStroke stroke) {
		PXText pxText = getPXText();
		pxText.setStroke(stroke);
		invalidate();
	}

	/**
	 * Set the text's fill color.
	 * 
	 * @param fill
	 */
	public void setFill(PXPaint fill) {
		PXText pxText = getPXText();
		pxText.setFillColor(fill);
		invalidate();
	}

	private PXText getPXText() {
		if (pxText == null) {
			pxText = new PXText();
			pxText.setFillColor(PXSolidPaint.createPaintWithColor(Color.BLACK));
		}
		return pxText;
	}
}
