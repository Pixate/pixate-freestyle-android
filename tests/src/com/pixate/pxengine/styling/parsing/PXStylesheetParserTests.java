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
package com.pixate.pxengine.styling.parsing;

import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.pixate.pxengine.cg.paints.PXLinearGradient;
import com.pixate.pxengine.cg.paints.PXPaint;
import com.pixate.pxengine.styling.PXDeclaration;
import com.pixate.pxengine.styling.PXRuleSet;
import com.pixate.pxengine.styling.PXStylesheet;
import com.pixate.pxengine.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.pxengine.styling.animation.PXKeyframe;
import com.pixate.pxengine.styling.animation.PXKeyframeBlock;
import com.pixate.pxengine.styling.media.PXMediaExpression;
import com.pixate.pxengine.styling.media.PXMediaExpressionGroup;
import com.pixate.pxengine.styling.media.PXMediaGroup;
import com.pixate.pxengine.styling.media.PXNamedMediaExpression;
import com.pixate.pxengine.styling.selectors.PXSelector;
import com.pixate.util.CollectionUtil;
import com.pixate.util.ObjectUtil;

/**
 * {@link PXStylesheetParser} tests.
 */
public class PXStylesheetParserTests extends AndroidTestCase {

    private PXStylesheetParser parser;

    // TODO: Write assertStyleSheet method to remove code duplication in these
    // tests

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        parser = new PXStylesheetParser();
        parser.setContext(getContext());
    }

    public void testSimpleDeclaration() {
        String source = "button { abc: def }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse errors encountered", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set", ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXDeclaration> declarations = ruleSet.getDeclarations();
        assertTrue("Expected a single declaration", declarations.size() == 1);

        PXDeclaration declaration = declarations.get(0);
        assertEquals("Expected 'abc' declaration", "abc", declaration.getName());
    }

    public void testSimpleDeclarations() {
        String source = "button { abc: def; ghi: jkl }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse errors encountered", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set", ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXDeclaration> declarations = ruleSet.getDeclarations();
        assertTrue("Expected two declarations", declarations.size() == 2);

        PXDeclaration declaration = declarations.get(0);
        assertEquals("Expected 'abc' declaration", "abc", declaration.getName());

        declaration = declarations.get(1);
        assertEquals("Expected 'abc' declaration", "ghi", declaration.getName());
    }

    public void testSimpleDeclarationsMissingSemicolon() {
        String source = "button { abc: def ghi: jkl }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse errors encountered", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set", ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXDeclaration> declarations = ruleSet.getDeclarations();
        assertTrue("Expected two declarations", declarations.size() == 2);

        PXDeclaration declaration = declarations.get(0);
        assertEquals("Expected 'abc' declaration", "abc", declaration.getName());

        declaration = declarations.get(1);
        assertEquals("Expected 'abc' declaration", "ghi", declaration.getName());
    }

    public void testMissingSelector() {
        String source = "{ abc: def }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Expected one parse error", !CollectionUtil.isEmpty(errors)
                && errors.size() == 1);

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set",
                !CollectionUtil.isEmpty(ruleSets) && ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXDeclaration> declarations = ruleSet.getDeclarations();
        assertTrue("Expected one declaration", declarations.size() == 1);

        PXDeclaration declaration = declarations.get(0);
        assertEquals("Expected 'abc' declaration", "abc", declaration.getName());
    }

    public void testBadDeclarationName() {
        String source = "button { 10: def; ghi: jkl }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Expected one parse error", !CollectionUtil.isEmpty(errors)
                && errors.size() == 1);

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set",
                !CollectionUtil.isEmpty(ruleSets) && ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXDeclaration> declarations = ruleSet.getDeclarations();
        assertTrue("Expected one declaration", declarations.size() == 1);

        PXDeclaration declaration = declarations.get(0);
        assertEquals("Expected 'ghi' declaration", "ghi", declaration.getName());
    }

    public void testColonInValue() {
        String source = "button { abc: :; }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set",
                !CollectionUtil.isEmpty(ruleSets) && ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXDeclaration> declarations = ruleSet.getDeclarations();
        assertTrue("Expected one declaration", declarations.size() == 1);

        PXDeclaration declaration = declarations.get(0);
        assertEquals("Expected 'ghi' declaration", "abc", declaration.getName());
    }

    // Selector Tests

    public void testTypeSelector() {
        String source = "button {}";
        String expected = "(button)";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set", ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXSelector> selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);

        PXSelector selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertNotNull("Unexpected null selector", selector);

        assertEquals(String.format("Selector trees do not match:\nexpected = %s\nactual = %s",
                expected, selector.getSource()), expected, selector.getSource());
    }

    public void testAdjacentSiblingCombinator() {
        String source = "view + button {}";
        String expected = "(ADJACENT_SIBLING_COMBINATOR\n" + "  (view)\n" + "  (button))";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set",
                !CollectionUtil.isEmpty(ruleSets) && ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXSelector> selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);

        PXSelector selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertNotNull("Unexpected null selector", selector);

        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected, selector.getSource()), expected, selector.getSource());
    }

    public void testSiblingCombinator() {
        String source = "view ~ button {}";
        String expected = "(GENERAL_SIBLING_COMBINATOR\n" + "  (view)\n" + "  (button))";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set", ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXSelector> selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);

        PXSelector selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertNotNull("Unexpected null selector", selector);

        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected, selector.getSource()), expected, selector.getSource());
    }

    public void testChildCombinator() {
        String source = "view > button {}";
        String expected = "(CHILD_COMBINATOR\n" + "  (view)\n" + "  (button))";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set",
                !CollectionUtil.isEmpty(ruleSets) && ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXSelector> selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);

        PXSelector selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertNotNull("Unexpected null selector", selector);

        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected, selector.getSource()), expected, selector.getSource());
    }

    public void testDescendantCombinator() {
        String source = "view button {}";
        String expected = "(DESCENDANT_COMBINATOR\n" + "  (view)\n" + "  (button))";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set", ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXSelector> selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);

        PXSelector selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertNotNull("Unexpected null selector", selector);

        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected, selector.getSource()), expected, selector.getSource());
    }

    public void testDescendantCombinator2() {
        String source = "view button label {}";
        String expected = "(DESCENDANT_COMBINATOR\n" + "  (DESCENDANT_COMBINATOR\n"
                + "    (view)\n" + "    (button))\n" + "  (label))";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set", ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXSelector> selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);

        PXSelector selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertNotNull("Unexpected null selector", selector);

        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected, selector.getSource()), expected, selector.getSource());
    }

    public void testDescendantCombinator3() {
        String source = ".t1 :only-of-type {}";
        String expected =
            "(DESCENDANT_COMBINATOR\n" +
            "  (*\n" +
            "    (CLASS t1))\n" +
            "  (*\n" +
            "    (PSEUDO_CLASS_PREDICATE :only-of-type)))";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected a single rule set", ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXSelector> selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);

        PXSelector selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertNotNull("Unexpected null selector", selector);

        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected, selector.getSource()), expected, selector.getSource());
    }

    public void testSelectorSequence() {
        String source = "button, slider {}";
        String expected1 = "(button)";
        String expected2 = "(slider)";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected two rule sets", ruleSets.size() == 2);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXSelector> selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);
        PXSelector selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertNotNull("Unexpected null selector", selector);
        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected1, selector.getSource()), expected1, selector.getSource());

        ruleSet = ruleSets.get(1);
        selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);
        selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected2, selector.getSource()), expected2, selector.getSource());
    }

    public void testPseudoElement() {
        // NOTE: we don't place psuedo-elements in the tree yet
        String source = "button::before {}";
        String expected = "(button\n" + "  (PSEUDO_ELEMENT before))";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected one rule set", ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXSelector> selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);
        PXSelector selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertNotNull("Unexpected null selector", selector);
        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected, selector.getSource()), expected, selector.getSource());
    }

    public void testPseudoElements() {
        // NOTE: we don't place psuedo-elements in the tree yet
        String source = "button::before, slider::before {}";
        String expected1 = "(button\n" + "  (PSEUDO_ELEMENT before))";
        String expected2 = "(slider\n" + "  (PSEUDO_ELEMENT before))";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected two rule sets", ruleSets.size() == 2);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXSelector> selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);
        PXSelector selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertNotNull("Unexpected null selector", selector);
        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected1, selector.getSource()), expected1, selector.getSource());

        ruleSet = ruleSets.get(1);
        selectors = ruleSet.getSelectors();
        assertTrue("Expected one selector", selectors.size() == 1);
        selector = CollectionUtil.isEmpty(selectors) ? null : selectors.get(0);
        assertEquals(String.format("Selector trees do not match:\nexpected = \n%s\nactual = \n%s",
                expected2, selector.getSource()), expected2, selector.getSource());
    }

    // At-keyword Tests

    public void testDefaultNamespace() {
        String url = "http://www.pixate.com";
        String source = String.format("@namespace \"%s\";", url);

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        String defaultNamespace = stylesheet.getNamespaceForPrefix(null);
        assertTrue(String.format("Default namespace, '%s', does not equal '%s'", defaultNamespace,
                url), ObjectUtil.areEqual(url, defaultNamespace));
    }

    public void testNamespacePrefix() {
        String prefix = "px";
        String url = "http://www.pixate.com";
        String source = String.format("@namespace %s \"%s\";", prefix, url);

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        String prefixNamespace = stylesheet.getNamespaceForPrefix(prefix);
        assertTrue(String.format("'%s' prefix namespace, '%s', does not equal '%s'", prefix,
                prefixNamespace, url), ObjectUtil.areEqual(url, prefixNamespace));
    }

    // Failures from W3C Selector tests

    public void testNot() {
        String source = "div.test *:not(a|p) { background-color : lime }";

        parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue(
                String.format("Found %d unexpected parse error(s): %s",
                        CollectionUtil.isEmpty(errors) ? 0 : errors.size(),
                        CollectionUtil.toString(errors, "\n")), CollectionUtil.isEmpty(errors));
    }

    public void testNthOfType() {
        String source = "line:nth-of-type(odd) { background: lime; }";

        parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue(
                String.format("Found %d unexpected parse error(s): %s",
                        CollectionUtil.isEmpty(errors) ? 0 : errors.size(),
                        CollectionUtil.toString(errors, "\n")), CollectionUtil.isEmpty(errors));
    }

    public void testNthChildWithoutSpaces() {
        String source = "line:nth-child(2n+1) { background: red; }";

        parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue(
                String.format("Found %d unexpected parse error(s): %s",
                        CollectionUtil.isEmpty(errors) ? 0 : errors.size(),
                        CollectionUtil.toString(errors, "\n")), CollectionUtil.isEmpty(errors));
    }

    public void testNthChildWithoutSpaces2() {
        String source = "line:nth-child(2n-1) { background: red; }";

        parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue(
                String.format("Found %d unexpected parse error(s): %s",
                        CollectionUtil.isEmpty(errors) ? 0 : errors.size(),
                        CollectionUtil.toString(errors, "\n")), CollectionUtil.isEmpty(errors));
    }

    public void testNthChildWithSpaces() {
        String source = "line:nth-child(2n + 1) { background: red; }";

        parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue(
                String.format("Found %d unexpected parse error(s): %s",
                        CollectionUtil.isEmpty(errors) ? 0 : errors.size(),
                        CollectionUtil.toString(errors, "\n")), CollectionUtil.isEmpty(errors));
    }

    public void testNthLastChild() {
        String source = "line:nth-last-child(n) { background: red; }";

        parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue(
                String.format("Found %d unexpected parse error(s): %s",
                        CollectionUtil.isEmpty(errors) ? 0 : errors.size(),
                        CollectionUtil.toString(errors, "\n")), CollectionUtil.isEmpty(errors));
    }

    public void testNthLastOfType() {
        String source = "line:nth-last-of-type(1) { background: red; }";

        parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue(
                String.format("Found %d unexpected parse error(s): %s",
                        CollectionUtil.isEmpty(errors) ? 0 : errors.size(),
                        CollectionUtil.toString(errors, "\n")), CollectionUtil.isEmpty(errors));
    }

    public void testImportant() {
        String source = "* { background: red !important; }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected two rule sets", ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        List<PXDeclaration> declarations = ruleSet.getDeclarations();
        assertTrue("Expected one declaration", declarations.size() == 1);
        PXDeclaration declaration = declarations.get(0);
        assertTrue("declaration should be marked as important", declaration.isImportant());

        String stringValue = declaration.getStringValue();
        String expectedValue = "red";
        assertTrue(String.format("declaration string value does not match: '%s' != '%s'",
                stringValue, expectedValue), ObjectUtil.areEqual(stringValue, expectedValue));
    }

    public void testImportWithString() {
        String source = "@import \"test.css\";";

        parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue(
                String.format("Found %d unexpected parse error(s): %s",
                        CollectionUtil.isEmpty(errors) ? 0 : errors.size(),
                        CollectionUtil.toString(errors, "\n")), CollectionUtil.isEmpty(errors));
    }

    public void testImportWithURL() {
        String source = "@import url(test.css);";

        parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue(
                String.format("Found %d unexpected parse error(s): %s",
                        CollectionUtil.isEmpty(errors) ? 0 : errors.size(),
                        CollectionUtil.toString(errors, "\n")), CollectionUtil.isEmpty(errors));
    }

    // Keyframes Tests

    public void testEmptyKeyframes() {
        String source = "@keyframes test {}";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        PXKeyframe keyframe = stylesheet.getKeyframeForName("test");
        assertNotNull("Expected a 'test' keyframe to be defined in the stylesheet", keyframe);

        List<PXKeyframeBlock> blocks = keyframe.getBlocks();
        assertTrue(String.format("Expected no keyframe blocks but found %d", blocks == null ? 0
                : blocks.size()), CollectionUtil.isEmpty(blocks));
    }

    public void testKeyframesWithTo() {
        String source = "@keyframes test { to { color: blue; } }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        PXKeyframe keyframe = stylesheet.getKeyframeForName("test");
        assertNotNull("Expected a 'test' keyframe to be defined in the stylesheet", keyframe);

        List<PXKeyframeBlock> blocks = keyframe.getBlocks();
        assertTrue(
                String.format("Expected 1 keyframe block but found %d",
                        blocks == null ? 0 : blocks.size()), !CollectionUtil.isEmpty(blocks)
                        && blocks.size() == 1);

        PXKeyframeBlock block = blocks.get(0);
        assertTrue(String.format("Expected offset to be 1.0, but found %s", block.getOffset()),
                block.getOffset() == 1.0f);
        assertTrue(String.format("Expected 1 declaration but found %d", block.getDeclarations()
                .size()), block.getDeclarations().size() == 1);
    }

    public void testKeyframesWithFrom() {
        String source = "@keyframes test { from { color: blue; } }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        PXKeyframe keyframe = stylesheet.getKeyframeForName("test");
        assertNotNull("Expected a 'test' keyframe to be defined in the stylesheet", keyframe);

        List<PXKeyframeBlock> blocks = keyframe.getBlocks();
        assertTrue(
                String.format("Expected 1 keyframe block but found %d",
                        blocks == null ? 0 : blocks.size()), !CollectionUtil.isEmpty(blocks)
                        && blocks.size() == 1);
        PXKeyframeBlock block = blocks.get(0);
        assertTrue(String.format("Expected offset to be 0.0, but found %s", block.getOffset()),
                block.getOffset() == 0.0f);
        assertTrue(String.format("Expected 1 declaration but found %d", block.getDeclarations()
                .size()), block.getDeclarations().size() == 1);
    }

    public void testKeyframesWithPercentage() {
        String source = "@keyframes test { 50% { color: blue; } }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        PXKeyframe keyframe = stylesheet.getKeyframeForName("test");
        assertNotNull("Expected a 'test' keyframe to be defined in the stylesheet", keyframe);

        List<PXKeyframeBlock> blocks = keyframe.getBlocks();
        assertTrue(
                String.format("Expected 1 keyframe block but found %d",
                        blocks == null ? 0 : blocks.size()), !CollectionUtil.isEmpty(blocks)
                        && blocks.size() == 1);

        PXKeyframeBlock block = blocks.get(0);
        assertTrue(String.format("Expected offset to be 0.5, but found %s", block.getOffset()),
                block.getOffset() == 0.5f);
        assertTrue(String.format("Expected 1 declaration but found %d", block.getDeclarations()
                .size()), block.getDeclarations().size() == 1);
    }

    public void testKeyframesWithMultipleBlocks() {
        String source = "@keyframes test { from { color: red; } 50% { color: blue; } to { color: green; } }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        PXKeyframe keyframe = stylesheet.getKeyframeForName("test");
        assertNotNull("Expected a 'test' keyframe to be defined in the stylesheet", keyframe);

        List<PXKeyframeBlock> blocks = keyframe.getBlocks();
        assertTrue(
                String.format("Expected 3 keyframe block but found %d",
                        blocks == null ? 0 : blocks.size()), !CollectionUtil.isEmpty(blocks)
                        && blocks.size() == 3);

        PXKeyframeBlock block = blocks.get(0);
        assertTrue(String.format("Expected offset to be 0.0, but found %s", block.getOffset()),
                block.getOffset() == 0.0f);
        assertTrue(String.format("Expected 1 declaration but found %d", block.getDeclarations()
                .size()), block.getDeclarations().size() == 1);

        block = blocks.get(1);
        assertTrue(String.format("Expected offset to be 0.5, but found %s", block.getOffset()),
                block.getOffset() == 0.5f);
        assertTrue(String.format("Expected 1 declaration but found %d", block.getDeclarations()
                .size()), block.getDeclarations().size() == 1);

        block = blocks.get(2);
        assertTrue(String.format("Expected offset to be 1.0, but found %s", block.getOffset()),
                block.getOffset() == 1.0f);
        assertTrue(String.format("Expected 1 declaration but found %d", block.getDeclarations()
                .size()), block.getDeclarations().size() == 1);
    }

    public void testKeyframesWithMultipleOffsets() {
        String source = "@keyframes test { from, to { color: red; } }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        PXKeyframe keyframe = stylesheet.getKeyframeForName("test");
        assertNotNull("Expected a 'test' keyframe to be defined in the stylesheet", keyframe);

        List<PXKeyframeBlock> blocks = keyframe.getBlocks();
        assertTrue(
                String.format("Expected 2 keyframe block but found %d",
                        blocks == null ? 0 : blocks.size()), !CollectionUtil.isEmpty(blocks)
                        && blocks.size() == 2);

        PXKeyframeBlock block = blocks.get(0);
        assertTrue(String.format("Expected offset to be 0.0, but found %s", block.getOffset()),
                block.getOffset() == 0.0f);
        assertTrue(String.format("Expected 1 declaration but found %d", block.getDeclarations()
                .size()), block.getDeclarations().size() == 1);

        block = blocks.get(1);
        assertTrue(String.format("Expected offset to be 1.0, but found %s", block.getOffset()),
                block.getOffset() == 1.0f);
        assertTrue(String.format("Expected 1 declaration but found %d", block.getDeclarations()
                .size()), block.getDeclarations().size() == 1);
    }

    // @media Tests

    public void testMediaQuery() {
        String source = "@media (orientation:portrait) { button { background-color: red; } }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<PXMediaGroup> mediaGroups = stylesheet.getMediaGroups();
        assertTrue("Expected one media group",
                !CollectionUtil.isEmpty(mediaGroups) && mediaGroups.size() == 1);

        // check expressions
        PXMediaGroup mediaGroup = mediaGroups.get(0);
        PXNamedMediaExpression expression = (PXNamedMediaExpression) mediaGroup.getQuery();
        assertTrue("Expected name to be 'orientation'", "orientation".equals(expression.getName()));
        assertTrue("Expected value to be 'portrait'", "portrait".equals(expression.getValue()));

        // check rule sets
        List<PXRuleSet> ruleSets = mediaGroup.getRuleSets();
        assertTrue("Expected one rule set", ruleSets.size() == 1);
    }

    public void testMediaQueries() {
        String source = "@media (orientation:portrait) and (min-device-width:100) { button { background-color: red; } }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<PXMediaGroup> mediaGroups = stylesheet.getMediaGroups();
        assertTrue("Expected one media group",
                !CollectionUtil.isEmpty(mediaGroups) && mediaGroups.size() == 1);

        // check expressions
        PXMediaGroup mediaGroup = mediaGroups.get(0);
        PXMediaExpressionGroup expressionGroup = (PXMediaExpressionGroup) mediaGroup.getQuery();
        List<PXMediaExpression> expressions = expressionGroup.getExpressions();
        assertTrue("Expected two query expressions", !CollectionUtil.isEmpty(expressions)
                && expressions.size() == 2);

        PXNamedMediaExpression expression = (PXNamedMediaExpression) expressions.get(0);
        assertTrue("Expected name to be 'orientation'", "orientation".equals(expression.getName()));
        assertTrue("Expected value to be 'portrait'", "portrait".equals(expression.getValue()));

        expression = (PXNamedMediaExpression) expressions.get(1);
        assertTrue("Expected name to be 'orientation'",
                "min-device-width".equals(expression.getName()));
        assertTrue("Expected value to be 100", "100".equals(expression.getValue()));

        // check rule sets
        List<PXRuleSet> ruleSets = mediaGroup.getRuleSets();
        assertTrue("Expected one rule set", !CollectionUtil.isEmpty(ruleSets)
                && ruleSets.size() == 1);
    }

    public void testNoMediaQuery() {
        String source = "button { background-color: red; }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<PXMediaGroup> mediaGroups = stylesheet.getMediaGroups();
        assertTrue("Expected one media group",
                !CollectionUtil.isEmpty(mediaGroups) && mediaGroups.size() == 1);

        // check expressions
        PXMediaGroup mediaGroup = mediaGroups.get(0);
        PXNamedMediaExpression expression = (PXNamedMediaExpression) mediaGroup.getQuery();
        assertNull("Expected the media group query to be null", expression);

        // check rule sets
        List<PXRuleSet> ruleSets = mediaGroup.getRuleSets();
        assertTrue("Expected one rule set", !CollectionUtil.isEmpty(ruleSets)
                && ruleSets.size() == 1);
    }

    public void testMediaWithLeadingAnd() {
        String source = "@media screen and (min-width: 768px) { button { color: red; } }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<String> errors = parser.getErrors();
        assertTrue("Unexpected parse error", CollectionUtil.isEmpty(errors));

        List<PXMediaGroup> mediaGroups = stylesheet.getMediaGroups();
        assertTrue(String.format("Expected 1 media group, found %d", mediaGroups == null ? 0
                : mediaGroups.size()), !CollectionUtil.isEmpty(mediaGroups)
                && mediaGroups.size() == 1);
    }

    public void testMediaWithRatio() {
        String source = "@media (device-aspect-ratio: 4/5) { button { color: red; } }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        List<PXMediaGroup> mediaGroups = stylesheet.getMediaGroups();
        assertTrue("Expected one media group, got "
                + (mediaGroups == null ? 0 : mediaGroups.size()),
                !CollectionUtil.isEmpty(mediaGroups) && mediaGroups.size() == 1);

        // check expressions
        PXMediaGroup mediaGroup = mediaGroups.get(0);
        PXNamedMediaExpression expression = (PXNamedMediaExpression) mediaGroup.getQuery();
        Object value = expression.getValue();
        assertTrue("Expected device aspect ratio to be an NSNumber", value instanceof Number);
        Number number = (Number) value;
        assertEquals(
                String.format("Expected device aspect ratio to be 0.8, but was %f",
                        number.floatValue()), 0.8f, number.floatValue());

        // check rule sets
        List<PXRuleSet> ruleSets = mediaGroup.getRuleSets();
        assertTrue("Expected one rule set", !CollectionUtil.isEmpty(ruleSets)
                && ruleSets.size() == 1);
    }

    // inline CSS Tests

    public void testInlineCssWithHexColor() {
        String source = "text: hello; size: 100 50; color: red; border-radius: 10; border-width: 1px; background-color: linear-gradient(red, #d97410);";

        PXStylesheet stylesheet = parser.parseInlineCSS(source);

        List<String> errors = parser.getErrors();
        assertTrue(String.format("Unexpected parse error: %d", errors == null ? 0 : errors.size()),
                CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected one rule set", !CollectionUtil.isEmpty(ruleSets)
                && ruleSets.size() == 1);

        PXRuleSet ruleSet = ruleSets.get(0);
        PXDeclaration backgroundColor = ruleSet.getDeclarationForName("background-color");
        assertNotNull("Expected a 'background-color' declaration", backgroundColor);

        PXPaint paint = backgroundColor.getPaintValue();
        assertNotNull("Expected a paint value", paint);
        assertTrue(String.format("Expected 'PXLinearGradient' class but found %s", paint.getClass()
                .getSimpleName()), paint instanceof PXLinearGradient);
    }

    // Performance Tests

    public void testLargeCSS() {
        float start = System.currentTimeMillis();
        PXStylesheet stylesheet = PXStylesheet.getStyleSheetFromFilePath(getContext(),
                "stylesheetParsing/large.css", PXStyleSheetOrigin.APPLICATION);
        float diff = System.currentTimeMillis() - start;

        assertNotNull("Expected a stylesheet", stylesheet);

        Log.i(PXStylesheetParserTests.class.getSimpleName(),
                String.format("Elapsed time = %f", diff * 1000));
    }

    // Bug Fixes

    public void testCrashWithHexPaint() {
        String source = "button { background-color: #D4D4D; }";

        PXStylesheet stylesheet = parser.parse(source, PXStyleSheetOrigin.APPLICATION);

        // NOTE: If we get this far, we didn't crash :)

        List<String> errors = parser.getErrors();
        assertTrue(String.format("Unexpected parse error: %d", errors == null ? 0 : errors.size()),
                CollectionUtil.isEmpty(errors));

        List<PXRuleSet> ruleSets = stylesheet.getRuleSets(getContext());
        assertTrue("Expected one rule set", ruleSets.size() == 1);
    }

    public void testCrashOnImport() {
        PXStylesheet stylesheet = PXStylesheet.getStyleSheetFromFilePath(getContext(),
                "stylesheetParsing/crashOnImport.css", PXStyleSheetOrigin.APPLICATION);
        assertNotNull("Expected a stylesheet after parsing crashOnImport.css", stylesheet);
    }
}
