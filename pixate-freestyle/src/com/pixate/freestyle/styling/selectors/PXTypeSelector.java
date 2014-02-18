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

import java.util.ArrayList;
import java.util.List;

import com.pixate.freestyle.parsing.PXSourceWriter;
import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.selectors.PXSpecificity.PXSpecificityType;
import com.pixate.freestyle.util.CollectionUtil;
import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.StringUtil;

public class PXTypeSelector extends PXSelector {

    public static final String UNIVERSAL = "*";
    private String typeName;
    private String namespaceURI;
    private String pseudoElement;
    private List<PXSelector> attributeExpressions;

    /**
     * Constructs a type selector with a universal type-name and name-space.
     */
    public PXTypeSelector() {
        this(UNIVERSAL, UNIVERSAL);
    }

    /**
     * Constructs a type selector with a given type-name and a universal
     * name-space. name.
     * 
     * @param typeName
     */
    public PXTypeSelector(String typeName) {
        this(UNIVERSAL, typeName);
    }

    /**
     * Constructs a type selector with a given type-name and name-space.
     * 
     * @param namespaceURI
     * @param typeName
     */
    public PXTypeSelector(String namespaceURI, String typeName) {
        super(PXSpecificityType.ELEMENT);
        this.typeName = typeName;
        this.namespaceURI = namespaceURI;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public String getPseudoElement() {
        return pseudoElement;
    }

    public void setPseudoElement(String pseudoElement) {
        this.pseudoElement = pseudoElement;
    }

    public boolean hasUniversalNamespace() {
        return UNIVERSAL.equals(namespaceURI);
    }

    public boolean hasUniversalType() {
        return UNIVERSAL.equals(typeName);
    }

    public void addAttributeExpression(PXSelector expression) {
        if (expression != null) {
            if (attributeExpressions == null) {
                attributeExpressions = new ArrayList<PXSelector>(3);
            }
            attributeExpressions.add(expression);
        }
    }

    public List<PXSelector> getAttributeExpressions() {
        return attributeExpressions != null ? new ArrayList<PXSelector>(attributeExpressions)
                : null;
    }

    public boolean hasPseudoClasses() {
        if (attributeExpressions != null) {
            for (PXSelector selector : attributeExpressions) {
                if (selector instanceof PXPseudoClassSelector) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasPseudoClass(String className) {
        if (attributeExpressions != null) {
            for (PXSelector selector : attributeExpressions) {
                if (selector instanceof PXPseudoClassSelector) {
                    PXPseudoClassSelector pseudoClass = (PXPseudoClassSelector) selector;

                    if (pseudoClass.getClassName().equals(className)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getStyleId() {
        String result = null;

        if (attributeExpressions != null) {
            for (PXSelector expression : attributeExpressions) {
                if (expression instanceof PXIdSelector) {
                    PXIdSelector idSelector = (PXIdSelector) expression;
                    result = idSelector.getIdValue();
                    break;
                }
            }
        }
        return result;
    }

    public List<String> getStyleClasses() {
        List<String> result = null;
        if (attributeExpressions != null) {
            for (PXSelector expression : attributeExpressions) {
                if (expression instanceof PXClassSelector) {
                    PXClassSelector classSelector = (PXClassSelector) expression;

                    if (result == null) {
                        result = new ArrayList<String>();
                    }
                    result.add(classSelector.getClassName());
                }
            }
        }
        return result;
    }

    public void incrementSpecificity(PXSpecificity specificity) {
        if (specificity != null && (!hasUniversalType() || !StringUtil.isEmpty(pseudoElement))) {
            specificity.incrementSpecifity(PXSpecificityType.ELEMENT);
        }

        if (!CollectionUtil.isEmpty(attributeExpressions)) {
            for (PXSelector expr : attributeExpressions) {
                expr.incrementSpecificity(specificity);
            }
        }
    }

    @Override
    public boolean matches(Object element) {
        boolean result = false;
        PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(element);

        // filter by namespace
        if (hasUniversalNamespace()) {
            result = true;
        } else {
            String elementNamespace = styleAdapter.getElementNamespace(element);
            if (namespaceURI == null) {
                result = StringUtil.isEmpty(elementNamespace);
            } else {
                // the URIs should match
                result = namespaceURI.equals(elementNamespace);
            }
        }
        // filter by type name
        if (result) {
            if (!hasUniversalType()) {
                result = typeName != null && typeName.equals(styleAdapter.getElementName(element));
            }
        }

        // filter by attribute expression
        if (result && attributeExpressions != null) {
            for (PXSelector expression : attributeExpressions) {
                if (!expression.matches(element)) {
                    result = false;
                    break;
                }
            }
        }

        // TODO: filter by pseudo-element
        if (result) {
            if (!StringUtil.isEmpty(pseudoElement)) {
                List<String> pseudoElements = styleAdapter.getSupportedPseudoElements(element);
                result = pseudoElements != null && pseudoElements.contains(pseudoElements);
            }
        }

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXTypeSelector.class.getSimpleName(), "%s matched %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXTypeSelector.class.getSimpleName(), "%s did not match %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            }
        }
        return result;
    }

    public void getSourceWithSourceWriter(PXSourceWriter writer) {
        // TODO: support namespace
        writer.printIndent();
        writer.print("(");

        if (hasUniversalType()) {
            writer.print(UNIVERSAL);
        } else {
            writer.print(typeName);
        }

        if (!CollectionUtil.isEmpty(attributeExpressions)) {
            writer.increaseIndent();

            for (PXSelector expr : attributeExpressions) {
                writer.println();
                expr.getSourceWithSourceWriter(writer);
            }

            writer.decreaseIndent();
        }

        if (!StringUtil.isEmpty(pseudoElement)) {
            writer.increaseIndent();

            writer.println();
            writer.printIndent();
            writer.print("(PSEUDO_ELEMENT ");
            writer.print(pseudoElement);
            writer.print(")");

            writer.decreaseIndent();
        }

        writer.print(")");
    }

    @Override
    public String toString() {
        List<String> parts = new ArrayList<String>();

        if (hasUniversalNamespace()) {
            parts.add(UNIVERSAL);
        } else {
            if (namespaceURI != null) {
                parts.add(namespaceURI);
            }
        }

        parts.add("|");

        if (hasUniversalType()) {
            parts.add(UNIVERSAL);
        } else {
            parts.add(typeName);
        }

        if (attributeExpressions != null) {
            for (PXSelector expr : attributeExpressions) {
                parts.add(expr.toString());
            }
        }

        if (!StringUtil.isEmpty(pseudoElement)) {
            parts.add("::" + pseudoElement);
        }

        return CollectionUtil.toString(parts, StringUtil.EMPTY);
    }
}
