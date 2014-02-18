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
package com.pixate.pxengine.cg;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.pixate.PixateFreestyle;
import com.pixate.pxengine.cg.parsing.PXSVGLoader;
import com.pixate.pxengine.cg.shapes.PXShapeDocument;
import com.pixate.pxengine.cg.shapes.PXShapeGroup;

/**
 * SVG images tests.<br>
 * XXX - Note: These tests will pass on the new Nexus 7. Eventually, we'll have
 * to generate the expected PNG results for every device we'll test on. The
 * rendering is different for every device type.
 */
public class PXSVGRenderingTests extends ImageBasedTests {

    // TODO - Have a 'Rendered' directory per device type.
    private static final String SVG_RENDERED_PATH = "SVG/Rendered/";
    private static final String SVG_BASE_PATH = "SVG/Vector/";
    private Bitmap svgImage;
    private Bitmap pngImage;

    @Override
    protected void setUp() throws Exception {
        PixateFreestyle.init(getContext());
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        // cleanup
        if (svgImage != null) {
            svgImage.recycle();
            svgImage = null;
        }
        if (pngImage != null) {
            pngImage.recycle();
            pngImage = null;
        }
        super.tearDown();
    }

    protected void assertSVG(String name) throws Exception {
        // Render the SVG into a Bitmap
        svgImage = getSVGImageForName(name);
        assertNotNull(svgImage);
        // Load the equivalent PNG file that we'll compare against
        int pathSeparatorIndex = name.lastIndexOf("/");
        if (pathSeparatorIndex > -1) {
            name = name.substring(pathSeparatorIndex + 1);
        }
        pngImage = BitmapFactory.decodeStream(getContext().getAssets().open(
                SVG_RENDERED_PATH + name + ".png"));
        assertNotNull(pngImage);

        // compare
        assertImages(name, svgImage, pngImage);
    }

    protected Bitmap getSVGImageForName(String name) throws IOException {
        // get path to SVG file
        String path = SVG_BASE_PATH + name + ".svg";

        // create a shape document (load the SVG)
        PXShapeDocument document = PXSVGLoader.loadFromStream(getContext().getAssets().open(path));

        // grab root shape
        PXShapeGroup root = (PXShapeGroup) document.getShape();
        RectF bounds = root.getViewport();

        if (bounds == null || bounds.isEmpty()) {
            // use 100x100 if we didn't find a view port in the SVG file
            bounds = new RectF(0, 0, 100, 100);
        }

        // set size
        document.setBounds(bounds);
        // render to UIImage
        Drawable drawable = root.renderToImage(bounds, false);
        return ((BitmapDrawable) drawable).getBitmap();
    }

    // Shapes tests

    public void testLine() throws Exception {
        assertSVG("Shapes/line");
    }

    public void testCircle() throws Exception {
        assertSVG("Shapes/circle");
    }

    public void testEllipse() throws Exception {
        assertSVG("Shapes/ellipse");
    }

    public void testRectangle() throws Exception {
        assertSVG("Shapes/rect");
    }

    // Path Command Tests

    public void testArcCommand() throws Exception {
        assertSVG("Paths/arcCommand");
    }

    public void testCloseCommand() throws Exception {
        assertSVG("Paths/closeCommand");
    }

    public void testCubicBezierCommand() throws Exception {
        assertSVG("Paths/cubicBezierCommand");
    }

    public void testHorizontalLineCommand() throws Exception {
        assertSVG("Paths/horizontalLineCommand");
    }

    public void testLineCommand() throws Exception {
        assertSVG("Paths/lineCommand");
    }

    public void testMoveCommand() throws Exception {
        assertSVG("Paths/moveCommand");
    }

    public void testMoveCommand2() throws Exception {
        // NOTE: This is actually failing but this is due to a bug in
        // CoreGraphics
        assertSVG("Paths/moveCommand2");
    }

    public void testQuadraticBezierCommand() throws Exception {
        assertSVG("Paths/quadraticBezierCommand");
    }

    public void testSmoothCubicBezierCommand() throws Exception {
        assertSVG("Paths/smoothCubicBezierCommand");
    }

    public void testSmoothQuadraticBezierCommand() throws Exception {
        assertSVG("Paths/smoothQuadraticBezierCommand");
    }

    public void testVerticalLineCommand() throws Exception {
        assertSVG("Paths/verticalLineCommand");
    }

    public void testRelativeArcCommand() throws Exception {
        assertSVG("Paths/relativeArcCommand");
    }

    public void testRelativeCloseCommand() throws Exception {
        assertSVG("Paths/relativeCloseCommand");
    }

    public void testRelativeCubicBezierCommand() throws Exception {
        assertSVG("Paths/relativeCubicBezierCommand");
    }

    public void testRelativeHorizontalLineCommand() throws Exception {
        assertSVG("Paths/relativeHorizontalLineCommand");
    }

    public void testRelativeLineCommand() throws Exception {
        assertSVG("Paths/relativeLineCommand");
    }

    public void testRelativeMoveCommand() throws Exception {
        assertSVG("Paths/relativeMoveCommand");
    }

    public void testRelativeQuadraticBezierCommand() throws Exception {
        assertSVG("Paths/relativeQuadraticBezierCommand");
    }

    public void testRelativeSmoothCubicBezierCommand() throws Exception {
        assertSVG("Paths/relativeSmoothCubicBezierCommand");
    }

    public void testRelativeSmoothQuadraticBezierCommand() throws Exception {
        assertSVG("Paths/relativeSmoothQuadraticBezierCommand");
    }

    public void testRelativeVerticalLineCommand() throws Exception {
        assertSVG("Paths/relativeVerticalLineCommand");
    }

    // Text Tests

    // public void testText() throws Exception {
    // assertSVG("Text/text");
    // }

    // Color Tests

    public void testOpacity() throws Exception {
        assertSVG("Colors/opacity");
    }

    // Gradient Tests

    public void testLinearGradient() throws Exception {
        assertSVG("Gradients/linear-gradient");
    }

    public void testRadialGradient() throws Exception {
        assertSVG("Gradients/radial-gradient");
    }

    // Strokes

    public void testLineCaps() throws Exception {
        assertSVG("Strokes/line-caps");
    }

    public void testLineJoin() throws Exception {
        assertSVG("Strokes/line-join");
    }

    public void testStrokeTypes() throws Exception {
        assertSVG("Strokes/stroke-types");
    }

    // Clipping Path Tests

    // public void testClippingPath() throws Exception {
    // assertSVG("Clipping-Paths/clipping-path");
    // }

    // public void testClippingPath2() throws Exception {
    // assertSVG("Clipping-Paths/clipping-path2");
    // }

    // Samples

    public void testLion() throws Exception {
        assertSVG("Samples/lion");
    }

    public void testPeople() throws Exception {
        assertSVG("Samples/people");
    }

    public void testToucan() throws Exception {
        assertSVG("Samples/toucan");
    }

    public void testWoodGrain() throws Exception {
        assertSVG("Samples/wood");
    }

    public void testIcon1() throws Exception {
        assertSVG("Samples/icon1");
    }

    public void testIcon2() throws Exception {
        assertSVG("Samples/icon2");
    }

    public void testIcon3() throws Exception {
        assertSVG("Samples/icon3");
    }

    public void testIcon4() throws Exception {
        assertSVG("Samples/icon4");
    }
}
