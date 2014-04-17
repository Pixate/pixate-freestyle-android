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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.graphics.Matrix;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.cg.math.PXDimension;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.stylers.PXGenericStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXStylerBase.PXDeclarationHandler;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.styling.virtualAdapters.PXVirtualImageViewImageAdapter;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualImageViewImage;
import com.pixate.freestyle.util.PXLog;

/**
 * A style adapter for {@link ImageView} widgets. This adapter supports the
 * regular view styling properties, plus a few special properties that are
 * unique for the {@link ImageView}.
 * 
 * <pre>
 * - scale-type: center | center-crop | center-inside | fit-center | fit-end | fit-start | fit-xy | matrix
 * - max-height: px
 * - max-width: px
 * - view-bounds: adjust | none
 * - tint: color
 * - transform: matrix (inherited from the view styles, but used here when the scale-type is set to 'matrix')
 * </pre>
 * 
 * For example:
 * 
 * <pre>
 * .imageView {
 *     tint: #450022FF;
 *     transform: matrix(0.8660254037844387, 0.49999999999999994, -0.49999999999999994, 0.8660254037844387, 0, 0);
 *     scale-type: matrix;
 * }
 * </pre>
 * 
 * And to set the image properties (virtual child of 'image'):
 * 
 * <pre>
 * .imageView image {
 *     background-image: url(mic-on.svg);
 *     background-size: 300px;
 * }
 * 
 * .imageView image:pressed {
 *     background-image: url(mic-off.svg);
 *     background-size: 300px;
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
@PXDocElement(properties = {
        @PXDocProperty(name = "scale-type", syntax = "center | center-crop | center-inside | fit-center | fit-end | fit-start | fit-xy | matrix"),
        @PXDocProperty(name = "max-height", syntax = "<length>"),
        @PXDocProperty(name = "max-width", syntax = "<length>"),
        @PXDocProperty(name = "view-bounds", syntax = "adjust"),
        @PXDocProperty(name = "tint", syntax = "<color>") })
public class PXImageViewStyleAdapter extends PXViewStyleAdapter {

    private static String TAG = PXVirtualImageViewImageAdapter.class.getSimpleName();
    private static String ELEMENT_NAME = "image-view";
    private static PXImageViewStyleAdapter instance;

    /**
     * Returns an instance of this {@link PXImageViewStyleAdapter}
     */
    public static PXImageViewStyleAdapter getInstance() {
        synchronized (PXImageViewStyleAdapter.class) {

            if (instance == null) {
                instance = new PXImageViewStyleAdapter();
            }
        }
        return instance;
    }

    protected PXImageViewStyleAdapter() {
    }

    @Override
    protected List<PXStyler> createStylers() {
        List<PXStyler> stylers = super.createStylers();
        // Append the ImageView stylers
        Map<String, PXDeclarationHandler> handlers = new HashMap<String, PXDeclarationHandler>(1);

        handlers.put("scale-type", new PXDeclarationHandler() {
            @Override
            public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                String value = declaration.getStringValue();
                if (value != null) {
                    // get the match from the scale-types enum
                    value = value.replaceAll("-", "_").toUpperCase(Locale.US);
                    ScaleType type = ScaleType.valueOf(value);
                    if (type != null) {
                        ImageView view = (ImageView) stylerContext.getStyleable();
                        view.setScaleType(type);
                    } else {
                        PXLog.e(TAG, "Unknown ImageView scale-type '%s'", value);
                    }
                }
            }
        });

        handlers.put("max-height", new PXDeclarationHandler() {
            @Override
            public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                PXDimension height = declaration.getLengthValue();
                if (height != null) {
                    ImageView view = (ImageView) stylerContext.getStyleable();
                    view.setMaxHeight((int) Math.ceil(height.getNumber()));
                } else {
                    PXLog.e(TAG, "Unknown ImageView max-height '%s'", declaration.getStringValue());
                }
            }
        });

        handlers.put("max-width", new PXDeclarationHandler() {
            @Override
            public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                PXDimension width = declaration.getLengthValue();
                if (width != null) {
                    ImageView view = (ImageView) stylerContext.getStyleable();
                    view.setMaxWidth((int) Math.ceil(width.getNumber()));
                } else {
                    PXLog.e(TAG, "Unknown ImageView max-width '%s'", declaration.getStringValue());
                }
            }
        });

        handlers.put("view-bounds", new PXDeclarationHandler() {
            @Override
            public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                String viewBounds = declaration.getStringValue();
                if (viewBounds != null) {
                    ImageView view = (ImageView) stylerContext.getStyleable();
                    view.setAdjustViewBounds("adjust".equals(viewBounds));
                } else {
                    PXLog.e(TAG, "Unknown ImageView view-bounds '%s'", viewBounds);
                }
            }
        });

        handlers.put("tint", new PXDeclarationHandler() {
            @Override
            public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                Integer colorValue = declaration.getColorValue();
                if (colorValue != null) {
                    ImageView view = (ImageView) stylerContext.getStyleable();
                    view.setColorFilter(colorValue);
                } else {
                    PXLog.e(TAG, "Unknown ImageView tint '%s'", declaration.getStringValue());
                }
            }
        });

        stylers.add(new PXGenericStyler(handlers));
        return stylers;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#getElementName
     * (java.lang.Object)
     */
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getVirtualChildren
     * (java.lang.Object)
     */
    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        List<Object> superVirtuals = super.getVirtualChildren(styleable);
        List<Object> result = new ArrayList<Object>(superVirtuals.size() + 1);
        result.addAll(superVirtuals);
        result.add(new PXVirtualImageViewImage(styleable));
        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#updateStyle(java
     * .util.List, java.util.List)
     */
    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        // Check for the Matrix in the contexts.
        ImageView view = (ImageView) contexts.get(0).getStyleable();
        // Check if we have a transformation that we can use. We can only have
        // one transformation, so look for one in each of the contexts.
        Matrix matrix = null;
        for (PXStylerContext context : contexts) {
            matrix = context.getTransform();
            if (matrix != null) {
                break;
            }
        }
        if (matrix != null) {
            view.setImageMatrix(matrix);
        }
        return super.updateStyle(ruleSets, contexts);
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#
     * createAdditionalStates (int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        List<int[]> states = new ArrayList<int[]>(4);
        // check for some special cases.
        switch (initialValue) {
            case android.R.attr.state_pressed:
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_pressed });
                states.add(new int[] { -android.R.attr.state_focused, android.R.attr.state_pressed });
                break;
            case android.R.attr.drawable:
                // add anything that will be treated as the default.
                states.add(new int[] { -android.R.attr.state_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused });
                states.add(new int[] {});
            default:
                break;
        }
        states.add(new int[] { initialValue });
        return states.toArray(new int[states.size()][]);
    }
}
