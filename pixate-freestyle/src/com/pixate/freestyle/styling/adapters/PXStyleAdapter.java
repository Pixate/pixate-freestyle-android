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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import android.app.ActionBar;
import android.graphics.RectF;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.media.PXMediaGroup;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.styling.stylers.PXStylerContext.IconPosition;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualActionBarIconAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualActionBarLogoAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualActionBarOverflowAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualActionBarSplitAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualActionBarStackedAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualActionBarTabIconAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualCheckboxIconAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualCheckedTextViewIconAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualCompoundButtonIconAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualDropDownAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualIconAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualImageViewImageAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualListSelectorAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualOverscrollListAdapter;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualToggleAdapter;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarLogo;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarOverflowImage;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarSplit;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarStacked;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarTabIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualBottomIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualCheckboxIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualCheckedTextViewIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualCompoundButtonIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualDropdown;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualImageViewImage;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualLeftIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualListOverscroll;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualListSelector;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualRightIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualStyleable;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualToggle;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualTopIcon;
import com.pixate.freestyle.util.CollectionUtil;
import com.pixate.freestyle.util.PXLog;

public abstract class PXStyleAdapter {

    // A class-name to style-adapter mapping.
    private static Map<String, PXStyleAdapter> sRegistry;
    private static final PXStyleAdapter NULL_ADAPTER = new PXStyleAdapter() {
        @Override
        protected List<PXStyler> createStylers() {
            // return empty stylers list
            return Collections.emptyList();
        }
    };

    private static final String TAG = PXStyleAdapter.class.getSimpleName();
    private HashMap<String, PXStyler> stylersByProperty;
    private List<PXStyler> stylers;

    public PXStyleAdapter() {
    }

    // Public instance methods

