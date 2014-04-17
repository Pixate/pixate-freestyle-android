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
/**
 * Copyright (c) 2012 Pixate, Inc. All rights reserved.
 */
package com.pixate.freestyle.cg.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Matrix;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;

import com.pixate.freestyle.cg.paints.PXGradient;
import com.pixate.freestyle.cg.paints.PXGradient.PXGradientUnits;
import com.pixate.freestyle.cg.paints.PXLinearGradient;
import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.paints.PXRadialGradient;
import com.pixate.freestyle.cg.paints.PXSolidPaint;
import com.pixate.freestyle.cg.shapes.PXArc;
import com.pixate.freestyle.cg.shapes.PXCircle;
import com.pixate.freestyle.cg.shapes.PXEllipse;
import com.pixate.freestyle.cg.shapes.PXLine;
import com.pixate.freestyle.cg.shapes.PXPath;
import com.pixate.freestyle.cg.shapes.PXPie;
import com.pixate.freestyle.cg.shapes.PXPolygon;
import com.pixate.freestyle.cg.shapes.PXRectangle;
import com.pixate.freestyle.cg.shapes.PXShape;
import com.pixate.freestyle.cg.shapes.PXShapeDocument;
import com.pixate.freestyle.cg.shapes.PXShapeGroup;
import com.pixate.freestyle.cg.shapes.PXShapeGroup.AlignViewPortType;
import com.pixate.freestyle.cg.shapes.PXShapeGroup.CropType;
import com.pixate.freestyle.cg.shapes.PXText;
import com.pixate.freestyle.cg.strokes.PXStroke;
import com.pixate.freestyle.cg.strokes.PXStroke.PXStrokeType;
import com.pixate.freestyle.styling.parsing.PXValueParser;
import com.pixate.freestyle.util.PXColorUtil;
import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.Scanner;
import com.pixate.freestyle.util.Size;
import com.pixate.freestyle.util.StringUtil;
import com.pixate.freestyle.util.UrlStreamOpener;

/**
 * PXSVGLoader
 * 
 * @author Shalom Gibly
 */
public class PXSVGLoader {

    // Used to time the SVG loading
    private static final boolean TIME_LOGGING = false;
    // TODO - Attach this to some verification mechanism
    private static final boolean PXTEXT_SUPPORT = true;
    private static final String TAG = PXSVGLoader.class.getSimpleName();
    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile("[ ,\r\n]");

    private static final Pattern COLON_SEPARATOR = Pattern.compile(":");
    private static final Pattern SEMICOLON_SEPARATOR = Pattern.compile(";");

    /**
     * Returns a {@link PXShapeDocument} after parsing a SVG file (subset of
     * SVG).
     * 
     * @param url
     * @return {@link PXShapeDocument}
     * @throws IOException
     */
    public static PXShapeDocument loadFromURL(Uri url) throws IOException {
        return loadFromStream(UrlStreamOpener.open(url));
    }

    /**
     * Returns a {@link PXShapeDocument} after parsing a SVG resource (subset of
     * SVG).
     * 
     * @param resources
     * @param resourceId
     * @return {@link PXShapeDocument}
     * @throws IOException
     * @throws NotFoundException
     */
    public static PXShapeDocument loadFromResource(Resources resources, int resourceId)
            throws NotFoundException, IOException {
        return loadFromStream(resources.openRawResource(resourceId));
    }

    /**
     * Returns a {@link PXShapeDocument} after parsing a SVG stream (subset of
     * SVG).
     * 
     * @param inputStream An {@link InputStream}. Will be closed by this method
     *            after the {@link PXShape} is loaded.
     * @return {@link PXShapeDocument}
     * @throws IOException
     */
    public static PXShapeDocument loadFromStream(InputStream inputStream) throws IOException {
        long start = System.currentTimeMillis();
        PXSVGParser parser = new PXSVGParser();
        PXShapeGroup result = parser.parse(inputStream);
        PXShapeDocument scene = parser.getDocument();
        if (result == null) {
            PXLog.e(TAG, "Error parsing document from input stream");
        } else {
            scene.setShape(result);
        }
        if (TIME_LOGGING) {
            PXLog.i(TAG, "Loading SVG (from stream) took " + (System.currentTimeMillis() - start)
                    + "ms");
        }
        return scene;
    }

    /**
     * Parses the SVG and returns a {@link Picture}.
     */
    protected static class PXSVGParser extends DefaultHandler {

