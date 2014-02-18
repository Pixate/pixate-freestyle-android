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
package com.pixate.freestyle.styling.selectors;

import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.selectors.PXSpecificity.PXSpecificityType;
import com.pixate.freestyle.util.PXLog;

public class PXAttributeSelector extends PXSelector {

    private String namespaceURI;
    private String attributeName;

    /**
     * Initialize a new instance using the specified type name
     * 
     * @param name The attribute name to match
     */
    public PXAttributeSelector(String name) {
        super(PXSpecificityType.CLASS_OR_ATTRIBUTE);
        this.attributeName = name;
    }

    /**
     * Initialize a new instance using the specified namespace and name
     * 
     * @param uri The attribute namespace URI to match
     * @param name The attribute name to match
     */
    public PXAttributeSelector(String uri, String attributeName) {
        this(attributeName);
        this.namespaceURI = uri;
    }

    public boolean matches(Object element) {

        boolean result =
                PXStyleAdapter.getStyleAdapter(element).getAttributeValue(element, attributeName, namespaceURI) != null;

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXAttributeSelector.class.getSimpleName(), "%s matched %s", attributeName,
                        PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXAttributeSelector.class.getSimpleName(), "%s did not match %s",
                        attributeName, PXStyleUtils.getDescriptionForStyleable(element));
            }
        }
        return result;
    }

    /**
     * The attribute namespace URI to match. This value may be nil
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * The attribute name to match. This value may be nil
     */
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public String toString() {
        if (namespaceURI != null) {
            return String.format("[%s|%s]", namespaceURI, attributeName);
        } else {
            return String.format("[*|%s]", attributeName);
        }
    }
}