    /**
     * Batch update with multiple styles. The given lists have to match in size.
     * 
     * @param ruleSets
     * @param contexts
     * @return true if styling may continue, false if no further styling should
     *         be done
     */
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        // Default behavior is to check validity of the arguments
        // and exit early if invalid, or call the single-ruleset, single-context
        // version of updateStyle if only one ruleSet and context are in the
        // lists.
        if (!validateStyleUpdateParameters(ruleSets, contexts)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the element name. This name will be matched against the rule-set
     * selector string.
     * 
     * @param object
     * @return The element name that will be matched against the rule-set
     *         selector.
     * @see PXMediaGroup#getRuleSets(Object) for the matching process.
     */
    public String getElementName(Object object) {
        // Null by default, subclasses can override;
        return null;
    }

    public String getStyleId(Object object) {
        // Null by default, subclasses can override;
        return null;
    }

    public String getStyleClass(Object object) {
        // Null by default, subclasses can override;
        return null;
    }

    public String getStyleKey(Object object) {
        // Default implementation. Subclasses may override to extend.
        return PXStyleUtils.getSelector(object);
    }

    /**
     * Returns the stylers.
     * 
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#getStylers()
     * @see #createStylers()
     */
    public synchronized List<PXStyler> getStylers() {
        if (stylers == null) {
            stylers = createStylers();
        }

        return stylers;
    }

    /**
     * Creates the stylers for this adapter. Subclasses may hook into this
     * method in order to add their own stylers when the stylers are created
     * (happens ones per adapter).
     * 
     * @return The stylers
     * @see #getStylers()
     */
    protected abstract List<PXStyler> createStylers();

    public RectF getBounds(Object styleable) {
        // Empty rect by default, subclasses can override.
        return new RectF();
    }

    public Object getParent(Object styleable) {
        // Null by default, subclasses can override.
        return null;
    }

    public String getElementNamespace(Object styleable) {
        // Null by default, subclasses can override.
        return null;
    }

    public String getAttributeValue(Object styleable, String attributeName) {
        // Null by default, subclasses can override.
        return null;
    }

    public String getAttributeValue(Object styleable, String attributeName, String namespaceUri) {
        // Default behavior is to return the value returned by the
        // non-namespaced
        // method. But subclasses can override.
        return getAttributeValue(styleable, attributeName);
    }

    /**
     * Returns a list of direct child elements of the given parent element. The
     * list may contain virtual styleables that will be handled when child
     * combinations are detected in the CSS (for example #myId > attr).
     * 
     * @param styleable
     * @return A list of child elements. Note that the children order is
     *         maintained in the result.
     */
    public List<Object> getElementChildren(Object styleable) {
        // Just return the virtual children in this default implementation.
        // Subclasses should override to add their concrete children.
        return getVirtualChildren(styleable);
    }

    /**
     * Returns a list of virtual children that should be appended to the
     * children of an element that is computed by this adapter. Note that these
     * virtual children do not effect the child count.
     * 
     * @param styleable
     * @return A list of virtual styleables.
     * @see PXVirtualStyleable
     */
    protected List<Object> getVirtualChildren(Object styleable) {
        return Collections.emptyList();
    }

    /**
     * A convenient way to get the previous sibling of the given element.
     * 
     * @param element
     * @return The previous sibling; <code>null</code> if none can be located.
     */
    public Object getPreviousSibling(Object styleable) {
        return getSiblingAt(styleable, -1);
    }

    /**
     * A convenient way to get the next sibling of the given element.
     * 
     * @param element
     * @return The next sibling; <code>null</code> if none can be located.
     */
    public Object getNextSibling(Object styleable) {
        return getSiblingAt(styleable, 1);
    }

    /**
     * Returns a sibling in a given offset of the given element.
     * 
     * @param element
     * @param index
     * @return
     */
    public Object getSiblingAt(Object styleable, int offset) {
        // null by default, subclasses can override.
        return null;
    }

    /**
     * Returns the index of the given element that represents its location in
     * the children list of its parent.
     * 
     * @param element
     * @return The index; -1 in case no index can be resolved.
     */
    public int getIndexInParent(Object styleable) {
        // -1 by default, subclasses can override.
        return -1;
    }

    /**
     * Return a list of pseudo-classes that are recognized by given styleable
     * instance.
     * 
     * @param styleable
     */
    public List<String> getSupportedPseudoClasses(Object styleable) {
        // Implemented by subclasses
        return null;
    }

    /**
     * Return a list of pseudo-elements that are recognized by this object
     * 
     * @param styleable
     */
    public List<String> getSupportedPseudoElements(Object styleable) {
        // FIXME - Not supported at the moment. We need to investigate how to
        // (and if) we need to implement this for Android.
        return null;
    }

    /**
     * Returns the default pseudo class. Null, if none exists for the given
     * styleable.
     * 
     * @param styleable
     * @return A pseudo class (may be <code>null</code>)
     */
    public String getDefaultPseudoClass(Object styleable) {
        // Implemented by subclasses
        return null;
    }

    public int getChildCount(Object styleable) {
        // default 0, subclasses can override.
        return 0;
    }

    public int getSiblingsCount(Object element) {
        // default 0, subclasses can override.
        return 0;
    }

    protected boolean validateStyleUpdateParameters(List<PXRuleSet> ruleSets,
            List<PXStylerContext> contexts) {
        if (ruleSets == null || contexts == null) {
            if (PXLog.isLogging()) {
                PXLog.e(TAG, "Rule sets or contexts were null");
            }
            return false;
        }
        if (ruleSets.size() != contexts.size()) {
            if (PXLog.isLogging()) {
                PXLog.e(TAG, "Rule sets or contexts size did not match");
            }
            return false;
        }

        return true;
    }

    /**
     * Generates a number of states from a given single state by artificially.
     * This is the default implementation, and it doesn't add any state.
     * Subclasses should override this implementation to add the unique states
     * they require to function properly.
     * 
     * @param stateValue
     * @return An array of states array that will be used to replace values in
     *         the styleable's existing instance.
     */
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // just wrap in an int array
        return new int[][] { new int[] { initialValue } };
    }

