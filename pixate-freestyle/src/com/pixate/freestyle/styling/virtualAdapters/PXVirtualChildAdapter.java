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

import android.graphics.RectF;

import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.stylers.PXAnimationStyler;
import com.pixate.freestyle.styling.stylers.PXBorderStyler;
import com.pixate.freestyle.styling.stylers.PXFillStyler;
import com.pixate.freestyle.styling.stylers.PXLayoutStyler;
import com.pixate.freestyle.styling.stylers.PXShapeStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXTransformStyler;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualStyleable;

/**
 * A virtual child adapter base class for adapters that are interested in
 * view-like actions on a virtual child. By default, the virtual adapter
 * supports the following attributes:
 * 
 * <pre>
 *  - transform: <transform>+
 *  - position: <size>
 *  - top: <length>
 *  - left: <length>
 *  - size: <size>
 *  - width: <length>
 *  - height: <length>
 *  - shape: ellipse | rectangle | arrow-button-left | arrow-button-right
 *  - background-color: <paint>
 *  - background-image: <url>
 *  - background-size: <size>  (default is 32x32)
 *  - background-inset: <inset>
 *  - background-inset-top: <length>
 *  - background-inset-right: <length>
 *  - background-inset-bottom: <length>
 *  - background-inset-left: <length>
 *  - background-padding: <padding>
 *  - background-padding-top: <length>
 *  - background-padding-right: <length>
 *  - background-padding-bottom: <length>
 *  - background-padding-left: <length>
 *  - border: <width> || <border-style> || <paint>
 *  - border-top: <width> || <border-style> || <paint>
 *  - border-right: <width> || <border-style> || <paint>
 *  - border-bottom: <width> || <border-style> || <paint>
 *  - border-left: <width> || <border-style> || <paint>
 *  - border-radius: <size>{1,4}
 *  - border-top-left-radius: <length>
 *  - border-top-right-radius: <length>
 *  - border-bottom-right-radius: <length>
 *  - border-bottom-left-radius: <length>
 *  - border-width: <length>{1,4}
 *  - border-top-width: <length>
 *  - border-right-width: <length>
 *  - border-bottom-width: <length>
 *  - border-left-width: <length>
 *  - border-color: <paint>{1,4}
 *  - border-top-color: <paint>
 *  - border-right-color: <paint>
 *  - border-bottom-color: <paint>
 *  - border-left-color: <paint>
 *  - border-style: <border-style>{1,4}
 *  - border-top-style: <border-style>
 *  - border-right-style: <border-style>
 *  - border-bottom-style: <border-style>
 *  - border-left-style: <border-style>
 *  - animation:
 *  - animation-name: <name>+
 *  - animation-duration: <time>+
 *  - animation-timing-function: <timing-function>+
 *  - animation-iteration-count: <number>+
 *  - animation-direction: <direction>+
 *  - animation-play-state: <play-state>+
 *  - animation-delay: <number>+
 *  - animation-fill-mode: <fill-mode>+
 * 
 * </pre>
 * 
 * @author Shalom Gibly
 */
public abstract class PXVirtualChildAdapter extends PXStyleAdapter {

    private static RectF DEFAULT_BOUNDS = null;

    // To allow our document generation program to run,
    // we need to load this class in a "normal" (not Android)
    // Java runtime environment. Unfortunately RectF is just a stub
    // in that environment, so we get a runtime error. This ignores
    // it when it happens.
    static {
        try {
            DEFAULT_BOUNDS = new RectF(0f, 0f, 32f, 32f);
        } catch (Exception ex) {
            // Ignored
        }
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#createStylers()
     */
    protected List<PXStyler> createStylers() {
        // We only add stylers that update the context! None of these stylers
        // should update the View directly (the virtual parent, which is the
        // real view).
        List<PXStyler> stylers = new ArrayList<PXStyler>();
        stylers.add(PXTransformStyler.getInstance());
        stylers.add(PXLayoutStyler.getInstance());
        stylers.add(PXShapeStyler.getInstance());
        stylers.add(PXFillStyler.getInstance());
        stylers.add(PXBorderStyler.getInstance());
        stylers.add(PXAnimationStyler.getInstance());
        return stylers;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#getBounds(java
     * .lang.Object)
     */
    @Override
    public RectF getBounds(Object styleable) {
        // TODO For now we just return a 32x32 bounds for the background
        // button-image for this compound. Eventually, we'll need to support the
        // button's icon as a virtual child and have the virtual return the
        // icon's bounds.
        return DEFAULT_BOUNDS;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getParent(java.lang
     * .Object)
     */
    @Override
    public Object getParent(Object styleable) {
        // Make sure we return the virtual styleable parent, which is a 'real'
        // view.
        if (styleable instanceof PXVirtualStyleable) {
            return ((PXVirtualStyleable) styleable).getParent();
        }
        return super.getParent(styleable);
    }
}
