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
package com.pixate.freestyle.styling.adapters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.TextUtils.TruncateAt;
import android.widget.TextView;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.cg.shadow.PXShadow;
import com.pixate.freestyle.cg.shadow.PXShadowPaint;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.cache.PXStyleInfo;
import com.pixate.freestyle.styling.infos.PXLineBreakInfo.PXLineBreakMode;
import com.pixate.freestyle.styling.stylers.PXColorStyler;
import com.pixate.freestyle.styling.stylers.PXFontStyler;
import com.pixate.freestyle.styling.stylers.PXGenericStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXStylerBase;
import com.pixate.freestyle.styling.stylers.PXStylerBase.PXDeclarationHandler;
import com.pixate.freestyle.styling.stylers.PXStylerBase.PXStylerInvocation;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.styling.stylers.PXTextContentStyler;
import com.pixate.freestyle.styling.stylers.PXTextShadowStyler;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualBottomIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualLeftIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualRightIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualTopIcon;
import com.pixate.freestyle.util.PXColorUtil;
import com.pixate.freestyle.util.PXLog;

@PXDocElement(properties = {
        @PXDocProperty(name = "text-transform", syntax = "uppercase | lowercase"),
        @PXDocProperty(name = "text-overflow", syntax = "word-wrap | character-wrap | ellipsis-head | ellipsis-tail | ellipsis-middle"),
        @PXDocProperty(name = "compound-padding", syntax = "<length>") })
public class PXTextViewStyleAdapter extends PXViewStyleAdapter {

    private static String TAG = PXTextViewStyleAdapter.class.getSimpleName();
    private static String COLOR_PROPERTY = "color";
    private static String ELEMENT_NAME = "text-view";
    private static PXTextViewStyleAdapter sInstance;

    protected PXTextViewStyleAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#getElementName
     * (java.lang.Object)
     */
    @Override
    public String getElementName(Object object) {

        return ELEMENT_NAME;
    }

    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        List<Object> superVirtuals = super.getVirtualChildren(styleable);
        List<Object> result = new ArrayList<Object>(superVirtuals.size() + 4);
        result.addAll(superVirtuals);
        result.add(new PXVirtualTopIcon(styleable));
        result.add(new PXVirtualRightIcon(styleable));
        result.add(new PXVirtualBottomIcon(styleable));
        result.add(new PXVirtualLeftIcon(styleable));
        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#createStylers()
     */
    @Override
    protected List<PXStyler> createStylers() {
        List<PXStyler> stylers = super.createStylers();

        stylers.add(new PXTextShadowStyler(new PXStylerInvocation() {
            public void invoke(Object view, PXStyler styler, PXStylerContext context) {
                TextView textView = (TextView) view;
                PXShadowPaint shadowPaint = context.getTextShadow();
                // TODO Shadow group?
                if (shadowPaint instanceof PXShadow) {
                    PXShadow shadow = (PXShadow) shadowPaint;
                    textView.setShadowLayer(0.5f, shadow.getHorizontalOffset(),
                            shadow.getVerticalOffset(), shadow.getColor());
                }
            }
        }));

        stylers.add(new PXFontStyler(new PXStylerInvocation() {
            public void invoke(Object view, PXStyler styler, PXStylerContext context) {
                TextView textView = (TextView) view;
                Typeface typeface = context.getFont();
                if (typeface != null) {
                    textView.setTypeface(typeface);
                }
                textView.setTextSize(context.getFontSize());
            }
        }));

        stylers.add(PXColorStyler.getInstance());

        // TODO Insets? Check relevance to Android.

        stylers.add(new PXTextContentStyler(new PXStylerInvocation() {
            public void invoke(Object view, PXStyler styler, PXStylerContext context) {
                TextView textView = (TextView) view;
                textView.setText(context.getText());
            }
        }));

        Map<String, PXDeclarationHandler> handlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>();
        handlers.put("text-transform", new PXDeclarationHandler() {
            public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                TextView textView = (TextView) stylerContext.getStyleable();
                String newTitle = declaration.transformString((String) textView.getText()
                        .toString());
                textView.setText(newTitle);
            }
        });