    /**
     * Indicates whether the StateListDrawables that are being created with this
     * adapter should adjust their bounds by unionizing their internal drawables
     * bounds. By default, this method returns <code>false</code> and subclasses
     * should overwrite as necessary.
     * 
     * @return <code>true</code> in case the StateListDrawable should hold a
     *         union of the bounds.
     */
    public boolean shouldAdjustDrawableBounds() {
        return false;
    }

    // Statics

    public static void registerStyleAdapter(String className, PXStyleAdapter styleAdapter) {
        synchronized (PXStyleAdapter.class) {

            if (sRegistry == null) {
                sRegistry = new HashMap<String, PXStyleAdapter>();
            }

            sRegistry.put(className, styleAdapter);
        }
    }

    public static PXStyleAdapter getStyleAdapter(Object objectToStyle) {
        synchronized (PXStyleAdapter.class) {
            if (sRegistry == null) {
                initDefaultStyleAdapters();
            }
        }

        if (objectToStyle == null) {
            return NULL_ADAPTER;
        }

        Class<?> cls = objectToStyle instanceof Class<?> ? (Class<?>) objectToStyle : objectToStyle
                .getClass();

        PXStyleAdapter result = sRegistry.get(cls.getName());

        if (result == null) {
            Class<?> ancestorClass = cls.getSuperclass();
            while (ancestorClass != null) {
                result = sRegistry.get(ancestorClass.getName());
                if (result != null) {
                    // Now that we have it, register it.
                    registerStyleAdapter(cls.getName(), result);
                    break;
                } else {
                    ancestorClass = ancestorClass.getSuperclass();
                }
            }
        }

        if (result == null) {
            // Fallback no-op
            result = NULL_ADAPTER;
        }

        return result;
    }

    public static boolean isStyleable(Object object) {
        return getStyleAdapter(object) != NULL_ADAPTER;
    }

    /**
     * Returns true by default to indicate that this adapter supports
     * {@link PXStyler}s.
     * 
     * @return <code>true</code> if this adapter supports stylers (default);
     *         <code>false</code> otherwise.
     */
    public boolean isSupportingStylers() {
        return true;
    }

    /**
     * Returns a map of property names to stylers that match this adapter. The
     * map will be built from the {@link PXStyler} list that is returned from
     * the {@link #getStylers()} implementation.
     * 
     * @return A {@link Map} of property names to {@link PXStyler} instances.
     */
    public Map<String, PXStyler> getStylersByProperty() {
        if (stylersByProperty == null) {
            // build a map of property names to stylers
            stylersByProperty = new HashMap<String, PXStyler>();
            List<PXStyler> viewStylers = getStylers();
            if (!CollectionUtil.isEmpty(viewStylers)) {
                for (PXStyler styler : viewStylers) {
                    for (String property : styler.getSupportedProperties()) {
                        stylersByProperty.put(property, styler);
                    }
                }
            }
        }
        return stylersByProperty;
    }

