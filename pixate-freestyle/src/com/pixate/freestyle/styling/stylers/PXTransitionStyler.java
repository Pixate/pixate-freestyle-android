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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.infos.PXAnimationInfo;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationTimingFunction;

// @formatter:off
/**
 *  - transition:
 *  - transition-property: <string>+
 *  - transition-duration: <number>+
 *  - transition-timing-function: <timing-function>+
 *  - transition-delay: <number>+
 */
// @formatter:on
@PXDocStyler(hide=true)
public class PXTransitionStyler extends PXStylerBase {

    private static Map<String, PXDeclarationHandler> declarationHandlers;

    public PXTransitionStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXTransitionStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXDeclarationHandler>(5);

                declarationHandlers.put("transition", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setTransitionInfos(declaration.getTransitionInfoList());
                    }
                });

                declarationHandlers.put("transition-property", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<String> names = declaration.getNameListValue();
                        int count = names.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getTransitionInfoAtIndex(i, stylerContext);
                            String name = names.get(i);

                            info.animationName = name.trim();
                        }
                    }
                });

                declarationHandlers.put("transition-duration", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<Float> timeValues = declaration.getSecondsListValue();
                        int count = timeValues.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getTransitionInfoAtIndex(i, stylerContext);
                            float time = timeValues.get(i);

                            info.animationDuration = time;
                        }
                    }
                });

                declarationHandlers.put("transition-timing-function", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<PXAnimationTimingFunction> timingFunctions = declaration.getAnimationTimingFunctionList();
                        int count = timingFunctions.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getTransitionInfoAtIndex(i, stylerContext);
                            PXAnimationTimingFunction value = timingFunctions.get(i);

                            info.animationTimingFunction = value;
                        }
                    }
                });

                declarationHandlers.put("transition-delay", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<Float> timeValues = declaration.getSecondsListValue();
                        int count = timeValues.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getTransitionInfoAtIndex(i, stylerContext);
                            float time = timeValues.get(i);

                            info.animationDelay = time;
                        }
                    }
                });

            }

            return declarationHandlers;
        }
    }

    @Override
    public void applyStylesWithContext(PXStylerContext stylerContext) {

        // remove invalid transition infos.
        List<PXAnimationInfo> infos = stylerContext.getTransitionInfos();
        List<PXAnimationInfo> toRemove = new ArrayList<PXAnimationInfo>(infos.size());
        PXAnimationInfo currentSettings = new PXAnimationInfo(true);

        for (PXAnimationInfo info : infos) {
            if (info.animationName.length() == 0) {
                // queue up to delete this unnamed animation.
                toRemove.add(info);

            } else {
                // set any undefined values using the latest settings.
                info.setUndefinedProperties(currentSettings);
                currentSettings = info;
            }
        }

        infos.removeAll(toRemove);
        stylerContext.setTransitionInfos(infos);

        // Continue with default behavior.
        super.applyStylesWithContext(stylerContext);
    }

    // Private

    private PXAnimationInfo getTransitionInfoAtIndex(int index, PXStylerContext context) {
        List<PXAnimationInfo> infos = context.getTransitionInfos();

        if (infos == null) {
            infos = new ArrayList<PXAnimationInfo>();
            context.setTransitionInfos(infos);
        }

        while (infos.size() < index) {
            infos.add(new PXAnimationInfo());
        }

        return infos.get(index);
    }

}
