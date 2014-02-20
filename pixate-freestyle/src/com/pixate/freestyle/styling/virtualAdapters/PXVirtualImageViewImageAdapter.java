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
package com.pixate.freestyle.styling.virtualAdapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.adapters.PXImageViewStyleAdapter;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.cache.PXStyleInfo;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.util.PXDrawableUtil;

/**
 * A virtual child adapter that handles the image settings on an
 * {@link ImageView}. The adapter can control the image drawable that is set for
 * the {@link ImageView}.<br>
 * For example:
 * 
 * <pre>
 * .imageView image {
 *     background-image: url(mic-on.svg);
 *     background-size: 300px;
 * }
 * 
 * .imageView image:pressed {
 *     background-image: url(mic-off.svg);
 *     background-size: 300px;
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
@PXDocElement
public class PXVirtualImageViewImageAdapter extends PXVirtualChildAdapter {

    private static String ELEMENT_NAME = "image";
    private static PXVirtualImageViewImageAdapter instance;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualImageViewImageAdapter}
     */
    public static PXVirtualImageViewImageAdapter getInstance() {
        synchronized (PXVirtualImageViewImageAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualImageViewImageAdapter();
            }
        }
        return instance;
    }

    protected PXVirtualImageViewImageAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getSupportedPseudoClasses
     * (java.lang.Object)
     */
    @Override
    public List<String> getSupportedPseudoClasses(Object styleable) {
        // have to implement this here to support the image states (e.g.
        // pressed, default etc.)
        return new ArrayList<String>(PXDrawableUtil.getSupportedStates().keySet());
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getDefaultPseudoClass
     * (java.lang.Object)
     */
    @Override
    public String getDefaultPseudoClass(Object styleable) {
        return PXStyleInfo.DEFAULT_STYLE;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getElementName(java
     * .lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#updateStyle(java.
     * util.List, java.util.List)
     */
    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        // skip the super implementation, and just check for the validity first.
        if (!validateStyleUpdateParameters(ruleSets, contexts)) {
            return false;
        }
        ImageView view = (ImageView) contexts.get(0).getStyleable();
        // Grab any existing states.
        Map<int[], Drawable> existingStates = PXDrawableUtil
                .getExistingStates(getViewDrawable(view));
        Drawable newImage;
        if (existingStates == null || existingStates.isEmpty()) {
            newImage = PXDrawableUtil.createNewStateListDrawable(getAdapter(), ruleSets, contexts);
        } else {
            // We have to tell the ImageView that it's clickable.
            view.setClickable(true);
            newImage = PXDrawableUtil.createDrawable(getAdapter(), existingStates, ruleSets,
                    contexts);
        }
        // Generate the new StateListDrawable and set it as the ImageView
        // drawable
        view.setImageDrawable(newImage);
        return true;
    }

    /**
     * Returns the Drawable that will be used to compute the required states and
     * existing drawables. Subclasses may overwrite.
     * 
     * @param view
     * @return A {@link Drawable}
     */
    protected Drawable getViewDrawable(ImageView view) {
        return view.getDrawable();
    }

    /**
     * Returns the adapter that will be used when computing the states.
     * 
     * @return A {@link PXStyleAdapter}
     */
    protected PXImageViewStyleAdapter getAdapter() {
        return PXImageViewStyleAdapter.getInstance();
    }
}