    public static void initDefaultStyleAdapters() {
        // This all could be in some kind of configuration.
        // @formatter:off
        registerStyleAdapter(View.class.getName(), PXViewStyleAdapter.getInstance());
        registerStyleAdapter(TextView.class.getName(), PXTextViewStyleAdapter.getInstance());
        registerStyleAdapter(ListView.class.getName(), PXListViewStyleAdapter.getInstance());
        registerStyleAdapter(Button.class.getName(), PXButtonStyleAdapter.getInstance());
        registerStyleAdapter(CompoundButton.class.getName(), PXCompoundButtonStyleAdapter.getInstance());
        registerStyleAdapter(ToggleButton.class.getName(), PXToggleButtonStyleAdapter.getInstance());
        registerStyleAdapter(CheckBox.class.getName(), PXCheckboxStyleAdapter.getInstance());
        registerStyleAdapter(RadioButton.class.getName(), PXRadioButtonStyleAdapter.getInstance());
        registerStyleAdapter(GridView.class.getName(), PXGridViewStyleAdapter.getInstance());
        registerStyleAdapter(ImageView.class.getName(), PXImageViewStyleAdapter.getInstance());
        registerStyleAdapter(ImageButton.class.getName(), PXImageButtonStyleAdapter.getInstance());
        registerStyleAdapter(Spinner.class.getName(), PXSpinnerStyleAdapter.getInstance());
        registerStyleAdapter(EditText.class.getName(), PXEditTextStyleAdapter.getInstance());
        registerStyleAdapter(CheckedTextView.class.getName(), PXCheckedTextViewStyleAdapter.getInstance());
        registerStyleAdapter("com.android.internal.widget.ScrollingTabContainerView$TabView", PXTabViewStyleAdapter.getInstance());
        registerStyleAdapter("com.android.internal.view.menu.ActionMenuPresenter$OverflowMenuButton", PXActionBarOverflowStyleAdapter.getInstance());

        // Non-view adapters.
        registerStyleAdapter(ActionBar.class.getName(), PXActionBarStyleAdapter.getInstance());

        // Virtual adapters.
        registerStyleAdapter(PXVirtualListOverscroll.class.getName(), PXVirtualOverscrollListAdapter.getInstance());
        registerStyleAdapter(PXVirtualTopIcon.class.getName(), PXVirtualIconAdapter.getInstance(IconPosition.TOP));
        registerStyleAdapter(PXVirtualRightIcon.class.getName(), PXVirtualIconAdapter.getInstance(IconPosition.RIGHT));
        registerStyleAdapter(PXVirtualBottomIcon.class.getName(), PXVirtualIconAdapter.getInstance(IconPosition.BOTTOM));
        registerStyleAdapter(PXVirtualLeftIcon.class.getName(), PXVirtualIconAdapter.getInstance(IconPosition.LEFT));
        registerStyleAdapter(PXVirtualCompoundButtonIcon.class.getName(), PXVirtualCompoundButtonIconAdapter.getInstance());
        registerStyleAdapter(PXVirtualToggle.class.getName(), PXVirtualToggleAdapter.getInstance());
        registerStyleAdapter(PXVirtualCheckboxIcon.class.getName(), PXVirtualCheckboxIconAdapter.getInstance());
        registerStyleAdapter(PXVirtualListSelector.class.getName(), PXVirtualListSelectorAdapter.getInstance());
        registerStyleAdapter(PXVirtualImageViewImage.class.getName(), PXVirtualImageViewImageAdapter.getInstance());
        registerStyleAdapter(PXVirtualDropdown.class.getName(), PXVirtualDropDownAdapter.getInstance());
        registerStyleAdapter(PXVirtualCheckedTextViewIcon.class.getName(), PXVirtualCheckedTextViewIconAdapter.getInstance());
        registerStyleAdapter(PXVirtualActionBarTabIcon.class.getName(), PXVirtualActionBarTabIconAdapter.getInstance());
        registerStyleAdapter(PXVirtualActionBarStacked.class.getName(), PXVirtualActionBarStackedAdapter.getInstance());
        registerStyleAdapter(PXVirtualActionBarSplit.class.getName(), PXVirtualActionBarSplitAdapter.getInstance());
        registerStyleAdapter(PXVirtualActionBarOverflowImage.class.getName(), PXVirtualActionBarOverflowAdapter.getInstance());
        registerStyleAdapter(PXVirtualActionBarIcon.class.getName(), PXVirtualActionBarIconAdapter.getInstance());
        registerStyleAdapter(PXVirtualActionBarLogo.class.getName(), PXVirtualActionBarLogoAdapter.getInstance());
        // @formatter:on

        // This is hideous, but DOM implementations are put on
        // devices/emulators.
        // At compile time, the DOM is only a set of interfaces, and we need
        // concrete classes for our registry.
        try {
            registerStyleAdapter(DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .newDocument().createElement("a").getClass().getName(),
                    PXDOMStyleAdapter.getInstance());
        } catch (Exception e) {
            PXLog.e(TAG, e, "Unable to instantiate DOM implementation.");
        }
    }

}
