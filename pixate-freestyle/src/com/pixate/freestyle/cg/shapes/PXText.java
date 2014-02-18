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
 * Copyright (c) 2012-2013 Pixate, Inc. All rights reserved.
 */
package com.pixate.freestyle.cg.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.pixate.freestyle.util.ObjectPool;
import com.pixate.freestyle.util.StringUtil;

/**
 * A PX Text representation.
 */
public class PXText extends PXShape {

    private String text;
    private Typeface typeface;
    private float textSize;
    private PointF origin;
    private Align align;

    /**
     * Constructs a new empty <code>PXText</code>.
     * 
     * @see #setText(String)
     */
    public PXText() {
        this(StringUtil.EMPTY);
    }

    /**
     * Constructs a new <code>PXText</code> with a text string.
     * 
     * @param text
     */
    public PXText(String text) {
        this.text = text;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        if (this.text == null || !this.text.equals(text)) {
            this.text = text;
            clearPath();
        }
    }

    /**
     * @return the typeface
     */
    public Typeface getTypeface() {
        return typeface;
    }

    /**
     * @param typeface the typeface to set
     */
    public void setTypeface(Typeface typeface) {
        if (this.typeface == null || !this.typeface.equals(typeface)) {
            this.typeface = typeface;
            clearPath();
        }
    }

    /**
     * @return the fontSize
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * @param fontSize the fontSize to set
     */
    public void setTextSize(float size) {
        if (this.textSize != size) {
            this.textSize = size;
            clearPath();
        }
    }

    /**
     * @return the origin
     */
    public PointF getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(PointF origin) {
        if (this.origin == null || !this.origin.equals(origin)) {
            this.origin = origin;
            clearPath();
        }
    }

    /**
     * @return the align
     */
    public Align getTextAlign() {
        return align;
    }

    /**
     * @param align the align to set
     */
    public void setTextAlign(Align align) {
        if (this.align == null || !this.align.equals(align)) {
            this.align = align;
            clearPath();
        }
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();
        if (align != null && align != Align.LEFT) {
            Rect clipBounds = canvas.getClipBounds();
            if (align == Align.CENTER) {
                canvas.translate(origin.x + clipBounds.exactCenterX(), origin.y + textSize);
            } else {
                canvas.translate(origin.x + clipBounds.width(), origin.y + textSize);
            }
        } else {
            canvas.translate(origin.x, origin.y + textSize);
        }
        super.render(canvas);
        canvas.restore();
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.cg.PXShape#newPath()
     */
    @Override
    protected Path newPath() {
        Path path = ObjectPool.pathPool.checkOut();
        Paint paint = ObjectPool.paintPool.checkOut();
        paint.setAntiAlias(false);
        if (getTextAlign() != null) {
            paint.setTextAlign(getTextAlign());
        }
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);
        paint.getTextPath(text, 0, text.length(), origin.x, origin.y, path);
        // Check the paint back into the pool
        ObjectPool.paintPool.checkIn(paint);
        return path;
    }

}