        handlers.put("text-overflow", new PXDeclarationHandler() {
            public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                TextView textView = (TextView) stylerContext.getStyleable();
                PXLineBreakMode lineBreakMode = declaration.getLineBreakModeValue();
                TruncateAt androidValue = lineBreakMode.getAndroidValue();
                boolean changed = false;

                if (androidValue != null) {
                    textView.setEllipsize(androidValue);
                    changed = true;
                } else {
                    switch (lineBreakMode) {
                        case CLIP:
                            textView.setSingleLine();
                            changed = true;
                            break;
                        default:
                            // TODO can other values be set to
                            // transformation methods?
                            break;
                    }
                }

                if (!changed) {
                    // Set to defaults
                    textView.setSingleLine(false);
                }
            }
        });

        handlers.put("compound-padding", new PXDeclarationHandler() {
            public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                TextView styleable = (TextView) stylerContext.getStyleable();
                int padding = (int) declaration.getFloatValue(stylerContext.getDisplayMetrics());
                if (styleable.getCompoundDrawablePadding() != padding) {
                    styleable.setCompoundDrawablePadding(padding);
                }
            }
        });

        stylers.add(new PXGenericStyler(handlers));
        return stylers;
    }

    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        if (!super.updateStyle(ruleSets, contexts)) {
            return false;
        }

        PXStylerContext context = null;

        // It needs to be the same View instance for all, so just grab the
        // first.
        TextView textView = (TextView) contexts.get(0).getStyleable();

        // We will maintain any existing text states, while injecting the CSS
        // defined states into the right places.
        Map<int[], Integer> existingColorStates = getExistingColorStates(textView);
        Map<int[], Integer> newStates = new LinkedHashMap<int[], Integer>();

        Integer defaultColor = null;
        int[] savedActivatedState = null;
        for (int i = 0; i < ruleSets.size(); i++) {
            context = contexts.get(i);
            Object colorPropertyValue = context.getPropertyValue(COLOR_PROPERTY);
            if (colorPropertyValue instanceof Number) {
                String activeStateName = context.getActiveStateName();
                if (activeStateName == null) {
                    activeStateName = PXStyleInfo.DEFAULT_STYLE;
                }
                int activeState = PXColorUtil.getStateValue(activeStateName);
                int color = ((Number) colorPropertyValue).intValue();
                if (activeState == android.R.attr.color) {
                    // remember the default color (will be inserted at the end)
                    defaultColor = color;

                    if (savedActivatedState == null) {
                        // set the same default color as 'activated' as well. We
                        // hold the activated state array in case we stumble
                        // into an explicit 'activated' color. In that case, we
                        // will call to replace it.
                        savedActivatedState = new int[] { android.R.attr.state_activated };
                        newStates.put(savedActivatedState, color);
                    }
                } else {
                    if (activeState == android.R.attr.state_activated
                            && savedActivatedState != null) {
                        // remove the existing state
                        newStates.remove(savedActivatedState);
                        savedActivatedState = new int[] { android.R.attr.state_activated };
                        newStates.put(savedActivatedState, color);
                    } else {
                        // just set the state
                        newStates.put(new int[] { activeState }, color);
                    }
                }
            }
        }

        int[][] states = new int[newStates.size() + existingColorStates.size()][];
        int[] colors = new int[states.length];
        int index = 0;
        for (int[] state : newStates.keySet()) {
            states[index] = state;
            colors[index] = newStates.get(state);
            index++;
        }
        for (int[] state : existingColorStates.keySet()) {
            states[index] = state;
            colors[index] = existingColorStates.get(state);
            index++;
        }
        // check the last value in the array of states.
        if (defaultColor != null) {
            int[] lastState = states[states.length - 1];
            if (lastState.length == 0 || lastState.length == 1
                    && lastState[0] == android.R.attr.color) {
                colors[colors.length - 1] = defaultColor;
            }
        }
        textView.setTextColor(new ColorStateList(states, colors));
        return true;
    }

    // Statics

    public static PXTextViewStyleAdapter getInstance() {
        synchronized (PXTextViewStyleAdapter.class) {

            if (sInstance == null) {
                sInstance = new PXTextViewStyleAdapter();
            }
        }

        return sInstance;
    }

    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // Looks like that by default there is no background set on the
        // TextView, so we assume here that the acceptable states would be
        // similar to a Button.
        // @formatter:off
        // { -android.R.attr.state_window_focused, android.R.attr.state_enabled}
        // { -android.R.attr.state_window_focused, -android.R.attr.state_enabled}
        // { android.R.attr.state_pressed}
        // { android.R.attr.state_focused, android.R.attr.state_enabled}
        // { android.R.attr.state_enabled}
        // { android.R.attr.state_focused}
        // { } (default - android.R.attr.drawable)
        // @formatter:on
        List<int[]> states = new ArrayList<int[]>(4);
        // check for some special cases.
        // @formatter:off
        switch (initialValue) {
            case android.R.attr.state_enabled:
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                break;
            case android.R.attr.state_focused:
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                break;
            case android.R.attr.drawable:
                // add anything that will be treated as the default. Note that
                // in case an additional pseudo ruleset appears to deal with
                // specific cases, it will take over.
                states.add(new int[] { -android.R.attr.state_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused });
                states.add(new int[] {});
            default:
                break;
        }
        // @formatter:on
        states.add(new int[] { initialValue });
        return states.toArray(new int[states.size()][]);
    }

    // privates

    private Map<int[], Integer> getExistingColorStates(TextView view) {
        Map<int[], Integer> map = new LinkedHashMap<int[], Integer>();
        ColorStateList textColors = view.getTextColors();
        if (textColors != null) {
            try {
                Field specsField = textColors.getClass().getDeclaredField("mStateSpecs");
                specsField.setAccessible(true);
                int[][] stateSpecs = (int[][]) specsField.get(textColors);

                Field colorsField = textColors.getClass().getDeclaredField("mColors");
                colorsField.setAccessible(true);
                int[] colors = (int[]) colorsField.get(textColors);

                // These all should match
                if (stateSpecs != null && colors != null && stateSpecs.length == colors.length) {
                    // load the map with the existing states
                    for (int i = 0; i < stateSpecs.length; i++) {
                        map.put(stateSpecs[i], colors[i]);
                    }
                }
            } catch (Exception e) {
                PXLog.e(TAG, e, "Error getting the state set");
            } finally {
            }
        }
        return map;
    }
}
