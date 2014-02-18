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
package com.pixate.pxengine.styling.selectors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.pixate.pxengine.styling.PXStyleUtils;
import com.pixate.pxengine.styling.PXStylesheet;
import com.pixate.pxengine.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.util.CollectionUtil;
import com.pixate.util.StringUtil;

public class W3CSelectorTests extends AndroidTestCase {

    private static final boolean WRITE_TO_DISC = false;
    private static final boolean OVERWRITE = true;
    // NOTE: This needs to be changed locally for a run that is writing the
    // results.
    private static final String RESULT_FILES_ASSETS_PATH = "W3C/Selectors Level 3/results/";
    private static final String TEST_FILES_ASSETS_PATH = "W3C/Selectors Level 3/source/";
    private static final char[] READ_BUFFER = new char[1024];
    // We remove that string from the results. It being injected by Java since
    // we use DocumentBuilderFactory#setNamespaceAware(true);
    private static final String NAMESPACE_STRING = "\\s+xmlns=\\\"http://www.w3.org/1999/xhtml\\\"";

    // prepare the xpath expressions
    private static XPathExpression TITLE_EXPRESSION;
    private static XPathExpression STYLE_EXPRESSION;
    private static XPathExpression TEST_BODY_EXPRESSION;
    private static XPathExpression ALL_EXPRESSION;
    static {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(new TestNamespaceContext());

        // find title node
        try {
            TITLE_EXPRESSION = xpath.compile("/ns:html/ns:head/ns:title");
            STYLE_EXPRESSION = xpath.compile("/ns:html/ns:head/ns:style");
            TEST_BODY_EXPRESSION = xpath.compile("/ns:html/ns:body/ns:div/ns:div");
            ALL_EXPRESSION = xpath.compile("//*");
        } catch (XPathExpressionException e) {
            Log.e(W3CSelectorTests.class.getSimpleName(), "XPath compilation error", e);
        }
    }
    private static ErrorHandler ERROR_HANDLER = new ErrorHandler() {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
            Log.w(W3CSelectorTests.class.getSimpleName(), exception.getMessage());
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            Log.e(W3CSelectorTests.class.getSimpleName(), exception.getMessage());
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            Log.e(W3CSelectorTests.class.getSimpleName(), exception.getMessage());
        }
    };

    private static class TestNamespaceContext implements NamespaceContext {

        public String getNamespaceURI(String prefix) {
            if ("ns".equals(prefix)) {
                return "http://www.w3.org/1999/xhtml";
            }
            return null;
        }

        public String getPrefix(String namespaceURI) {
            return null;
        }

        @SuppressWarnings("rawtypes")
        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }

    }

    private DocumentBuilder builder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // IMPORTANT since we use namespaces in our selectors
        factory.setNamespaceAware(true);
        // convert CDATA nodes to text nodes.
        factory.setCoalescing(true);
        builder = factory.newDocumentBuilder();
        builder.setErrorHandler(ERROR_HANDLER);
    }

    private void assertStyleForFilename(String fileName) throws Exception, SAXException,
            IOException {
        assertStyleForFilename(fileName, 0);
    }

    private void assertStyleForFilename(String fileName, int errorCount) throws Exception {

        Document doc = builder.parse(getContext().getAssets().open(
                TEST_FILES_ASSETS_PATH + fileName + ".xml"));
        // Document doc = builder.parse(new File(TEST_FILES_ASSETS_PATH +
        // fileName + ".xml"));
        // find title node
        NodeList nl = (NodeList) TITLE_EXPRESSION.evaluate(doc, XPathConstants.NODESET);
        Node titleNode = null;
        if (nl.getLength() > 0) {
            titleNode = nl.item(0);
        }

        // find style node
        nl = (NodeList) STYLE_EXPRESSION.evaluate(doc, XPathConstants.NODESET);
        Node styleNode = null;
        if (nl.getLength() > 0) {
            styleNode = nl.item(0);
        }

        // find test body
        Node bodyNode = null;
        nl = (NodeList) TEST_BODY_EXPRESSION.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i).getAttributes().getNamedItem("class");
            if (n != null && "testText".equals(n.getNodeValue())) {
                bodyNode = nl.item(i);
                break;
            }
        }

        // grab style text
        String styleText = styleNode.getTextContent();

        // create stylesheet
        PXStylesheet stylesheet = PXStylesheet.getStyleSheetFromSource(styleText,
                PXStyleSheetOrigin.APPLICATION);
        List<String> errors = stylesheet.getErrors();
        int errorsSize = CollectionUtil.isEmpty(errors) ? 0 : errors.size();
        if (errorsSize == errorCount) {
            // TODO? [Pixate applyStylesheets];
        }

        // Some checks
        assertNotNull("title should not be null", titleNode);
        assertNotNull("style should not be null", styleText);
        assertNotNull("body should not be null", bodyNode);

        // create a flattened list of all nodes in the body
        NodeList nodes = (NodeList) ALL_EXPRESSION.evaluate(bodyNode, XPathConstants.NODESET);

        // style each element node
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                PXStyleUtils.updateStyle(node);
            }
        }

        // Use a Transformer for output
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        // Check for CDATA nodes and replace them with a regular text nodes.
        // For some reason, setCoalescing(true) doesn't work well...
        List<Node> cdataToRemove = new ArrayList<Node>(3);
        NodeList childNodes = styleNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node n = childNodes.item(i);
            if (n.getNodeType() == Node.CDATA_SECTION_NODE) {
                cdataToRemove.add(n);
            }
        }
        for (Node n : cdataToRemove) {
            styleNode.replaceChild(doc.createTextNode(n.getTextContent()), n);
        }

        Element root = doc.createElement("test");
        root.appendChild(titleNode);
        root.appendChild(styleNode);
        root.appendChild(bodyNode);
        root.normalize();
        DOMSource source = new DOMSource(root);

        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(source, sr);

        // For now, just write it back if needed
        String resultFileName = getResultFileName(fileName);
        String currentResult = sw.toString().replaceAll(NAMESPACE_STRING, StringUtil.EMPTY);
        if (WRITE_TO_DISC) {

            writeText(resultFileName, currentResult, OVERWRITE);
        } else {
            // Compare the result to the 'golden'.
            assertFileContent(resultFileName, currentResult);
        }
    }

    private void assertFileContent(String resultFileName, String currentResult) throws IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(getContext().getAssets().open(
                    RESULT_FILES_ASSETS_PATH + resultFileName));
            StringBuilder expected = new StringBuilder();
            int read = -1;
            while ((read = reader.read(READ_BUFFER)) != -1) {
                expected.append(READ_BUFFER, 0, read);
            }
            assertEquals("Expected equal output", expected.toString(), currentResult.trim());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }

    private void writeText(String fileName, String resultText, boolean overwrite)
            throws IOException {
        // This will write to the device/emulator card. Later, those results
        // have to be copied into the project's assets directory manually to run
        // the test in a "non-writing" mode.
        File writeTarget = new File(Environment.getExternalStorageDirectory(), "results");
        writeTarget.mkdirs();
        File file = new File(writeTarget, fileName);
        if (file.exists() && !overwrite) {
            return;
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(resultText.trim());
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private String getResultFileName(String fileName) {
        return fileName + "-result.xml";
    }

    /**
     * Groups of selectors
     */
    public void test1() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-1");
    }

    /**
     * Type element selectors
     */
    public void test2() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-2");
    }

    /**
     * Universal selector
     */
    public void test3() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-3");
    }

    /**
     * Universal selector (no namespaces)
     */
    public void test3a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-3a");
    }

    /**
     * Omitted universal selector
     */
    public void test4() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-4");
    }

    /**
     * Attribute existence selector
     */
    public void test5() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-5");
    }

    /**
     * Attribute value selector
     */
    public void test6() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-6");
    }

    /**
     * Attribute multivalue selector
     */
    public void test7() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-7");
    }

    /**
     * Attribute multivalue selector
     */
    public void test7b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-7b");
    }

    /**
     * Attribute value selectors (hyphen-separated attributes)
     */
    public void test8() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-8");
    }

    /**
     * Substring matching attribute selector (beginning)
     */
    public void test9() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-9");
    }

    /**
     * Substring matching attribute selector (end)
     */
    public void test10() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-10");
    }

    /**
     * Substring matching attribute selector (contains)
     */
    public void test11() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-11");
    }

    /**
     * Class selectors
     */
    public void test13() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-13");
    }

    /**
     * More than one class selector
     */
    public void test14() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-14");
    }

    /**
     * More than one class selector
     */
    public void test14b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-14b");
    }

    /**
     * More than one class selector
     */
    public void test14c() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-14c");
    }

    /**
     * NEGATED More than one class selector
     */
    public void test14d() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-14d");
    }

    /**
     * NEGATED More than one class selector
     */
    public void test14e() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-14e");
    }

    /**
     * ID selectors
     */
    public void test15() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-15");
    }

    /**
     * Multiple ID selectors
     */
    public void test15b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-15b");
    }

    /**
     * Multiple IDs Treating as invalid. This test requires support for two or
     * more of XHTML, xml:id, and DOM3 Core. It also uses Javascript to alter
     * nodes, which is required for the test to pass.
     */
    /*
     * public void test15c() throws Exception {
     * assertStyleForFilename("css3-modsel-15c");
     * //STFail(@"xml:id not supported"); }
     */

    /**
     * :link pseudo-class Treating as invalid. We don't have a "link" concept
     */
    /*
     * public void test16() throws Exception {
     * assertStyleForFilename("css3-modsel-16");
     * //STFail(@":link pseudo-class not implemented"); }
     */

    /**
     * :visited pseudo-class Treating as invalid. We don't have a "visited"
     * concept
     */
    /*
     * public void test17() throws Exception {
     * assertStyleForFilename("css3-modsel-17");
     * //STFail(@":visited pseudo-class not implemented"); }
     */

    /**
     * :hover pseudo-class Treating as invalid. We don't have a "hover" concept
     */
    /*
     * public void test18() throws Exception {
     * assertStyleForFilename("css3-modsel-18");
     * //STFail(@":hover pseudo-class not implemented"); }
     */

    /**
     * :hover pseudo-class on links Treating as invalid. We don't have a "hover"
     * concept
     */
    /*
     * public void test18a() throws Exception {
     * assertStyleForFilename("css3-modsel-18a");
     * //STFail(@":hover pseudo-class not implemented"); }
     */

    /**
     * :hover pseudo-class Treating as invalid. We don't have a "hover" concept
     */
    /*
     * public void test18b() throws Exception {
     * assertStyleForFilename("css3-modsel-18b");
     * //STFail(@":hover pseudo-class not implemented"); }
     */

    /**
     * :hover pseudo-class on links Treating as invalid. We don't have a "hover"
     * concept
     */
    /*
     * public void test18c() throws Exception {
     * assertStyleForFilename("css3-modsel-18c");
     * //STFail(@":hover pseudo-class not implemented"); }
     */

    /**
     * :active pseudo-class Treating as invalid. We don't have an "active"
     * concept
     */
    /*
     * public void test19() throws Exception {
     * assertStyleForFilename("css3-modsel-19");
     * //STFail(@":active pseudo-class not implemented"); }
     */

    /**
     * :active pseudo-class on controls Treating as invalid. We don't have a
     * "active" concept
     */
    /*
     * public void test19b() throws Exception {
     * assertStyleForFilename("css3-modsel-19b");
     * //STFail(@":active pseudo-class not implemented"); }
     */

    /**
     * :focus pseudo-class Treating as invalid. We don't have a "focus" concept
     */
    /*
     * public void test20() throws Exception {
     * assertStyleForFilename("css3-modsel-20");
     * //STFail(@":focus pseudo-class not implemented"); }
     */

    /**
     * :target pseudo-class Treating as invalid. We don't have a "target"
     * concept
     */
    /*
     * public void test21() throws Exception {
     * assertStyleForFilename("css3-modsel-21");
     * //STFail(@":target pseudo-class not implemented"); }
     */

    /**
     * :target pseudo-class Treating as invalid. We don't have a "target"
     * concept
     */
    /*
     * public void test21b() throws Exception {
     * assertStyleForFilename("css3-modsel-21b");
     * //STFail(@":target pseudo-class not implemented"); }
     */

    /**
     * :target pseudo-class Treating as invalid. We don't have a "target"
     * concept
     */
    /*
     * public void test21c() throws Exception {
     * assertStyleForFilename("css3-modsel-21c");
     * //STFail(@":target pseudo-class not implemented"); }
     */

    /**
     * :lang() pseudo-class Treating as invalid. We don't have a "lang" concept
     */
    /*
     * public void test22() throws Exception {
     * assertStyleForFilename("css3-modsel-22");
     * //STFail(@":lang() pseudo-class not implemented"); }
     */

    /**
     * :enabled pseudo-class Treating as invalid. We don't have an "enabled"
     * concept
     */
    /*
     * public void test23() throws Exception {
     * assertStyleForFilename("css3-modsel-23");
     * //STFail(@":enabled pseudo-class not implemented"); }
     */

    /**
     * :disabled pseudo-class Treating as invalid. We don't have a "disabled"
     * concept
     */
    /*
     * public void test24() throws Exception {
     * assertStyleForFilename("css3-modsel-24");
     * //STFail(@":disabled pseudo-class not implemented"); }
     */

    /**
     * :checked pseudo-class Treating as invalid. We don't have a "checked"
     * concept
     */
    /*
     * public void test25() throws Exception {
     * assertStyleForFilename("css3-modsel-25");
     * //STFail(@":checked pseudo-class not implemented"); }
     */

    /**
     * :root pseudo-class
     */
    public void test27() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-27");
    }

    /**
     * Impossible rules (:root:first-child, etc)
     */

    public void test27a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-27a");
    }

    /**
     * Impossible rules (* html, * :root)
     */
    public void test27b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-27b");
    }

    /**
     * :nth-child() pseudo-class
     */
    public void test28() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-28");
    }

    /**
     * :nth-child() pseudo-class
     */
    public void test28b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-28b");
    }

    /**
     * :nth-last-child() pseudo-class
     */
    public void test29() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-29");
    }

    /**
     * :nth-last-child() pseudo-class
     */
    public void test29b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-29b");
    }

    /**
     * :nth-of-type() pseudo-class
     */
    public void test30() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-30");
    }

    /**
     * :nth-last-of-type() pseudo-class not supported
     */
    public void test31() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-31");
    }

    /**
     * :first-child pseudo-class
     */
    public void test32() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-32");
    }

    /**
     * :last-child pseudo-class
     */
    public void test33() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-33");
    }

    /**
     * :first-of-type pseudo-class
     */
    public void test34() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-34");
    }

    /**
     * :last-of-type pseudo-class
     */
    public void test35() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-35");
    }

    /**
     * :only-child pseudo-class
     */
    public void test36() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-36");
    }

    /**
     * :only-of-type pseudo-class
     */
    public void test37() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-37");
    }

    /**
     * ::first-line pseudo-element not supported
     */
    /*
     * public void test38() throws Exception {
     * assertStyleForFilename("css3-modsel-38");
     * //STFail(@"::first-line pseudo-element not implemented"); }
     */

    /**
     * ::first-letter pseudo-element not supported
     */
    /*
     * public void test39() throws Exception {
     * assertStyleForFilename("css3-modsel-39");
     * //STFail(@"::first-letter pseudo-element not implemented"); }
     */

    /**
     * ::first-letter pseudo-element with ::before pseudo-element not supported
     */
    /*
     * public void test39a() throws Exception {
     * assertStyleForFilename("css3-modsel-39a");
     * //STFail(@"::first-letter pseudo-element not implemented"); }
     */

    /**
     * ::first-letter pseudo-element not supported
     */
    /*
     * public void test39b() throws Exception {
     * assertStyleForFilename("css3-modsel-39b");
     * //STFail(@"::first-letter pseudo-element not implemented"); }
     */

    /**
     * ::first-letter pseudo-element with ::before pseudo-element not supported
     */
    /*
     * public void test39c() throws Exception {
     * assertStyleForFilename("css3-modsel-39c");
     * //STFail(@"::first-letter pseudo-element not implemented"); }
     */

    /**
     * ::before pseudo-element not supported
     */
    /*
     * public void test41() throws Exception {
     * assertStyleForFilename("css3-modsel-41");
     * //STFail(@"::before pseudo-element not implemented"); }
     */

    /**
     * :before pseudo-element not supported
     */
    /*
     * public void test41a() throws Exception {
     * assertStyleForFilename("css3-modsel-41a");
     * //STFail(@"::before pseudo-element not implemented"); }
     */

    /**
     * ::after pseudo-element not supported
     */
    /*
     * public void test42() throws Exception {
     * assertStyleForFilename("css3-modsel-42");
     * //STFail(@"::after pseudo-element not implemented"); }
     */

    /**
     * :after pseudo-element not supported
     */
    /*
     * public void test42a() throws Exception {
     * assertStyleForFilename("css3-modsel-42a");
     * //STFail(@"::after pseudo-element not implemented"); }
     */

    /**
     * Descendant combinator
     */
    public void test43() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-43");
    }

    /**
     * Descendant combinator
     */
    public void test43b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-43b");
    }

    /**
     * Child combinator
     */
    public void test44() throws Exception {
        // verfied
        assertStyleForFilename("css3-modsel-44");
    }

    /**
     * Child combinator
     */
    public void test44b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-44b");
    }

    /**
     * Child combinator and classes
     */
    public void test44c() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-44c");
    }

    /**
     * Child combinatior and IDs
     */
    public void test44d() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-44d");
    }

    /**
     * Direct adjacent combinator
     */
    public void test45() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-45");
    }

    /**
     * Direct adjacent combinator
     */
    public void test45b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-45b");
    }

    /**
     * Direct adjacent combinator and classes
     */
    public void test45c() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-45c");
    }

    /**
     * Indirect adjacent combinator
     */
    public void test46() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-46");
    }

    /**
     * Indirect adjacent combinator
     */
    public void test46b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-46b");
    }

    /**
     * NEGATED type element selector
     */
    public void test47() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-47");
    }

    /**
     * NEGATED universal selector
     */
    public void test48() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-48");
    }

    /**
     * NEGATED omitted universal selector is forbidden
     */
    public void test49() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-49");
    }

    /**
     * NEGATED attribute existence selector
     */
    public void test50() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-50");
    }

    /**
     * NEGATED attribute value selector
     */
    public void test51() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-51");
    }

    /**
     * NEGATED attribute space-separated value selector
     */
    public void test52() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-52");
    }

    /**
     * NEGATED attribute dash-separated value selector
     */
    public void test53() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-53");
    }

    /**
     * NEGATED substring matching attribute selector on beginning
     */
    public void test54() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-54");
    }

    /**
     * NEGATED substring matching attribute selector on end
     */
    public void test55() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-55");
    }

    /**
     * NEGATED substring matching attribute selector on middle
     */
    public void test56() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-56");
    }

    /**
     * NEGATED Attribute existence selector with declared namespace
     */
    public void test57() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-57");
    }

    /**
     * NEGATED Attribute existence selector with declared namespace
     */
    public void test57b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-57b");
    }

    /**
     * NEGATED class selector
     */
    public void test59() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-59");
    }

    /**
     * NEGATED ID selector
     */
    public void test60() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-60");
    }

    /**
     * NEGATED :link pseudo-class
     */
    public void test61() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-61");
    }

    /**
     * NEGATED :visited pseudo-class
     */
    public void test62() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-62");
    }

    /**
     * NEGATED :hover pseudo-class
     */
    /*
     * public void test63() throws Exception {
     * assertStyleForFilename("css3-modsel-63");
     * //STFail(@":hover pseudo-class not implemented"); }
     */

    /**
     * NEGATED :active pseudo-class
     */
    public void test64() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-64");
    }

    /**
     * NEGATED :focus pseudo-class
     */
    /*
     * public void test65() throws Exception {
     * assertStyleForFilename("css3-modsel-65");
     * //STFail(@":focus pseudo-class not implemented"); }
     */

    /**
     * NEGATED :target pseudo-class
     */
    public void test66() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-66");
    }

    /**
     * NEGATED :target pseudo-class
     */
    public void test66b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-66b");
    }

    /**
     * NEGATED :lang() pseudo-class
     */
    /*
     * public void test67() throws Exception {
     * assertStyleForFilename("css3-modsel-67");
     * //STFail(@":lang() pseudo-class not implemented"); }
     */

    /**
     * NEGATED :enabled pseudo-class
     */
    public void test68() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-68");
    }

    /**
     * NEGATED :disabled pseudo-class
     */
    public void test69() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-69");
    }

    /**
     * NEGATED :checked pseudo-class
     */
    public void test70() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-70");
    }

    /**
     * NEGATED :root pseudo-class
     */
    public void test72() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-72");
    }

    /**
     * NEGATED :root pseudo-class
     */
    public void test72b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-72b");
    }

    /**
     * NEGATED :nth-child() pseudo-class
     */
    public void test73() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-73");
    }

    /**
     * NEGATED :nth-child() pseudo-class
     */
    public void test73b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-73b");
    }

    /**
     * NEGATED :nth-last-child() pseudo-class
     */
    public void test74() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-74");
    }

    /**
     * NEGATED :nth-last-child() pseudo-class
     */
    public void test74b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-74b");
    }

    /**
     * NEGATED :nth-of-type() pseudo-class
     */
    public void test75() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-75");
    }

    /**
     * NEGATED :nth-of-type() pseudo-class
     */
    public void test75b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-75b");
    }

    /**
     * NEGATED :nth-last-of-type() pseudo-class
     */
    public void test76() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-76");
    }

    /**
     * NEGATED :nth-last-of-type() pseudo-class
     */
    public void test76b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-76b");
    }

    /**
     * NEGATED :first-child pseudo-class
     */
    public void test77() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-77");
    }

    /**
     * NEGATED :first-child pseudo-class
     */
    public void test77b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-77b");
    }

    /**
     * NEGATED :last-child pseudo-class
     */
    public void test78() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-78");
    }

    /**
     * NEGATED :last-child pseudo-class
     */
    public void test78b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-78b");
    }

    /**
     * NEGATED :first-of-type pseudo-class
     */
    public void test79() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-79");
    }

    /**
     * NEGATED :last-of-type pseudo-class
     */
    public void test80() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-80");
    }

    /**
     * NEGATED :only-child pseudo-class
     */
    public void test81() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-81");
    }

    /**
     * NEGATED :only-child pseudo-class
     */
    public void test81b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-81b");
    }

    /**
     * NEGATED :only-of-type pseudo-class
     */
    public void test82() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-82");
    }

    /**
     * NEGATED :only-of-type pseudo-class
     */
    public void test82b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-82b");
    }

    /**
     * Negation pseudo-class cannot be an argument of itself
     */
    public void test83() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-83", 1);
    }

    /**
     * Nondeterministic matching of descendant and child combinators
     */
    public void test86() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-86");
    }

    /**
     * Nondeterministic matching of direct and indirect adjacent combinators
     */
    public void test87() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-87");
    }

    /**
     * Nondeterministic matching of direct and indirect adjacent combinators
     */
    public void test87b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-87b");
    }

    /**
     * Nondeterministic matching of descendant and direct adjacent combinators
     */
    public void test88() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-88");
    }

    /**
     * Nondeterministic matching of descendant and direct adjacent combinators
     */
    public void test88b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-88b");
    }

    /**
     * Simple combination of descendant and child combinators
     */
    public void test89() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-89");
    }

    /**
     * Simple combination of direct and indirect adjacent combinators
     */
    public void test90() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-90");
    }

    /**
     * Simple combination of direct and indirect adjacent combinators
     */
    public void test90b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-90b");
    }

    /**
     * Type element selector with declared namespace
     */
    public void test91() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-91");
    }

    /**
     * Type element selector with universal namespace
     */
    public void test92() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-92");
    }

    /**
     * Type element selector without declared namespace
     */
    public void test93() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-93");
    }

    /**
     * Universal selector with declared namespace
     */
    public void test94() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-94");
    }

    /**
     * Universal selector with declared namespace
     */
    public void test94b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-94b");
    }

    /**
     * Universal selector with universal namespace
     */
    public void test95() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-95");
    }

    /**
     * Universal selector without declared namespace
     */
    public void test96() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-96");
    }

    /**
     * Universal selector without declared namespace
     */
    public void test96b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-96b");
    }

    /**
     * Attribute existence selector with declared namespace
     */
    public void test97() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-97");
    }

    /**
     * Attribute existence selector with declared namespace
     */
    public void test97b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-97b");
    }

    /**
     * Attribute value selector with declared namespace
     */
    public void test98() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-98");
    }

    /**
     * Attribute value selector with declared namespace
     */
    public void test98b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-98b");
    }

    /**
     * Attribute space-separated value selector with declared namespace
     */
    public void test99() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-99");
    }

    /**
     * Attribute space-separated value selector with declared namespace
     */
    public void test99b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-99b");
    }

    /**
     * Attribute dash-separated value selector with declared namespace
     */
    public void test100() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-100");
    }

    /**
     * Attribute dash-separated value selector with declared namespace
     */
    public void test100b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-100b");
    }

    /**
     * Substring matching attribute value selector on beginning with declared
     * namespace
     */
    public void test101() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-101");
    }

    /**
     * Substring matching attribute value selector on beginning with declared
     * namespace
     */
    public void test101b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-101b");
    }

    /**
     * Substring matching attribute value selector on end with declared
     * namespace
     */
    public void test102() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-102");
    }

    /**
     * Substring matching attribute value selector on end with declared
     * namespace
     */
    public void test102b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-102b");
    }

    /**
     * Substring matching attribute value selector on middle with declared
     * namespace
     */
    public void test103() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-103");
    }

    /**
     * Substring matching attribute value selector on middle with declared
     * namespace
     */
    public void test103b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-103b");
    }

    /**
     * Attribute existence selector with universal namespace
     */
    public void test104() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-104");
    }

    /**
     * Attribute existence selector with universal namespace
     */
    public void test104b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-104b");
    }

    /**
     * Attribute value selector with universal namespace
     */
    public void test105() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-105");
    }

    /**
     * Attribute value selector with universal namespace
     */
    public void test105b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-105b");
    }

    /**
     * Attribute space-separated value selector with universal namespace
     */
    public void test106() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-106");
    }

    /**
     * Attribute space-separated value selector with universal namespace
     */
    public void test106b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-106b");
    }

    /**
     * Attribute dash-separated value selector with universal namespace
     */
    public void test107() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-107");
    }

    /**
     * Attribute dash-separated value selector with universal namespace
     */
    public void test107b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-107b");
    }

    /**
     * Substring matching attribute selector on beginning with universal
     * namespace
     */
    public void test108() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-108");
    }

    /**
     * Substring matching attribute selector on beginning with universal
     * namespace
     */
    public void test108b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-108b");
    }

    /**
     * Substring matching attribute selector on end with universal namespace
     */
    public void test109() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-109");
    }

    /**
     * Substring matching attribute selector on end with universal namespace
     */
    public void test109b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-109b");
    }

    /**
     * Substring matching attribute selector on middle with universal namespace
     */
    public void test110() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-110");
    }

    /**
     * Substring matching attribute selector on middle with universal namespace
     */
    public void test110b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-110b");
    }

    /**
     * Attribute existence selector without declared namespace
     */
    public void test111() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-111");
    }

    /**
     * Attribute existence selector without declared namespace
     */
    public void test111b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-111b");
    }

    /**
     * Attribute value selector without declared namespace
     */

    public void test112() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-112");
    }

    /**
     * Attribute value selector without declared namespace
     */
    public void test112b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-112b");
    }

    /**
     * Attribute space-separated value selector without declared namespace
     */
    public void test113() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-113");
    }

    /**
     * Attribute space-separated value selector without declared namespace
     */
    public void test113b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-113b");
    }

    /**
     * Attribute dash-separated value selector without declared namespace
     */
    public void test114() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-114");
    }

    /**
     * Attribute dash-separated value selector without declared namespace
     */
    public void test114b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-114b");
    }

    /**
     * Substring matching attribute selector on beginning without declared
     * namespace
     */
    public void test115() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-115");
    }

    /**
     * Substring matching attribute selector on beginning without declared
     * namespace
     */
    public void test115b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-115b");
    }

    /**
     * Substring matching attribute selector on end without declared namespace
     */
    public void test116() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-116");
    }

    /**
     * Substring matching attribute selector on end without declared namespace
     */
    public void test116b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-116b");
    }

    /**
     * Substring matching attribute selector on middle without declared
     * namespace
     */
    public void test117() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-117");
    }

    /**
     * Substring matching attribute selector on middle without declared
     * namespace
     */
    public void test117b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-117b");
    }

    /**
     * NEGATED type element selector with declared namespace
     */
    public void test118() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-118");
    }

    /**
     * NEGATED type element selector with universal namespace
     */
    public void test119() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-119");
    }

    /**
     * NEGATED type element selector without declared namespace
     */
    public void test120() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-120");
    }

    /**
     * NEGATED universal selector with declared namespace
     */
    public void test121() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-121");
    }

    /**
     * NEGATED universal selector with universal namespace
     */
    public void test122() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-122");
    }

    /**
     * NEGATED universal selector with declared namespace
     */
    public void test123() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-123");
    }

    /**
     * NEGATED universal selector with declared namespace
     */
    public void test123b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-123b");
    }

    /**
     * NEGATED Attribute value selector with declared namespace
     */
    public void test124() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-124");
    }

    /**
     * NEGATED Attribute value selector with declared namespace
     */
    public void test124b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-124b");
    }

    /**
     * NEGATED Attribute space-separated value selector with declared namespace
     */
    public void test125() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-125");
    }

    /**
     * NEGATED Attribute space-separated value selector with declared namespace
     */
    public void test125b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-125b");
    }

    /**
     * NEGATED Attribute dash-separated value selector with declared namespace
     */
    public void test126() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-126");
    }

    /**
     * NEGATED Attribute dash-separated value selector with declared namespace
     */
    public void test126b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-126b");
    }

    /**
     * NEGATED Substring matching attribute value selector on beginning with
     * declared namespace
     */
    public void test127() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-127");
    }

    /**
     * NEGATED Substring matching attribute value selector on beginning with
     * declared namespace
     */
    public void test127b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-127b");
    }

    /**
     * NEGATED Substring matching attribute value selector on end with declared
     * namespace
     */
    public void test128() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-128");
    }

    /**
     * NEGATED Substring matching attribute value selector on end with declared
     * namespace
     */
    public void test128b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-128b");
    }

    /**
     * NEGATED Substring matching attribute value selector on middle with
     * declared namespace
     */
    public void test129() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-129");
    }

    /**
     * NEGATED Substring matching attribute value selector on middle with
     * declared namespace
     */
    public void test129b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-129b");
    }

    /**
     * NEGATED Attribute existence selector with universal namespace
     */
    public void test130() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-130");
    }

    /**
     * NEGATED Attribute existence selector with universal namespace
     */
    public void test130b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-130b");
    }

    /**
     * NEGATED Attribute value selector with universal namespace
     */
    public void test131() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-131");
    }

    /**
     * NEGATED Attribute value selector with universal namespace
     */
    public void test131b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-131b");
    }

    /**
     * NEGATED Attribute space-separated value selector with universal namespace
     */
    public void test132() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-132");
    }

    /**
     * NEGATED Attribute space-separated value selector with universal namespace
     */
    public void test132b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-132b");
    }

    /**
     * NEGATED Attribute dash-separated value selector with universal namespace
     */
    public void test133() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-133");
    }

    /**
     * NEGATED Attribute dash-separated value selector with universal namespace
     */
    public void test133b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-133b");
    }

    /**
     * NEGATED Substring matching attribute selector on beginning with universal
     * namespace
     */
    public void test134() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-134");
    }

    /**
     * NEGATED Substring matching attribute selector on beginning with universal
     * namespace
     */
    public void test134b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-134b");
    }

    /**
     * NEGATED Substring matching attribute selector on end with universal
     * namespace
     */
    public void test135() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-135");
    }

    /**
     * NEGATED Substring matching attribute selector on end with universal
     * namespace
     */
    public void test135b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-135b");
    }

    /**
     * NEGATED Substring matching attribute selector on middle with universal
     * namespace
     */
    public void test136() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-136");
    }

    /**
     * NEGATED Substring matching attribute selector on middle with universal
     * namespace
     */
    public void test136b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-136b");
    }

    /**
     * NEGATED Attribute existence selector without declared namespace
     */
    public void test137() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-137");
    }

    /**
     * NEGATED Attribute existence selector without declared namespace
     */
    public void test137b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-137b");
    }

    /**
     * NEGATED Attribute value selector without declared namespace
     */
    public void test138() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-138");
    }

    /**
     * NEGATED Attribute value selector without declared namespace
     */
    public void test138b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-138b");
    }

    /**
     * NEGATED Attribute space-separated value selector without declared
     * namespace
     */
    public void test139() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-139");
    }

    /**
     * NEGATED Attribute space-separated value selector without declared
     * namespace
     */
    public void test139b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-139b");
    }

    /**
     * NEGATED Attribute dash-separated value selector without declared
     * namespace
     */
    public void test140() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-140");
    }

    /**
     * NEGATED Attribute dash-separated value selector without declared
     * namespace
     */
    public void test140b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-140b");
    }

    /**
     * NEGATED Substring matching attribute selector on beginning without
     * declared namespace
     */
    public void test141() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-141");
    }

    /**
     * NEGATED Substring matching attribute selector on beginning without
     * declared namespace
     */
    public void test141b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-141b");
    }

    /**
     * NEGATED Substring matching attribute selector on end without declared
     * namespace
     */
    public void test142() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-142");
    }

    /**
     * NEGATED Substring matching attribute selector on end without declared
     * namespace
     */
    public void test142b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-142b");
    }

    /**
     * NEGATED Substring matching attribute selector on middle without declared
     * namespace
     */
    public void test143() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-143");
    }

    /**
     * NEGATED Substring matching attribute selector on middle without declared
     * namespace
     */
    public void test143b() throws Exception {
        //
        assertStyleForFilename("css3-modsel-143b");
    }

    /**
     * NEGATED :enabled:disabled pseudo-classes
     */
    /*
     * public void test144() throws Exception {
     * assertStyleForFilename("css3-modsel-144");
     * //STFail(@":enabled pseudo-class not implemented");
     * //STFail(@":disabled pseudo-class not implemented"); }
     */

    /**
     * :nth-of-type() pseudo-class with hidden elements
     */
    public void test145a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-145a");
    }

    /**
     * :nth-of-type() pseudo-class with hidden elements
     */
    public void test145b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-145b");
    }

    /**
     * :nth-child() pseudo-class with hidden elements
     */
    public void test146a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-146a");
    }

    /**
     * :nth-child() pseudo-class with hidden elements
     */
    public void test146b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-146b");
    }

    /**
     * :nth-last-of-type() pseudo-class with collapsed elements
     */
    public void test147a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-147a");
    }

    /**
     * :nth-last-of-type() pseudo-class with collapsed elements
     */
    public void test147b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-147b");
    }

    /**
     * :empty pseudo-class and text
     */
    public void test148() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-148");
    }

    /**
     * :empty pseudo-class and empty elements
     */
    public void test149() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-149");
    }

    /**
     * :empty pseudo-class and empty elements
     */
    public void test149b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-149b");
    }

    /**
     * :empty pseudo-class and XML/SGML constructs
     */
    public void test150() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-150");
    }

    /**
     * :empty pseudo-class and whitespace
     */
    public void test151() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-151");
    }

    /**
     * :empty pseudo-class and elements
     */
    public void test152() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-152");
    }

    /**
     * :empty pseudo-class and CDATA
     */
    public void test153() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-153");
    }

    /**
     * Syntax and parsing
     */
    public void test154() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-154", 1);
    }

    /**
     * Syntax and parsing
     */
    public void test155() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-155", 1);
    }

    /**
     * Syntax and parsing
     */
    public void test155a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-155a", 1);
    }

    /**
     * Syntax and parsing
     */
    public void test155b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-155b");
    }

    /**
     * Syntax and parsing
     */
    public void test155c() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-155c");
    }

    /**
     * Syntax and parsing
     */
    public void test155d() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-155d");
    }

    /**
     * Syntax and parsing
     */
    public void test156() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-156", 1);
    }

    /**
     * Syntax and parsing
     */
    public void test156b() throws Exception {
        assertStyleForFilename("css3-modsel-156b", 1);
    }

    /**
     * Syntax and parsing
     */
    public void test156c() throws Exception {
        assertStyleForFilename("css3-modsel-156c", 1);
    }

    /**
     * Syntax and parsing
     */
    public void test157() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-157", 1);
    }

    /**
     * Syntax and parsing
     */
    public void test158() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-158", 1);
    }

    /**
     * Syntax and parsing of new pseudo-elements
     */
    /*
     * public void test159() throws Exception {
     * assertStyleForFilename("css3-modsel-159");
     * //STFail(@"::selection pseudo-element not implemented"); }
     */

    /**
     * Syntax and parsing of unknown pseudo-classes
     */
    public void test160() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-160");
    }

    /**
     * Syntax and parsing of unknown pseudo-classes and pseudo-elements
     */
    public void test161() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-161");
    }

    /**
     * :first-letter with ::first-letter
     */
    /*
     * public void test166() throws Exception {
     * assertStyleForFilename("css3-modsel-166");
     * //STFail(@"::first-letter pseudo-element not implemented"); }
     */

    /**
     * :first-letter with ::first-letter
     */
    /*
     * public void test166a() throws Exception {
     * assertStyleForFilename("css3-modsel-166a");
     * //STFail(@"::first-letter pseudo-element not implemented"); }
     */

    /**
     * :first-line with ::first-line
     */
    /*
     * public void test167() throws Exception {
     * assertStyleForFilename("css3-modsel-167");
     * //STFail(@"::first-letter pseudo-element not implemented"); }
     */

    /**
     * :first-line with ::first-line
     */
    /*
     * public void test167a() throws Exception {
     * assertStyleForFilename("css3-modsel-167a");
     * //STFail(@"::first-letter pseudo-element not implemented"); }
     */

    /**
     * :before with ::before
     */
    /*
     * public void test168() throws Exception {
     * assertStyleForFilename("css3-modsel-168");
     * //STFail(@"::before pseudo-element not implemented"); }
     */

    /**
     * :before with ::before
     */
    /*
     * public void test168a() throws Exception {
     * assertStyleForFilename("css3-modsel-168a");
     * //STFail(@"::before pseudo-element not implemented"); }
     */

    /**
     * :after with ::after
     */
    /*
     * public void test169() throws Exception {
     * assertStyleForFilename("css3-modsel-169");
     * //STFail(@"::after pseudo-element not implemented"); }
     */

    /**
     * :after with ::after
     */
    /*
     * public void test169a() throws Exception {
     * assertStyleForFilename("css3-modsel-169a");
     * //STFail(@"::after pseudo-element not implemented"); }
     */

    /**
     * Long chains of selectors
     */
    public void test170() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-170");
    }

    /**
     * Long chains of selectors
     */
    public void test170a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-170a");
    }

    /**
     * Long chains of selectors
     */
    public void test170b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-170b");
    }

    /**
     * Long chains of selectors
     */
    public void test170c() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-170c");
    }

    /**
     * Long chains of selectors
     */
    public void test170d() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-170d");
    }

    /**
     * Classes: XHTML global class attribute
     */
    /*
     * public void test171() throws Exception {
     * assertStyleForFilename("css3-modsel-171");
     * //STFail(@"xhtml:class attribute not supported"); }
     */

    /**
     * Namespaced attribute selectors
     */
    public void test172a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-172a");
    }

    /**
     * Namespaced attribute selectors
     */
    public void test172b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-172b");
    }

    /**
     * Namespaced attribute selectors
     */
    public void test173a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-173a");
    }

    /**
     * Namespaced attribute selectors
     */
    public void test173b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-173b");
    }

    /**
     * Attribute selectors with multiple attributes
     */
    /*
     * public void test174a() throws Exception {
     * assertStyleForFilename("css3-modsel-174a");
     * //STFail(@"DOMElement doesn't support same named attributes"); }
     */

    /**
     * NEGATED Attribute selectors with multiple attributes
     */
    /*
     * public void test174b() throws Exception {
     * assertStyleForFilename("css3-modsel-174b");
     * //STFail(@"DOMElement doesn't support same named attributes"); }
     */

    /**
     * Parsing: Numbers in classes
     */
    public void test175a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-175a", 1);
    }

    /**
     * Parsing: Numbers in classes
     */
    public void test175b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-175b", 1);
    }

    /**
     * Parsing: Numbers in classes
     */
    /*
     * public void test175c() throws Exception {
     * assertStyleForFilename("css3-modsel-175c");
     * //STFail(@"unimplemented escape sequence?"); }
     */

    /**
     * Combinations: classes and IDs
     */
    public void test176() throws Exception {
        assertStyleForFilename("css3-modsel-176");
    }

    /**
     * Parsing : vs ::
     */
    /*
     * public void test177a() throws Exception {
     * assertStyleForFilename("css3-modsel-177a");
     * //STFail(@"::selection pseudo-element not implemented"); }
     */

    /**
     * Parsing : vs ::
     */
    /*
     * public void test177b() throws Exception {
     * assertStyleForFilename("css3-modsel-177b");
     * //STFail(@"::first-child pseudo-element not implemented"); }
     */

    /**
     * Parsing: :not and pseudo-elements
     */
    /*
     * public void test178() throws Exception {
     * assertStyleForFilename("css3-modsel-178");
     * //STFail(@"::after pseudo-element not implemented");
     * //STFail(@"::first-line pseudo-element not implemented"); }
     */

    /**
     * ::first-line on inlines
     */
    /*
     * public void test179() throws Exception {
     * assertStyleForFilename("css3-modsel-179");
     * //STFail(@"::first-line pseudo-element not implemented"); }
     */

    /**
     * ::first-line after <br>
     */
    /*
     * public void test179a() throws Exception {
     * assertStyleForFilename("css3-modsel-179a");
     * //STFail(@"::first-line pseudo-element not implemented"); }
     */

    /**
     * ::first-letter after <br>
     */
    /*
     * public void test180a() throws Exception {
     * assertStyleForFilename("css3-modsel-180a");
     * //STFail(@"::first-letter pseudo-element not implemented"); }
     */

    /**
     * Case sensitivity
     */
    /*
     * public void test181() throws Exception {
     * assertStyleForFilename("css3-modsel-181");
     * //STFail(@"case-sensitivity failure with classes?"); }
     */

    /**
     * Namespaces and \: in selectors
     */
    /*
     * public void test182() throws Exception {
     * assertStyleForFilename("css3-modsel-182"); //STFail(@"possible parse
     * failure with foo\:bar matching '<foo:bar...>"); }
     */

    /**
     * Syntax and parsing of class selectors
     */
    /*
     * public void test183() throws Exception { // verified
     * assertStyleForFilename("css3-modsel-183" ,3);
     * //STFail(@"possible parse failure"); }
     */

    /**
     * Ends-with attribute selector with empty value
     */
    public void test184a() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-184a");
    }

    /**
     * Starts-with attribute selector with empty value
     */
    public void test184b() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-184b");
    }

    /**
     * Contains attribute selector with empty value
     */
    public void test184c() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-184c");
    }

    /**
     * NEGATED ends-with attribute selector with empty value
     */
    public void test184d() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-184d");
    }

    /**
     * NEGATED starts-with attribute selector with empty value
     */
    public void test184e() throws Exception {
        // verified
        assertStyleForFilename("css3-modsel-184e");
    }

    /**
     * NEGATED contains attribute selector with empty value
     */
    public void test184f() throws Exception {
        assertStyleForFilename("css3-modsel-184f");
    }

    /**
     * NEGATED Dynamic handling of :empty
     */
    /*
     * public void testD1() throws Exception {
     * assertStyleForFilename("css3-modsel-d1"); //STFail(@"uses javascript"); }
     */

    /**
     * Dynamic handling of :empty
     */
    /*
     * public void testD1b() throws Exception {
     * assertStyleForFilename("css3-modsel-d1b"); //STFail(@"uses javascript");
     * }
     */

    /**
     * Dynamic handling of combinators
     */
    /*
     * public void testD2() throws Exception {
     * assertStyleForFilename("css3-modsel-d2"); //STFail(@"uses javascript"); }
     */

    /**
     * Dynamic handling of attribute selectors
     */
    /*
     * public void testD3() throws Exception {
     * assertStyleForFilename("css3-modsel-d3"); //STFail(@"uses javascript"); }
     */

    /**
     * Dynamic updating of :first-child and :last-child
     */
    /*
     * public void testD4() throws Exception {
     * assertStyleForFilename("css3-modsel-d4"); //STFail(@"uses javascript"); }
     */
}
