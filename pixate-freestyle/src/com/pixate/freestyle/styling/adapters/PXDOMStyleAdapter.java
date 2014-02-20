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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXStylerContext;

@PXDocElement(hide=true)
public class PXDOMStyleAdapter extends PXStyleAdapter {
    private static PXDOMStyleAdapter sInstance;

    private PXDOMStyleAdapter() {
    }

    @Override
    protected List<PXStyler> createStylers() {
        return Collections.emptyList();

    }

    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        for (int i = 0; i < ruleSets.size(); i++) {
            PXStylerContext context = contexts.get(i);
            PXRuleSet ruleSet = ruleSets.get(i);
            Node node = (Node) context.getStyleable();
            Document ownerDocument = node.getOwnerDocument();
            NamedNodeMap attributes = node.getAttributes();
            for (PXDeclaration declaration : ruleSet.getDeclarations()) {
                String name = declaration.getName();
                String value = declaration.getStringValue();

                // Set the node's attribute
                Node attNode = ownerDocument.createAttribute(name);
                attNode.setNodeValue(value);
                attributes.setNamedItem(attNode);
            }
        }
        return true;
    }

    @Override
    public String getElementName(Object object) {
        return ((Node) object).getNodeName();
    }

    @Override
    public String getStyleId(Object object) {
        return getAttributeValue(object, "id");
    }

    @Override
    public String getStyleClass(Object object) {
        return getAttributeValue(object, "class");
    }

    @Override
    public int getIndexInParent(Object styleable) {
        Node node = (Node) styleable;
        Node parentNode = node.getParentNode();
        if (parentNode != null) {
            NodeList siblings = parentNode.getChildNodes();
            // find the node's index
            for (int i = 0; i < siblings.getLength(); i++) {
                Node sibling = siblings.item(i);
                if (sibling == node) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public String getElementNamespace(Object styleable) {
        return ((Node) styleable).getNamespaceURI();
    }

    @Override
    public String getAttributeValue(Object styleable, String attribute) {
        Node node = (Node) styleable;
        NamedNodeMap attributes = node.getAttributes();
        String result = null;
        if (attributes != null) {
            Node idAttr = attributes.getNamedItem(attribute);
            if (idAttr != null) {
                result = idAttr.getNodeValue();
            }
        }
        return result;
    }

    @Override
    public String getAttributeValue(Object styleable, String attributeName, String namespaceURI) {
        Node node = (Node) styleable;
        if (node.hasAttributes()) {
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                if (attr.getLocalName().equals(attributeName)) {
                    // check for any namespace prefix (TODO - we check for the
                    // attribute prefix and for the node's namespace URI here.
                    // There is a chance that only one way is needed, but for
                    // now we keep both)
                    if (namespaceURI == null) {
                        return attr.getNamespaceURI() == null ? attr.getNodeValue() : null;
                    }
                    if ("*".equals(namespaceURI) || namespaceURI.equals(attr.getNamespaceURI())) {
                        return attr.getNodeValue();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object getSiblingAt(Object styleable, int offset) {
        return getSiblingAt((Node) styleable, offset, true);
    }

    /**
     * Returns the element children of the given node (Note: only the
     * ELEMENT_NODEs will be returned)
     * 
     * @param node
     * @return
     */
    @Override
    public List<Object> getElementChildren(Object styleable) {
        return getElementChildren((Node) styleable, true);
    }

    @Override
    public int getChildCount(Object styleable) {
        List<Object> children = getElementChildren(styleable);
        return children == null ? 0 : children.size();
    }

    @Override
    public Object getParent(Object styleable) {
        return ((Node) styleable).getParentNode();
    }

    @Override
    public boolean isSupportingStylers() {
        return false;
    }

    // Private

    private Object getSiblingAt(Node node, int offset, boolean skipNonElement) {
        Object result = null;
        int indexInParent = getIndexInParent(node);
        if (indexInParent != -1) {
            int siblingIndex = indexInParent + offset;
            NodeList siblings = node.getParentNode().getChildNodes();
            // Get the sibling at the offset. In case it's out of
            // bounds, return null.
            while (result == null) {
                if (siblingIndex >= 0 && siblingIndex < siblings.getLength()) {
                    Node sibling = siblings.item(siblingIndex);
                    if (skipNonElement && sibling.getNodeType() != Document.ELEMENT_NODE) {
                        // skip the non-element node
                        siblingIndex += (offset < 0) ? -1 : 1;
                    } else {
                        result = sibling;
                        break;
                    }
                } else {
                    // out of bounds
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns the children of the given node. In case 'onlyElements' is passed,
     * only the ELEMENT_NODEs will be returned.
     * 
     * @param node
     * @param onlyElements
     * @return
     */
    private List<Object> getElementChildren(Node node, boolean onlyElements) {
        NodeList childNodes = node.getChildNodes();
        if (childNodes == null) {
            return Collections.emptyList();
        }
        List<Object> children = new ArrayList<Object>(childNodes.getLength());
        for (int i = 0; i < childNodes.getLength(); i++) {
            short nodeType = childNodes.item(i).getNodeType();
            if (!onlyElements
                    || (onlyElements && nodeType != Document.COMMENT_NODE && nodeType != Document.PROCESSING_INSTRUCTION_NODE)) {
                children.add(childNodes.item(i));
            }
        }
        return children;
    }

    // Statics

    public static PXDOMStyleAdapter getInstance() {
        synchronized (PXDOMStyleAdapter.class) {
            if (sInstance == null) {
                sInstance = new PXDOMStyleAdapter();
            }
        }

        return sInstance;
    }

}
