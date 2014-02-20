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
package com.pixate.freestyle.styling.stylers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.widget.TextView;

import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.stylers.PXStylerContext.IconPosition;

/**
 * An icon styler that is defined for styling one of four icons that can be set
 * on a {@link TextView}.
 * 
 * <pre>
 * - background-image: &lt;url&gt;
 * - background-size: &lt;size&gt; (default 32x32)
 * </pre>
 */
@PXDocStyler(hide=true)
public class PXCompoundIconStyler extends PXStylerBase {

    private static Map<IconPosition, PXCompoundIconStyler> instances = new HashMap<IconPosition, PXCompoundIconStyler>();
    private IconPosition position;

    public PXCompoundIconStyler(PXStylerInvocation invocation, IconPosition position) {
        super(invocation);
        this.position = position;
    }

    public static PXCompoundIconStyler getInstance(IconPosition position) {
        PXCompoundIconStyler instance = instances.get(position);
        synchronized (PXCompoundIconStyler.class) {
            if (instance == null) {
                instance = new PXCompoundIconStyler(null, position);
                instances.put(position, instance);
            }
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.stylers.PXStylerBase#getDeclarationHandlers()
     */
    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        return Collections.emptyMap();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.stylers.PXStylerBase#applyStylesWithContext
     * (com.pixate.freestyle.styling.stylers.PXStylerContext)
     */
    @Override
    public void applyStylesWithContext(PXStylerContext stylerContext) {
        super.applyStylesWithContext(stylerContext);
        stylerContext.setCompoundIcon(position, stylerContext.getBackgroundImage());
    }
}
