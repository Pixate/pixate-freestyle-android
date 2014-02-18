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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.util.MapUtil;

/**
 * A common base class to simplify implementation of new stylers
 */
public abstract class PXStylerBase implements PXStyler {

    public interface PXStylerInvocation {
        /**
         * Called once a styler is ready to be applied to a control.
         * 
         * @param view The view to be styled
         * @param styler The styler to use when styling the view
         * @param context Any additional context associated with this styling
         *            cycle
         */
        void invoke(Object view, PXStyler styler, PXStylerContext context);
    }

    public interface PXDeclarationHandler {
        /**
         * Called for a given property.
         * 
         * @param declaration The declaration to process
         * @param context Any additional context associated with this styling
         *            cycle
         */
        void process(PXDeclaration declaration, PXStylerContext stylerContext);
    }

    private static final List<String> EMPTY_PROPERTY_LIST = Collections.unmodifiableList(new ArrayList<String>(0));

    protected PXStylerInvocation stylerInvocation;

    public PXStylerBase(PXStylerInvocation invocation) {
        this.stylerInvocation = invocation;
    }

    // Implementations

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.stylers.PXStyler#getSupportedProperties()
     */
    public Collection<String> getSupportedProperties() {
        Map<String, PXDeclarationHandler> handlers = getDeclarationHandlers();
        if (!MapUtil.isEmpty(handlers)) {
            return handlers.keySet();
        }
        return EMPTY_PROPERTY_LIST;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.stylers.PXStyler#processDeclaration(com.pixate.freestyle
     * .pxengine.styling.PXDeclaration,
     * com.pixate.freestyle.styling.stylers.PXStylerContext)
     */
    public void processDeclaration(PXDeclaration declaration, PXStylerContext stylerContext) {
        Map<String, PXDeclarationHandler> handlers = getDeclarationHandlers();
        if (!MapUtil.isEmpty(handlers)) {
            PXDeclarationHandler handler = handlers.get(declaration.getName());
            if (handler != null) {
                handler.process(declaration, stylerContext);
            }
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.stylers.PXStyler#applyStylesWithContext(com
     * .pixate.pxengine.styling.stylers.PXStylerContext)
     */
    public void applyStylesWithContext(PXStylerContext stylerContext) {
        if (stylerInvocation != null) {
            stylerInvocation.invoke(stylerContext.getStyleable(), this, stylerContext);
        }
    }

    // Getters

    // Helper Methods

    public abstract Map<String, PXDeclarationHandler> getDeclarationHandlers();

}
