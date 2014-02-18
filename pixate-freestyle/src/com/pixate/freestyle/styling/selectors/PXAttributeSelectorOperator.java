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
import com.pixate.freestyle.util.StringUtil;

public class PXAttributeSelectorOperator extends PXSelector {

    /**
     * The PXAttributeSelectorOperatorType enumeration defines the operators
     * available when matching the content of an attribute against a value.
     */
    public enum PXAttributeSelectorOperatorType {
        STARTS_WITH,
        ENDS_WITH,
        CONTAINS,
        EQUAL,
        LIST_CONTAINS,
        EQUAL_WITH_HYPHEN
    };

    private PXAttributeSelectorOperatorType operatorType;
    private PXAttributeSelector attributeSelector;
    private String value;

    /**
     * Initialize a new instance using the specified lhs, operator, and rhs of
     * the operator expression.
     * 
     * @param type The operator type
     * @param attributeSelector The attribute selector to which the operator
     *            will be applied
     * @param value The string value used by the operator when matching the
     *            matched attribute's value
     */
    public PXAttributeSelectorOperator(PXAttributeSelectorOperatorType type,
            PXAttributeSelector attributeSelector, String value) {
        super(PXSpecificityType.CLASS_OR_ATTRIBUTE);
        this.operatorType = type;
        this.attributeSelector = attributeSelector;
        this.value = value;
    }

    public boolean matches(Object element) {
        boolean result = false;
        String attrValue = PXStyleAdapter.getStyleAdapter(element).getAttributeValue(element,
                attributeSelector.getAttributeName(), attributeSelector.getNamespaceURI());

        if (!StringUtil.isEmpty(attrValue)) {
            switch (operatorType) {
                case STARTS_WITH:
                    result = attrValue.startsWith(this.value);
                    break;

                case ENDS_WITH:
                    result = attrValue.endsWith(this.value);
                    break;

                case CONTAINS:
                    result = attrValue.indexOf(this.value) > -1;
                    break;

                case EQUAL:
                    result = attrValue.equals(this.value);
                    break;

                case LIST_CONTAINS: {
                    String[] components = PXStyleUtils.PATTERN_WHITESPACE.split(attrValue);
                    result = StringUtil.contains(components, this.value);
                    break;
                }

                case EQUAL_WITH_HYPHEN: {
                    result = attrValue.equals(this.value);

                    if (!result) {
                        result = attrValue.startsWith(this.value + "-");
                    }
                    break;
                }
            }
        }

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXAttributeSelectorOperator.class.getSimpleName(), "%s matched %s",
                        toString(), PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXAttributeSelectorOperator.class.getSimpleName(), "%s did not match %s",
                        toString(), PXStyleUtils.getDescriptionForStyleable(element));
            }
        }

        return result;
    }

    /**
     * Returns the type of match to be performed on the attribute value
     */
    public PXAttributeSelectorOperatorType getOperatorType() {
        return operatorType;
    }

    /**
     * Returns the attribute to match
     */
    public PXAttributeSelector getAttributeSelector() {
        return attributeSelector;
    }

    /**
     * Returns the value to be used by the operator type during matching
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        String operator = "";

        switch (operatorType) {
            case CONTAINS:
                operator = "*=";
                break;
            case ENDS_WITH:
                operator = "$=";
                break;
            case EQUAL:
                operator = "=";
                break;
            case EQUAL_WITH_HYPHEN:
                operator = "|=";
                break;
            case LIST_CONTAINS:
                operator = "~=";
                break;
            case STARTS_WITH:
                operator = "^=";
                break;
        }

        if (attributeSelector.getNamespaceURI() != null) {
            return String.format("%s|%s%s%s", attributeSelector.getNamespaceURI(),
                    attributeSelector.getAttributeName(), operator, value);
        } else {
            return String.format("*|%s%s%s", attributeSelector.getAttributeName(), operator, value);
        }
    }
}
