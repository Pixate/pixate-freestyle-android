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

import java.util.Collection;

import com.pixate.freestyle.styling.PXDeclaration;

/**
 * <p>
 * A collection of methods used to apply styles defined in a PXRuleSet to a
 * styleable object such as a View. Stylers have four phases:
 * </p>
 * <ol>
 * <li>setup</li>
 * <li>compute styles</li>
 * <li>apply styles</li>
 * <li>clean up</li>
 * </ol>
 * <p>
 * The setup and cleanup stages are optional and only need to be used if state
 * needs to be initialized before computing style information or if that state
 * needs to be cleared once styling has completed.
 * </p>
 * <p>
 * The compute styles phase extracts information from a ruleSet and may use the
 * view to be styled when performing those calculations.
 * </p>
 * <p>
 * The apply styles phase creates any instances needed for styling and applies
 * these to View (or other styleable) instance.
 * </p>
 */
public interface PXStyler {

    /**
     * The list of properties supported by this styler
     */
    Collection<String> getSupportedProperties();

    /**
     * Calculate styling information using the specified declaration. This will
     * be called once for each property name supported by the styler.
     * 
     * @param declaration The declaration to process
     * @param context The styler context
     */
    void processDeclaration(PXDeclaration declaration, PXStylerContext stylerContext);

    /**
     * Apply the computed styles to the specified view.
     * 
     * @param context The styler context
     */
    void applyStylesWithContext(PXStylerContext stylerContext);
}