        // Supported SVG elements.
        private static final String SVG_ELEMENT = "svg";
        private static final String GROUP_ELEMENT = "g";
        private static final String PATH_ELEMENT = "path";
        private static final String RECT_ELEMENT = "rect";
        private static final String LINE_ELEMENT = "line";
        private static final String CIRCLE_ELEMENT = "circle";
        private static final String ELLIPSE_ELEMENT = "ellipse";
        private static final String LINEAR_GRADIENT_ELEMENT = "linearGradient";
        private static final String RADIAL_GRADIENT_ELEMENT = "radialGradient";
        private static final String STOP_ELEMENT = "stop";
        private static final String POLYGON_ELEMENT = "polygon";
        private static final String POLYLINE_ELEMENT = "polyline";
        private static final String TEXT_ELEMENT = "text";
        private static final String ARC_ELEMENT = "arc";
        private static final String PIE_ELEMENT = "pie";

        // static parsers
        private static PXTransformParser transformParser = new PXTransformParser();
        private static PXValueParser valueParser = new PXValueParser();

        // Use an ArrayDeque an a non-synchronized replacement for Stack.
        private PXShapeDocument document;
        private PXShapeGroup result;
        private ArrayDeque<PXShapeGroup> stack;
        private PXGradient currentGradient;
        private Map<String, PXGradient> gradients;
        private Map<String, PXShapeGroup.AlignViewPortType> alignTypes;
        private PXText currentTextElement;

        /**
         * Constructs a parser
         */
        protected PXSVGParser() {
            document = new PXShapeDocument();
            stack = new ArrayDeque<PXShapeGroup>();
            gradients = new HashMap<String, PXGradient>();
            // view port alignment type map
            alignTypes = new HashMap<String, PXShapeGroup.AlignViewPortType>(10);
            for (AlignViewPortType type : EnumSet.allOf(AlignViewPortType.class)) {
                alignTypes.put(type.toString(), type);
            }
        }

        /**
         * Returns the root group, which is the result of the shape parsing.
         * 
         * @return A {@link PXShapeGroup} instance.
         */
        protected PXShapeGroup getResult() {
            return result;
        }

        /**
         * Returns the document (scene).
         * 
         * @return A {@link PXShapeDocument}
         */
        protected PXShapeDocument getDocument() {
            return document;
        }

        /**
         * Parses the stream and returns a {@link PXShape} out of it.
         * 
         * @return A {@link PXShapeGroup}
         * @throws PXSVGParseException
         */
        protected PXShapeGroup parse(InputStream inputStream) throws PXSVGParseException {
            try {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                XMLReader reader = parser.getXMLReader();
                reader.setContentHandler(this);
                reader.parse(new InputSource(new InputStreamReader(inputStream, "UTF-8")));
                inputStream.close();
            } catch (Throwable t) {
                throw new PXSVGParseException("Error processing the shape", t);
            }
            return result;
        }

