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
package com.pixate.freestyle.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.widget.CompoundButton;

import com.pixate.freestyle.R;

/**
 * The switch has two states similar to a checkbox or toggle button. But unlike
 * the two, you see the other state which is currently not selected. The switch
 * flips from the left to the right side, selecting either the right or the left
 * side.
 */
public class Switch extends CompoundButton {
    // left and right text
    private CharSequence textLeft;
    private CharSequence textRight;

    // the background
    private Drawable drawableBackground;

    // the switch
    private Drawable drawableSwitch;
    private Drawable drawableSwitchOff;

    // helper for left and right text
    private Layout layoutLeft;
    private Layout layoutRight;

    // min width of the whole switch
    private int switchMinWidth;

    // actual width and height of the switch
    private int width;
    private int height;

    // the padding left+right inside of the switch
    private int innerPadding;

    // the space between the text to the left of the switch and the switch
    private int switchPadding;

    // the colors for the text of the switch
    private int textColorChecked;
    private int textColorUnChecked;
    private OnClickListener onClickListener;

    /**
     * Construct a new Switch with default styling
     * 
     * @param context The Context that will determine this widget's theming
     */
    public Switch(Context context) {
        this(context, null);
    }

    /**
     * Construct a new Switch with default styling, overriding specific style
     * attributes as requested.
     * 
     * @param context The Context that will determine this widget's theming.
     * @param attrs Specification of attributes that should deviate from default
     *            styling.
     */
    public Switch(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.switchStyle);
    }

    /**
     * Construct a new Switch with a default style determined by the given theme
     * attribute, overriding specific style attributes as requested.
     * 
     * @param context The Context that will determine this widget's theming.
     * @param attrs Specification of attributes that should deviate from the
     *            default styling.
     * @param defStyle An attribute ID within the active theme containing a
     *            reference to the default style for this widget. e.g.
     *            android.R.attr.switchStyle.
     */

    public Switch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.getPaint().setAntiAlias(true);

        // load the default values
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Switch, defStyle, 0);
        this.textLeft = a.getText(R.styleable.Switch_textLeft);
        this.textRight = a.getText(R.styleable.Switch_textRight);
        this.switchMinWidth = a.getDimensionPixelSize(R.styleable.Switch_switchMinWidth, 50);
        this.textColorUnChecked = a.getColor(R.styleable.Switch_colorUnChecked, Color.WHITE);
        this.textColorChecked = a.getColor(R.styleable.Switch_colorChecked, Color.WHITE);
        this.drawableBackground = a.getDrawable(R.styleable.Switch_backgroundDrawable);
        this.drawableSwitch = a.getDrawable(R.styleable.Switch_switchDrawable);
        this.drawableSwitchOff = a.getDrawable(R.styleable.Switch_switchDrawableOff);
        this.switchPadding = a.getDimensionPixelSize(R.styleable.Switch_switchPadding, 32);
        this.innerPadding = a.getDimensionPixelSize(R.styleable.Switch_innerPadding, 20);
        this.setChecked(a.getBoolean(R.styleable.Switch_isChecked, false));
        a.recycle();

        // throw an error if the texts have not been set
        if (this.textLeft == null || this.textRight == null)
            throw new IllegalStateException(
                    "Either textLeft or textRight is null. Please them via the attributes with the same name in the layout");
    }

    /**
     * Sets the text displayed on the left side.
     * 
     * @param textLeft The left text. Not <code>null</code>
     */
    public void setTextLeft(CharSequence textLeft) {
        if (textLeft == null)
            throw new IllegalArgumentException("The text for the left side must not be null!");

        this.textLeft = textLeft;
        this.requestLayout();
    }

    /**
     * Returns the text on the left
     * 
     * @return the text on the left. <code>null</code> shouldn't be possible,
     *         due to the button throwing an error if the texts are
     *         <code>null</code>
     */
    public CharSequence getTextLeft() {
        return this.textLeft;
    }

    /**
     * Returns the text on the right.
     * 
     * @return the text on the right. <code>null</code> shouldn't be possible,
     *         due to the button throwing an error if the texts are
     *         <code>null</code>
     */
    public CharSequence getTextRight() {
        return this.textRight;
    }

    /**
     * Sets the text displayed on the right side
     * 
     * @param textRight The right text. Not <code>null</code>
     */
    public void setTextRight(CharSequence textRight) {

        if (textRight == null)
            throw new IllegalArgumentException("The text for the right side must not be null!");

        this.textRight = textRight;
        this.requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // create the helper layouts if necessary
        if (this.layoutLeft == null)
            this.layoutLeft = this.makeLayout(this.textLeft);

        if (this.layoutRight == null)
            this.layoutRight = this.makeLayout(this.textRight);

        // find the larger text so both halfs of the switch are equally wide
        final int maxTextWidth = Math.max(this.layoutLeft.getWidth(), this.layoutRight.getWidth());

        // calculate the width for the whole switch
        int actualWidth = Math.max(this.switchMinWidth, maxTextWidth * 2 + this.getPaddingLeft()
                + this.getPaddingRight() + this.innerPadding * 4);

        // calculate the height of the switch
        // TODO if you want to have a padding-top and padding-bottom, add here
        final int switchHeight = Math.max(this.drawableBackground.getIntrinsicHeight(),
                this.drawableSwitch.getIntrinsicHeight());

        this.width = actualWidth;
        this.height = switchHeight;

        // recalculate the width if there is a text
        if (this.getText() != null)
            actualWidth += this.makeLayout(this.getText()).getWidth() + this.switchPadding;

        // set the dimensions for this view
        setMeasuredDimension(actualWidth, switchHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // calculate the left and right values
        int right = this.getWidth() - this.getPaddingRight();
        int left = right - this.width;

        // draw background
        this.drawableBackground.setBounds(left, 0, right, this.height);
        this.drawableBackground.draw(canvas);

        // draw switch
        if (this.isChecked()) {
            this.drawableSwitch.setBounds(left + this.width / 2, 0, right, this.height);
            this.drawableSwitch.draw(canvas);
        } else {
            this.drawableSwitchOff.setBounds(left, 0, left + (this.width / 2), this.height);
            this.drawableSwitchOff.draw(canvas);
        }

        // save canvas before translation (0x0)
        canvas.save();

        // draw left text
        if (this.isChecked()) {
            this.getPaint().setColor(
                    this.isChecked() ? this.textColorChecked : this.textColorUnChecked);
            canvas.translate(left + (this.width / 2 - this.layoutRight.getWidth()) / 2 + this.width
                    / 2, (this.height - this.layoutRight.getHeight()) / 2);
            this.layoutLeft.draw(canvas);
            canvas.restore();
        }

        // draw right text
        if (!this.isChecked()) {
            this.getPaint().setColor(
                    !this.isChecked() ? this.textColorChecked : this.textColorUnChecked);
            canvas.translate(left + (this.width / 2 - this.layoutLeft.getWidth()) / 2,
                    (this.height - this.layoutLeft.getHeight()) / 2);
            this.layoutRight.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public int getCompoundPaddingRight() {
        int padding = super.getCompoundPaddingRight() + this.width;
        if (!TextUtils.isEmpty(getText()))
            padding += this.switchPadding;

        return padding;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);

        this.onClickListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                this.setChecked(!this.isChecked());
                invalidate();

                // call the onClickListener
                if (this.onClickListener != null)
                    this.onClickListener.onClick(this);
                return false;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Make a layout for the text
     * 
     * @param text The text. Neither <code>null</code> nor empty
     * @return The layout
     */
    private Layout makeLayout(CharSequence text) {
        return new StaticLayout(text, this.getPaint(), (int) Math.ceil(Layout.getDesiredWidth(
                text, this.getPaint())), Layout.Alignment.ALIGN_NORMAL, 1f, 0, true);
    }

    @Override
    protected void drawableStateChanged() {
        // TODO don't use the 9-patches directly use a *.xml drawable instead
        super.drawableStateChanged();

        int[] myDrawableState = getDrawableState();

        if (this.drawableSwitch != null) {
            this.drawableSwitch.setState(myDrawableState);
        }

        if (this.drawableSwitchOff != null) {
            this.drawableSwitchOff.setState(myDrawableState);
        }

        invalidate();
    }
}
