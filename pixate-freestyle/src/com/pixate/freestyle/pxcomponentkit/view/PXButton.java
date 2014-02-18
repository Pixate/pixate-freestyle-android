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

import android.R.color;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

import com.pixate.freestyle.cg.paints.PXPaint;

/**
 * A Pixate button.
 */
public class PXButton extends Button {

	private PXButtonDrawable drawable;

	/**
	 * Constructs a new {@link PXButton}.
	 * 
	 * @param context
	 */
	public PXButton(Context context) {
		this(context, null);
	}

	/**
	 * Constructs a new {@link PXButton}.
	 * 
	 * @param context
	 * @param attrs
	 */
	public PXButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundColor(color.transparent);
		setBackgroundDrawable(new PXButtonDrawable());
	}

    @SuppressWarnings("deprecation")
    @Override
    public void setBackgroundDrawable(Drawable d) {
        if (d instanceof PXButtonDrawable) {
            drawable = (PXButtonDrawable) d;
        } else {
            drawable = null;
        }
        super.setBackgroundDrawable(d);
    }

	/**
	 * Set the button's corner radius. This method delegates the call to the
	 * {@link PXButtonDrawable} set on this button. In case a different drawable
	 * is set, nothing happens.
	 * 
	 * @param radius
	 */
	public void setCornerRadius(float radius) {
		if (drawable != null) {
			drawable.setCornerRadius(radius);
		}
	}

	/**
	 * Set the button's background {@link PXPaint}. This method delegates the
	 * call to the {@link PXButtonDrawable} set on this button. In case a
	 * different drawable is set, nothing happens.
	 * 
	 * @param paint
	 * @param state
	 */
	public void setBackgroundPaint(PXPaint paint, int state) {
		if (drawable != null) {
			drawable.setBackgroundPaint(paint, state);
			invalidate();
		}
	}

	/**
	 * Set the button's foreground {@link PXPaint}. This method delegates the
	 * call to the {@link PXButtonDrawable} set on this button. In case a
	 * different drawable is set, nothing happens.
	 * 
	 * @param paint
	 * @param state
	 */
	public void setForegroundPaint(PXPaint paint, int state) {
		if (drawable != null) {
			drawable.setForegroundPaint(paint, state);
			invalidate();
		}
	}
}