        /*
         * (non-Javadoc)
         * @see
         * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
         * java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (localName == null) {
                return;
            }

            // merge style properties with attribute values. Style declarations
            // override attributes
            attributes = getAttributesWithMergedStyles(attributes);

            if (SVG_ELEMENT.equals(localName)) {
                PXShapeGroup newGroup = new PXShapeGroup();

                // set viewport, if we have one
                Float width = numberFromString(attributes.getValue("width"), null);
                Float height = numberFromString(attributes.getValue("height"), null);
                if (width != null && height != null) {
                    newGroup.setViewport(new RectF(0, 0, width, height));
                    // set viewport settings, if we have those
                    applyViewport(attributes, newGroup);
                }
                // create top-level group
                stack.push(newGroup);
            } else if (GROUP_ELEMENT.equals(localName)) {
                // create nested group
                PXShapeGroup newGroup = new PXShapeGroup();

                // TODO: set all inherited properties
                newGroup.setOpacity(opacityFromString(attributes.getValue("opacity")));

                // id
                String ident = attributes.getValue("id");
                if (ident != null) {
                    document.addShape(ident, newGroup);
                }

                // transform
                newGroup.setTransform(transformFromString(attributes.getValue("transform")));

                // set viewport settings, if we have those
                applyViewport(attributes, newGroup);

                // add group as child of active group
                addShape(stack, newGroup);
                // push new group as active group
                stack.push(newGroup);
            } else if (PATH_ELEMENT.equals(localName)) {
                // add path to current group
                String data = attributes.getValue("d");
                if (data != null) {
                    PXPath path = PXPath.createPathFromPathData(data);
                    applyStyles(attributes, path, gradients, document);
                    addShape(stack, path);
                }
            } else if (RECT_ELEMENT.equals(localName)) {
                // add path to current group
                Float x = numberFromString(attributes.getValue("x"));
                Float y = numberFromString(attributes.getValue("y"));
                RectF rect = new RectF(x, y, x + numberFromString(attributes.getValue("width")), y
                        + numberFromString(attributes.getValue("height")));
                Float rx = numberFromString(attributes.getValue("rx"), null);
                Float ry = numberFromString(attributes.getValue("ry"), null);

                PXRectangle rectangle = new PXRectangle(rect);
                if (rx != null && ry != null) {
                    rectangle.setCornerRadii(new Size(rx, ry));
                }
                applyStyles(attributes, rectangle, gradients, document);
                addShape(stack, rectangle);
            } else if (LINE_ELEMENT.equals(localName)) {
                PXLine line = new PXLine(numberFromString(attributes.getValue("x1")),
                        numberFromString(attributes.getValue("y1")),
                        numberFromString(attributes.getValue("x2")),
                        numberFromString(attributes.getValue("y2")));
                applyStyles(attributes, line, gradients, document);
                addShape(stack, line);
            } else if (CIRCLE_ELEMENT.equals(localName)) {
                PXCircle circle = new PXCircle(new PointF(
                        numberFromString(attributes.getValue("cx")),
                        numberFromString(attributes.getValue("cy"))),
                        numberFromString(attributes.getValue("r")));
                applyStyles(attributes, circle, gradients, document);
                addShape(stack, circle);
            } else if (ELLIPSE_ELEMENT.equals(localName)) {
                PXEllipse ellipse = new PXEllipse(new PointF(
                        numberFromString(attributes.getValue("cx")),
                        numberFromString(attributes.getValue("cy"))),
                        numberFromString(attributes.getValue("rx")),
                        numberFromString(attributes.getValue("ry")));
                applyStyles(attributes, ellipse, gradients, document);
                addShape(stack, ellipse);
            } else if (LINEAR_GRADIENT_ELEMENT.equals(localName)) {
                String name = attributes.getValue("id");
                if (name != null) {
                    Matrix transform = transformFromString(attributes.getValue("gradientTransform"));
                    PXLinearGradient gradient = new PXLinearGradient();
                    gradient.setP1(new PointF(numberFromString(attributes.getValue("x1")),
                            numberFromString(attributes.getValue("y1"))));
                    gradient.setP2(new PointF(numberFromString(attributes.getValue("x2")),
                            numberFromString(attributes.getValue("y2"))));
                    gradient.setTransform(transform);
                    String gradientUnits = attributes.getValue("gradientUnits");

                    if ("userSpaceOnUse".equals(gradientUnits)) {
                        gradient.setGradientUnits(PXGradientUnits.USER_SPACE);
                    } else {
                        // assume all non-valid values in addition to
                        // "objectBoundingBox" mean bounding box
                        gradient.setGradientUnits(PXGradientUnits.BOUNDING_BOX);
                    }

                    currentGradient = gradient;
                    gradients.put(name, currentGradient);
                } else {
                    PXLog.i(TAG, "Skipping unnamed linear gradient");
                }
            } else if (RADIAL_GRADIENT_ELEMENT.equals(localName)) {
                String name = attributes.getValue("id");
                if (name != null) {
                    PXRadialGradient gradient = new PXRadialGradient();
                    gradient.setCenter(new PointF(numberFromString(attributes.getValue("cx")),
                            numberFromString(attributes.getValue("cy"))));
                    gradient.setRadius(numberFromString(attributes.getValue("r")));
                    String gradientUnits = attributes.getValue("gradientUnits");
                    gradient.setTransform(transformFromString(attributes
                            .getValue("gradientTransform")));
                    // TODO - Add (somehow) start and end centers for the
                    // gradient.
                    // Float fx = numberFromString(attributes.getValue("fx"));
                    // Float fy = numberFromString(attributes.getValue("fy"));
                    // if (fx != null && fy != null) {
                    // gradient.setStartCenter(new PointF(fx, fy));
                    // } else {
                    // gradient.setStartCenter(gradient.getEndCenter());
                    // }
                    if ("userSpaceOnUse".equals(gradientUnits)) {
                        gradient.setGradientUnits(PXGradientUnits.USER_SPACE);
                    } else {
                        // assume all non-valid values in addition to
                        // "objectBoundingBox" mean bounding box
                        gradient.setGradientUnits(PXGradientUnits.BOUNDING_BOX);
                    }

                    currentGradient = gradient;
                    gradients.put(name, currentGradient);
                } else {
                    PXLog.i(TAG, "Skipping unnamed radial gradient");
                }
            } else if (STOP_ELEMENT.equals(localName)) {
                if (currentGradient != null) {
                    Float offset = numberFromString(attributes.getValue("offset"), null);
                    String stopColorString = attributes.getValue("stop-color");

                    if (stopColorString != null) {
                        String stopOpacityString = attributes.getValue("stop-opacity");
                        Integer stopColor = valueParser.parseColor(PXValueParser
                                .lexemesForSource(stopColorString));

                        if (stopOpacityString != null && stopColor != null) {
                            stopColor = PXColorUtil.colorWithAlpha(stopColor,
                                    opacityFromString(stopOpacityString));
                        }

                        if (offset != null) {
                            currentGradient.addOffset(offset);
                        }
                        currentGradient.addColor(stopColor);
                    } else {
                        PXLog.e(TAG, "Stop element is missing a stop-color");
                    }
                } else {
                    PXLog.e(TAG,
                            "Skipping stop element since it is not contained within a gradient element");
                }
            } else if (POLYGON_ELEMENT.equals(localName)) {
                PXPolygon polygon = makePolygon(attributes.getValue("points"));
                polygon.setClosed(true);
                applyStyles(attributes, polygon, gradients, document);
                addShape(stack, polygon);
            } else if (POLYLINE_ELEMENT.equals(localName)) {
                PXPolygon polygon = makePolygon(attributes.getValue("points"));
                polygon.setClosed(false);
                applyStyles(attributes, polygon, gradients, document);
                addShape(stack, polygon);
            } else if (TEXT_ELEMENT.equals(localName)) {
                if (PXTEXT_SUPPORT) {
                    float x = numberFromString(attributes.getValue("x"), 0F);
                    float y = numberFromString(attributes.getValue("y"), 0F);
                    PXText text = new PXText();
                    text.setOrigin(new PointF(x, y));

                    applyStyles(attributes, text, gradients, document);
                    addShape(stack, text);

                    currentTextElement = text;
                }
            } else if (ARC_ELEMENT.equals(localName)) {
                float cx = numberFromString(attributes.getValue("cx"));
                float cy = numberFromString(attributes.getValue("cy"));
                float r = numberFromString(attributes.getValue("r"));
                float startAngle = numberFromString(attributes.getValue("start-angle"));
                float endAngle = numberFromString(attributes.getValue("end-angle"));

                PXArc arc = new PXArc();
                arc.setCenter(new PointF(cx, cy));
                arc.setRadius(r);
                arc.setStartingAngle(startAngle);
                arc.setEndingAngle(endAngle);

                applyStyles(attributes, arc, gradients, document);
                addShape(stack, arc);
            } else if (PIE_ELEMENT.equals(localName)) {
                float cx = numberFromString(attributes.getValue("cx"));
                float cy = numberFromString(attributes.getValue("cy"));
                float r = numberFromString(attributes.getValue("r"));
                float startAngle = numberFromString(attributes.getValue("start-angle"));
                float endAngle = numberFromString(attributes.getValue("end-angle"));
                PXPie pie = new PXPie();
                pie.setCenter(new PointF(cx, cy));
                pie.setRadius(r);
                pie.setStartingAngle(startAngle);
                pie.setEndingAngle(endAngle);

                applyStyles(attributes, pie, gradients, document);
                addShape(stack, pie);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
         * java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName == null) {
                return;
            }
            if (SVG_ELEMENT.equals(localName)) {
                result = stack.pop();
            } else if (GROUP_ELEMENT.equals(localName)) {
                stack.pop();
            } else if (LINEAR_GRADIENT_ELEMENT.equals(localName)
                    || RADIAL_GRADIENT_ELEMENT.equals(localName)) {
                currentGradient = null;
            } else if (TEXT_ELEMENT.equals(localName)) {
                if (PXTEXT_SUPPORT) {
                    // TODO: grab accumulated text and assigned to text element
                    currentTextElement.setText("Professional!");
                    currentTextElement = null;
                }
            }
        }

        /**
         * Add a {@link Shape} to the stack.
         * 
         * @param stack
         * @param shape
         */
        private static void addShape(ArrayDeque<PXShapeGroup> stack, PXShape shape) {
            if (!stack.isEmpty()) {
                stack.peek().addShape(shape);
            }
        }

