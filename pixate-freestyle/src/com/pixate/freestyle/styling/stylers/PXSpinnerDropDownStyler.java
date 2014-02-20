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

import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.os.Build;
import android.widget.Spinner;

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.util.PXLog;

// @formatter:off
/**
 * - vertical-offset: length
 * - horizontal-offset: length
 * - width: length
 */
// @formatter:on
@PXDocStyler(properties = { @PXDocProperty(name = "vertical-offset", syntax = "<length>"),
        @PXDocProperty(name = "horizontal-offset", syntax = "<length>"),
        @PXDocProperty(name = "width", syntax = "<length>"), })
public class PXSpinnerDropDownStyler extends PXStylerBase {

    private static String TAG = PXSpinnerDropDownStyler.class.getSimpleName();
    private static Map<String, PXDeclarationHandler> handlers;
    private static PXSpinnerDropDownStyler instance;

    public synchronized static PXSpinnerDropDownStyler getInstance() {
        if (instance == null) {
            instance = new PXSpinnerDropDownStyler(null);
        }
        return instance;
    }

    public PXSpinnerDropDownStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXSpinnerDropDownStyler.class) {
            if (handlers == null) {
                handlers = new HashMap<String, PXDeclarationHandler>(3);

                handlers.put("vertical-offset", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            setDropDownVerticalOffset((Spinner) stylerContext.getStyleable(),
                                    (int) declaration.getFloatValue(stylerContext
                                            .getDisplayMetrics()));
                        } else {
                            if (PXLog.isLogging()) {
                                PXLog.w(TAG, "Spinner drop-down 'vertical-offset' requires API 16");
                            }
                        }
                    }
                });

                handlers.put("horizontal-offset", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            setDropDownHorizontalOffset((Spinner) stylerContext.getStyleable(),
                                    (int) declaration.getFloatValue(stylerContext
                                            .getDisplayMetrics()));
                        } else {
                            if (PXLog.isLogging()) {
                                PXLog.w(TAG,
                                        "Spinner drop-down 'horizontal-offset' requires API 16");
                            }
                        }
                    }
                });

                handlers.put("width", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            setDropDownWidth((Spinner) stylerContext.getStyleable(),
                                    (int) declaration.getFloatValue(stylerContext
                                            .getDisplayMetrics()));
                        } else {
                            if (PXLog.isLogging()) {
                                PXLog.w(TAG, "Spinner drop-down 'width' requires API 16");
                            }
                        }
                    }
                });
            }
            return handlers;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void setDropDownWidth(Spinner spinner, int pixels) {
        spinner.setDropDownWidth(pixels);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void setDropDownHorizontalOffset(Spinner spinner, int pixels) {
        spinner.setDropDownHorizontalOffset(pixels);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void setDropDownVerticalOffset(Spinner spinner, int pixels) {
        spinner.setDropDownVerticalOffset(pixels);
    }
}
