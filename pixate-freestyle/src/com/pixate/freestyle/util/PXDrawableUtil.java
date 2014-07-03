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
package com.pixate.freestyle.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;

import com.pixate.freestyle.PixateFreestyle;
import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.shapes.PXRectangle;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.cache.PXStyleInfo;
import com.pixate.freestyle.styling.stylers.PXStylerContext;

/**
 * A utility class for {@link Drawable} related functionalities.
 * 
 * @author Shalom Gibly
 */
public class PXDrawableUtil {

    private static String TAG = PXDrawableUtil.class.getSimpleName();

    // Holds all the possible Android Drawable state names and values. Note that
    // the keys that are used in this map will omit the "state_" prefix that
    // android defines in the name of the attribute.
    private static final Map<String, Integer> STATES;
    static {
        STATES = new HashMap<String, Integer>();
        STATES.put("focused", android.R.attr.state_focused);
        STATES.put("window_focused", android.R.attr.state_window_focused);
        STATES.put("enabled", android.R.attr.state_enabled);
        STATES.put("checked", android.R.attr.state_checked);
        STATES.put("checkable", android.R.attr.state_checkable);
        STATES.put("selected", android.R.attr.state_selected);
        STATES.put("active", android.R.attr.state_active);
        STATES.put("single", android.R.attr.state_single);
        STATES.put("first", android.R.attr.state_first);
        STATES.put("middle", android.R.attr.state_middle);
        STATES.put("last", android.R.attr.state_last);
        STATES.put("pressed", android.R.attr.state_pressed);
        STATES.put("activated", android.R.attr.state_activated);
        STATES.put("above-anchor", android.R.attr.state_above_anchor);
        STATES.put("multiline", android.R.attr.state_multiline);
        // note that default is "drawable"
        STATES.put(PXStyleInfo.DEFAULT_STYLE, android.R.attr.drawable);

        if (PixateFreestyle.ICS_OR_BETTER) {
            addICSStates(STATES);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void addICSStates(Map<String, Integer> states) {
        states.put("hovered", android.R.attr.state_hovered);
        states.put("drag_can_accept", android.R.attr.state_drag_can_accept);
        states.put("drag_hovered", android.R.attr.state_drag_hovered);
    }

    /**
     * Returns a map of the {@link Drawable} supported states.
     * <ul>
     * <li>"state_focused"
     * <li>"state_window_focused"
     * <li>"state_enabled"
     * <li>"state_checked"
     * <li>"state_checkable"
     * <li>"state_selected"
     * <li>"state_active"
     * <li>"state_single"
     * <li>"state_first"
     * <li>"state_mid"
     * <li>"state_last"
     * <li>"state_pressed"
     * <li>"state_activated"
     * <li>"state_multiline"
     * <li>"state_above_anchor"
     * <li>"state_hovered" (ICE_CREAM_SANDWICH+)
     * <li>"state_drag_can_accept" (ICE_CREAM_SANDWICH+)
     * <li>"state_drag_hovered" (ICE_CREAM_SANDWICH+)
     * <li>"drawable" (default)
     * </ul>
     * 
     * @return A supported states map that
     */
    public static Map<String, Integer> getSupportedStates() {
        return new HashMap<String, Integer>(STATES);
    }

    /**
     * Returns the integer state value that is mapped to the given state name.
     * In case none can be mapped, the method returns {@link Integer#MIN_VALUE}.
     * 
     * @param stateName
     * @return The drawable state integer value; {@link Integer#MIN_VALUE} in
     *         case the given state name cannot be matched.
     */
    public static int getStateValue(String stateName) {
        if (stateName != null && STATES.containsKey(stateName)) {
            return STATES.get(stateName);
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Creates a {@link Drawable} by rendering the {@link PXPaint} into an a
     * drawable image.
     * 
     * @param width
     * @param height
     * @param paint
     * @return A new {@link Drawable} for the {@link PXPaint}.
     */
    public static Drawable createDrawable(float width, float height, PXPaint paint) {
        return createDrawable(new RectF(0, 0, width, height), paint);
    }

    /**
     * Creates a {@link Drawable} by rendering the {@link PXPaint} into an a
     * drawable image.
     * 
     * @param bounds
     * @param paint
     * @return A new {@link Drawable} for the {@link PXPaint}.
     */
    public static Drawable createDrawable(RectF bounds, PXPaint paint) {
        PXRectangle rectangle = new PXRectangle(bounds);
        rectangle.setFillColor(paint);
        return rectangle.renderToImage(bounds, true);
    }

    /**
     * Sets a background {@link Drawable} on a view. In case the call is set to
     * check for a layer-drawable and there is an existing {@link LayerDrawable}
     * on the given View, set/replace the layer with the
     * {@code android.R.id.background} id.
     * 
     * @param view
     * @param drawable
     * @param checkForLayer Indicate if this method should check for a
     *            {@link LayerDrawable} when applying a background.
     */
    public static void setBackgroundDrawable(View view, Drawable drawable, boolean checkForLayer) {
        Drawable background = view.getBackground();
        if (checkForLayer && background instanceof LayerDrawable) {
            LayerDrawable layeredBG = (LayerDrawable) background;
            layeredBG.setDrawableByLayerId(android.R.id.background, drawable);
            layeredBG.invalidateSelf();
        } else {
            setBackgroundDrawable(view, drawable);
        }
    }

    /**
     * Sets a background {@link Drawable} on a view.
     * 
     * @param view
     * @param drawable
     */
    @SuppressWarnings("deprecation")
    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundJB(view, drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void setBackgroundJB(View view, Drawable drawable) {
        view.setBackground(drawable);
    }

    /**
     * Sets a {@link Bitmap} background. In case the call is set to check for a
     * layer-drawable and there is an existing {@link LayerDrawable} on the
     * given View, set/replace the layer with the
     * {@code android.R.id.background} id.
     * 
     * @param view
     * @param bitmap
     * @param checkForLayer Indicate if this method should check for a
     *            {@link LayerDrawable} when applying a background.
     */
    public static void setBackground(View view, Bitmap bitmap, boolean checkForLayer) {
        BitmapDrawable newDrawable = new BitmapDrawable(PixateFreestyle.getAppContext()
                .getResources(), bitmap);
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            // keep the background color so it would show when the bitmap is
            // transparent
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[] { background,
                    newDrawable });
            layerDrawable.setId(1, android.R.id.background);
            view.setBackground(layerDrawable);
        } else {
            setBackgroundDrawable(view, newDrawable, checkForLayer);
        }
    }

    /**
     * Returns a {@link Map} that holds a mapping from the existing state-lists
     * in the background {@link Drawable}.
     * 
     * @param background A {@link Drawable} background (
     *            {@link StateListDrawable}).
     * @return A {@link Map}. An empty map in case the background
     *         <code>null</code>, or is not a {@link StateListDrawable}.
     */
    public static Map<int[], Drawable> getExistingStates(Drawable background) {
        LinkedHashMap<int[], Drawable> map = new LinkedHashMap<int[], Drawable>();
        if (background instanceof StateListDrawable) {
            // Grab the existing states. Note that the API hides some of the
            // public functionality with the @hide tag, so we have to access
            // those through reflection...
            StateListDrawable stateList = (StateListDrawable) background;
            DrawableContainerState containerState = (DrawableContainerState) stateList
                    .getConstantState();
            Drawable[] children = containerState.getChildren();
            try {
                // This method is public but hidden ("pending API council")
                Method method = stateList.getClass().getMethod("getStateSet", int.class);
                for (int i = 0; i < containerState.getChildCount(); i++) {
                    Object state = method.invoke(stateList, i);
                    if (state instanceof int[]) {
                        map.put((int[]) state, children[i]);
                    }
                }
            } catch (Exception e) {
                PXLog.e(TAG, e, "Error getting the state set");
            }
        }
        return map;
    }

    /**
     * Check if two Drawables are equal. A regular check for a Drawable equals
     * just checks for the instance reference, while this check is doing a
     * deeper equals when dealing with {@link DrawableContainer} instances. In
     * these cases, the method will run equals on each of the child drawables in
     * the container (order is importance as well).
     * 
     * @param d1
     * @param d2
     * @return <code>true</code> if the drawables are equal, <code>false</code>
     *         otherwise.
     */
    public static boolean isEquals(Drawable d1, Drawable d2) {
        if (d1 == d2) {
            return true;
        }
        if (d1 == null || d2 == null) {
            return false;
        }
        if (d1 instanceof DrawableContainer && d2 instanceof DrawableContainer) {
            // Try to match the content of those containers
            DrawableContainerState containerState1 = (DrawableContainerState) ((DrawableContainer) d1)
                    .getConstantState();
            DrawableContainerState containerState2 = (DrawableContainerState) ((DrawableContainer) d2)
                    .getConstantState();

            return Arrays.equals(containerState1.getChildren(), containerState2.getChildren());
        }
        return d1.equals(d2);
    }

    /**
     * A utility method that will create a new {@link StateListDrawable} from
     * the given contexts and the existing {@link View}'s background that is in
     * that context.
     * 
     * @param adapter A {@link PXStyleAdapter}. This will be used to create
     *            additional states (see
     *            {@link PXStyleAdapter#createAdditionalDrawableStates(int)}
     * @param existingStates The states that exist in the {@link View} that is
     *            being styled. Note that in case that the existing states are
     *            empty (or <code>null</code>), there may be a need to call the
     *            {@link #createNewStateListDrawable(PXStyleAdapter, List, List)}
     *            method instead.
     * @param ruleSets
     * @param contexts
     * @return A new {@link StateListDrawable}
     * @see #createNewStateListDrawable(PXStyleAdapter, List, List)
     */
    public static Drawable createDrawable(PXStyleAdapter adapter,
            Map<int[], Drawable> existingStates, List<PXRuleSet> ruleSets,
            List<PXStylerContext> contexts) {
        PXStylerContext context = contexts.get(0);

        Set<int[]> statesKeys;
        if (existingStates != null) {
            statesKeys = existingStates.keySet();
        } else {
            statesKeys = Collections.emptySet();
        }

        // Will hold the new background.
        StateListDrawable stateListDrawable = adapter.shouldAdjustDrawableBounds() ? (new StateListDrawableWithBoundsChange())
                : (new StateListDrawable());

        // For every image we have in the contexts, create a drawable and insert
        // it into the StateListDrawable. The assumption is that every context
        // will provide a drawable for a different state. Otherwise, we may end
        // up with multiple drawables for the same state and Android will pick
        // the first one it hits. We also want to keep the order of the original
        // state list, so we do that insertion in two parts. First, we collect
        // all the states indexes that will be 'replaced', and then we do the
        // actual construction of the new state-list while weaving in drawables
        // from the original list and our contexts.
        Map<Integer, Drawable> newStatesPositions = new LinkedHashMap<Integer, Drawable>();
        int rulesetSize = ruleSets.size();
        for (int i = 0; i < rulesetSize; i++) {
            context = contexts.get(i);
            Drawable drawable = (context.usesImage() || context.usesColorOnly()) ? context
                    .getBackgroundImage() : null;
            if (drawable != null && existingStates != null && !existingStates.isEmpty()) {
                String activeStateName = context.getActiveStateName();
                if (activeStateName == null) {
                    activeStateName = PXStyleInfo.DEFAULT_STYLE;
                }
                // Artificially add states to the one we got. For example, add a
                // 'pressed' state to a 'checked' state.
                int[][] activeStates = adapter.createAdditionalDrawableStates(PXDrawableUtil
                        .getStateValue(activeStateName));

                // Find the index we would like to insert this state. Remember
                // it and its newly assigned drawable.
                int index = 0;
                for (int[] state : statesKeys) {
                    for (int[] activeState : activeStates) {
                        if (Arrays.equals(state, activeState)) {
                            // we set the new position, intentionally not
                            // breaking out of the loop (last occurrence wins)
                            newStatesPositions.put(index, drawable);
                        }
                    }
                    index++;
                }
            }
        }
        // At this point we have the indexes in the original state list that we
        // would like to replace with our drawables, so we loop and create a the
        // new state list.
        int index = 0;
        for (int[] state : statesKeys) {
            Drawable drawable = newStatesPositions.get(index);
            if (drawable != null) {
                // NOTE: This is super important for Spinners and
                // CheckedTextViews! Update the state list drawable bounds size.
                if (adapter.shouldAdjustDrawableBounds()) {
                    stateListDrawable.getBounds().union(drawable.getBounds());
                }
                stateListDrawable.addState(state, drawable);
            } else {
                // add the existing one.
                stateListDrawable.addState(state, existingStates.get(state));
            }
            index++;
        }
        return stateListDrawable.mutate();
    }

    /**
     * Creates a new {@link Drawable}. The returned {@link Drawable} can be a
     * {@link StateListDrawable}, or in case we only have a single context, it
     * will be a {@link BitmapDrawable}.
     * 
     * @param adapter
     * @param ruleSets
     * @param contexts
     * @return A new {@link Drawable}
     * @see #createNewStateListDrawable(PXStyleAdapter, List, List)
     */
    public static Drawable createNewDrawable(PXStyleAdapter adapter, List<PXRuleSet> ruleSets,
            List<PXStylerContext> contexts) {
        Drawable drawable;
        if (contexts.size() == 1
                && PXStyleInfo.DEFAULT_STYLE.equals(contexts.get(0).getActiveStateName())) {
            drawable = contexts.get(0).getBackgroundImage();
        } else {
            drawable = createNewStateListDrawable(adapter, ruleSets, contexts);
        }
        return drawable;
    }

    /**
     * Creates a new {@link StateListDrawable} by looking into the contexts and
     * generating one according to their states.
     * 
     * @param adapter
     * @param ruleSets
     * @param contexts
     * @return A new {@link StateListDrawable}. <code>null</code> in case the
     *         contexts is <code>null</code> or empty.
     */
    public static StateListDrawable createNewStateListDrawable(PXStyleAdapter adapter,
            List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        if (contexts != null && !contexts.isEmpty()) {
            // No state-lists here, so simply loop and set the backgrounds

            StateListDrawable stateListDrawable = adapter.shouldAdjustDrawableBounds() ? (new StateListDrawableWithBoundsChange())
                    : (new StateListDrawable());

            int[][] deferredDefaultStates = null;
            Drawable defaultDrawable = null;
            int rulesetSize = ruleSets.size();
            for (int i = 0; i < rulesetSize; i++) {
                PXStylerContext context = contexts.get(i);
                String activeStateName = context.getActiveStateName();
                if (activeStateName == null) {
                    activeStateName = PXStyleInfo.DEFAULT_STYLE;
                }
                Drawable drawable = (context.usesImage() || context.usesColorOnly()) ? context
                        .getBackgroundImage() : null;
                // Artificially add states to the one we got. For example, add a
                // 'pressed' state to a 'checked' state.
                int stateValue = PXDrawableUtil.getStateValue(activeStateName);
                if (stateValue == android.R.attr.drawable) {
                    deferredDefaultStates = adapter.createAdditionalDrawableStates(stateValue);
                    defaultDrawable = drawable;
                } else {
                    int[][] activeStates = adapter.createAdditionalDrawableStates(stateValue);
                    for (int[] activeState : activeStates) {
                        stateListDrawable.addState(activeState, drawable);
                    }
                }
            }
            if (deferredDefaultStates != null && defaultDrawable != null) {
                // add the defaults at the end of the state list.
                for (int[] activeState : deferredDefaultStates) {
                    stateListDrawable.addState(activeState, defaultDrawable);
                }
            }
            return stateListDrawable;
        }
        return null;
    }

    private static class StateListDrawableWithBoundsChange extends StateListDrawable {
        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);
            onBoundsChange(getBounds());
        }
    }
}