        /**
         * Apply style attributes to the shape.
         * 
         * @param attributes
         * @param shape
         * @param gradients
         * @param scene
         * @param transformParser
         */
        private static void applyStyles(Attributes attributes, PXShape shape,
                Map<String, PXGradient> gradients, PXShapeDocument scene) {
            String strokeDashArray = attributes.getValue("stroke-dasharray");
            String fillColor = attributes.getValue("fill");

            shape.setOpacity(opacityFromString(attributes.getValue("opacity")));

            // fill
            if (fillColor == null) {
                fillColor = "#000000";
            }

            // TODO - Check if in this case we can just update directly the
            // fillColor field.
            // Perhaps add another method that accepts a flag for the update.
            shape.setFillColor(paintFromString(fillColor, attributes.getValue("fill-opacity"),
                    gradients));

            // stroke
            PXStroke stroke = new PXStroke();
            String strokeType = attributes.getValue("stroke-type");
            if (strokeType != null) {
                if ("inner".equals(strokeType)) {
                    stroke.setType(PXStrokeType.INNER);
                } else if ("outer".equals(strokeType)) {
                    stroke.setType(PXStrokeType.OUTER);
                }
                // else, use the default "center"
            }

            stroke.setColor(paintFromString(attributes.getValue("stroke"),
                    attributes.getValue("stroke-opacity"), gradients));
            stroke.setWidth(numberFromString(attributes.getValue("stroke-width"), 1f));

            if (strokeDashArray != null) {
                stroke.setDashArray(numberArrayFromString(strokeDashArray));
            }

            stroke.setDashOffset(numberFromString(attributes.getValue("stroke-dashoffset"))
                    .intValue());
            stroke.setLineCap(lineCapFromString(attributes.getValue("stroke-linecap")));
            stroke.setLineJoin(lineJoinFromString(attributes.getValue("stroke-linejoin")));

            String miterLimit = attributes.getValue("stroke-miterlimit");
            stroke.setMiterLimit((miterLimit != null) ? numberFromString(miterLimit) : 4.0F);
            // TODO - Check if in this case we can just update directly the
            // stroke field. perhaps add another method that accepts a flag for
            // the update.
            shape.setStroke(stroke);

            // visibility
            String visibility = attributes.getValue("visibility");
            if (visibility != null) {
                shape.setVisible("visible".equals(visibility));
            }

            String ident = attributes.getValue("id");

            if (ident != null) {
                scene.addShape(ident, shape);
            }

            // transform
            shape.setTransform(transformFromString(attributes.getValue("transform")));
        }

