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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.widget.ListView;

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.stylers.PXStylerContext.OverscrollStyle;
import com.pixate.freestyle.util.PXDrawableUtil;
import com.pixate.freestyle.util.PXLog;

// @formatter:off
/**
 * -distance: length
 * -header: paint
 * -footer: paint
 */
// @formatter:on
@PXDocStyler(properties = { @PXDocProperty(name = "distance", syntax = "<length>"),
        @PXDocProperty(name = "header", syntax = "<paint>"),
        @PXDocProperty(name = "footer", syntax = "<paint>"), })
public class PXOverscrollStyler extends PXStylerBase {

    private static Map<String, PXDeclarationHandler> handlers;
    private static PXOverscrollStyler instance;

    public synchronized static PXOverscrollStyler getInstance() {
        if (instance == null) {
            instance = new PXOverscrollStyler(null);
        }
        return instance;
    }

    public PXOverscrollStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXOverscrollStyler.class) {
            if (handlers == null) {
                handlers = new HashMap<String, PXDeclarationHandler>(3);

                handlers.put("distance", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setOverscrollDistance(declaration.getFloatValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                handlers.put("footer", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setOverscrollFooter(declaration.getPaintValue());
                    }
                });

                handlers.put("header", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setOverscrollHeader(declaration.getPaintValue());
                    }
                });
            }
            return handlers;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.stylers.PXStylerBase#applyStylesWithContext
     * (com.pixate.freestyle.styling.stylers.PXStylerContext)
     */
    @Override
    public void applyStylesWithContext(PXStylerContext stylerContext) {
        // view.setOverScrollMode(ListView.OVER_SCROLL_ALWAYS); // needed?
        OverscrollStyle overscrollStyle = stylerContext.getOverscrollStyle();
        if (overscrollStyle != null) {
            // Get the ListView through the virtual styleable.
            ListView view = (ListView) stylerContext.getStyleable();
            // Apply the overscroll
            setOverscrollDistance(view, overscrollStyle.distance);
            int viewWidth = view.getWidth();
            if (overscrollStyle.header != null) {
                view.setOverscrollHeader(PXDrawableUtil.createDrawable((viewWidth != 0) ? viewWidth
                        : overscrollStyle.distance, overscrollStyle.distance,
                        overscrollStyle.header));
            }
            if (overscrollStyle.footer != null) {
                view.setOverscrollFooter(PXDrawableUtil.createDrawable((viewWidth != 0) ? viewWidth
                        : overscrollStyle.distance, overscrollStyle.distance,
                        overscrollStyle.footer));
            }
        }
        super.applyStylesWithContext(stylerContext);
    }

    /**
     * Set the overscroll distance using reflection. By default, Android sets
     * this distance to zero, and unless we override the ListView class there is
     * no other way to update that value.
     * 
     * @param view
     * @param distance
     */
    private void setOverscrollDistance(ListView view, int distance) {
        try {
            Class<?> c = view.getClass().getSuperclass();
            Field field = c.getDeclaredField("mOverscrollDistance");
            field.setAccessible(true);
            field.setInt(view, distance);
        } catch (NoSuchFieldException e) {
            if (PXLog.isLogging()) {
                PXLog.v(PXOverscrollStyler.class.getSimpleName(), e,
                        "Could not set the mOverscrollDistance");
            }
        } catch (IllegalAccessException e) {
            if (PXLog.isLogging()) {
                PXLog.v(PXOverscrollStyler.class.getSimpleName(), e,
                        "Could not set the mOverscrollDistance");
            }
        }
    }
}