        private void applyViewport(Attributes attributes, PXShapeGroup group) {
            String par = attributes.getValue("preserveAspectRatio");
            if (par != null) {
                String[] parts = par.split(" ");
                int partCount = parts.length;
                AlignViewPortType alignment = AlignViewPortType.XMID_YMID;
                CropType crop = CropType.MEET;

                if (1 <= partCount && partCount <= 2) {
                    String alignmentString = parts[0];
                    AlignViewPortType typeNumber = alignTypes.get(alignmentString);

                    if (typeNumber != null) {
                        alignment = typeNumber;
                    } else {
                        PXLog.e(TAG, "Unrecognized aspect ratio crop setting: " + alignmentString);
                    }

                    if (partCount == 2) {
                        String cropString = parts[1];

                        if ("meet".equals(cropString)) {
                            crop = CropType.MEET;
                        } else if ("slice".equals(cropString)) {
                            crop = CropType.SLICE;
                        } else {
                            PXLog.e(TAG, "Unrecognized crop type: " + cropString);
                        }
                    } else {
                        PXLog.e(TAG, "Unrecognized preserveAspectRatio value: " + par);
                    }
                }

                group.setViewportAlignment(alignment);
                group.setViewportCrop(crop);
            }
        }

        private static Cap lineCapFromString(String value) {
            if (value == null) {
                return Cap.BUTT;
            }
            Cap cap = Cap.valueOf(value.toUpperCase(Locale.US));
            if (cap == null) {
                PXLog.e(TAG, "Unrecognized line cap: " + value);
            }
            return cap;
        }

        private static Join lineJoinFromString(String value) {
            if (value == null) {
                return Join.MITER;
            }
            Join join = Join.valueOf(value.toUpperCase(Locale.US));
            if (join == null) {
                PXLog.e(TAG, "Unrecognized line join: " + value);
            }
            return join;
        }

        private static float opacityFromString(String value) {
            return (value != null) ? Float.parseFloat(value) : 1.0F;
        }

        private static PXPolygon makePolygon(String pointsString) {
            float[] coords = numberArrayFromString(pointsString);
            int length = coords.length;
            if ((length % 2) == 1) {
                length--;
            }
            PointF[] points = new PointF[length / 2];
            for (int i = 0, j = 0; i < length; i += 2, j++) {
                points[j] = new PointF(coords[i], coords[i + 1]);
            }
            return new PXPolygon(points);
        }

        private static PXPaint paintFromString(String attributeValue, String opacity,
                Map<String, PXGradient> gradients) {
            PXPaint paint = null;
            if (attributeValue != null) {
                float alpha = opacityFromString(opacity);
                if (attributeValue.equals("none")) {
                    // TODO - Test if this is a "clear" color
                    paint = new PXSolidPaint(0x00000000);
                } else if (attributeValue.startsWith("#")) {
                    int color = PXColorUtil.colorFromHexString(attributeValue, alpha);
                    paint = new PXSolidPaint(color);
                } else if (attributeValue.startsWith("url(#")) {
                    // locate the gradient color in the gradients map
                    paint = (PXPaint) gradients.get(attributeValue.substring(5,
                            attributeValue.length() - 1));
                } else {
                    paint = valueParser.parsePaint(PXValueParser.lexemesForSource(attributeValue));
                }
            }
            return paint;
        }

        private static Float numberFromString(String value) {
            return numberFromString(value, 0F);
        }

        private static Float numberFromString(String value, Float defaultValue) {
            if (value == null) {
                return defaultValue;
            } else {
                if (value.endsWith("px")) {
                    return Float.parseFloat(value.substring(0, value.length() - 2));
                } else if (value.endsWith("%")) {
                    return Float.parseFloat(value.substring(0, value.length() - 1)) / 100.0f;
                }
                return Float.parseFloat(value);
            }
        }

        private static float[] numberArrayFromString(String value) {
            List<Float> numbers = new ArrayList<Float>(5);
            Scanner scanner = new Scanner(value);
            scanner.useDelimiter(DEFAULT_DELIMITER_PATTERN);
            while (scanner.hasNextFloat()) {
                numbers.add(scanner.nextFloat());
            }
            scanner.close();
            // have to convert the arraylist into float-array
            return toPrimitiveArray(numbers);
        }

        private static float[] toPrimitiveArray(List<Float> values) {
            float[] primitiveValues = new float[values.size()];
            for (int i = 0; i < primitiveValues.length; i++) {
                primitiveValues[i] = values.get(i);
            }
            return primitiveValues;
        }

        private static Matrix transformFromString(String value) {
            if (value != null) {
                return transformParser.parse(value);
            }
            return null;
        }

        /**
         * Returns the Attributes after expanding the "style" values as separate
         * attributes.
         * 
         * @param attributes
         * @return
         */
        private static Attributes getAttributesWithMergedStyles(Attributes attributes) {
            String styles = attributes.getValue("style");
            if (!StringUtil.isEmpty(styles)) {
                // break up the styles into additional attributes.
                AttributesImpl newAttributes = new AttributesImpl(attributes);
                String[] declarations = SEMICOLON_SEPARATOR.split(styles);
                for (String declaration : declarations) {
                    String[] parts = COLON_SEPARATOR.split(declaration);
                    if (parts.length == 2) {
                        newAttributes.addAttribute(StringUtil.EMPTY, StringUtil.EMPTY,
                                parts[0].trim(), StringUtil.EMPTY, parts[1].trim());
                    } else {
                        PXLog.w(TAG, "Expected 2 parts in the style declaration, but got %d. '%s'",
                                parts.length, declaration);
                    }
                }
                attributes = newAttributes;
            }
            return attributes;
        }
    }

}
