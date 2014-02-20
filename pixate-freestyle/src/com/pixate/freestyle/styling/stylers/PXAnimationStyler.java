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

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.infos.PXAnimationInfo;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationDirection;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationFillMode;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationPlayState;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationTimingFunction;

// @formatter:off
/**
 *  - animation:
 *  - animation-name: <name>+
 *  - animation-duration: <time>+
 *  - animation-timing-function: <timing-function>+
 *  - animation-iteration-count: <number>+
 *  - animation-direction: <direction>+
 *  - animation-play-state: <play-state>+
 *  - animation-delay: <number>+
 *  - animation-fill-mode: <fill-mode>+
 */
// @formatter:on
@PXDocStyler(hide=true, properties = { @PXDocProperty(name = "animation", syntax = "<single-animation>#"),
        @PXDocProperty(name = "animation-name", syntax = "<name>#"),
        @PXDocProperty(name = "animation-duration", syntax = "<time>#"),
        @PXDocProperty(name = "animation-timing-function", syntax = "<timing-function>#"),
        @PXDocProperty(name = "animation-iteration-count", syntax = "<number>#"),
        @PXDocProperty(name = "animation-direction", syntax = "<direction>#"),
        @PXDocProperty(name = "animation-play-state", syntax = "<play-state>#"),
        @PXDocProperty(name = "animation-delay", syntax = "<number>#"),
        @PXDocProperty(name = "animation-fill-mode", syntax = "<fill-mode>#"), })
public class PXAnimationStyler extends PXStylerBase {

    private static PXAnimationStyler instance;
    private static Map<String, PXDeclarationHandler> declarationHandlers;

    public PXAnimationStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    public synchronized static PXAnimationStyler getInstance() {
        if (instance == null) {
            instance = new PXAnimationStyler(null);
        }
        return instance;
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXAnimationStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>();

                declarationHandlers.put("animation", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setAnimationInfos(declaration.getAnimationInfoList());
                    }
                });

                declarationHandlers.put("animation-name", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<String> names = declaration.getNameListValue();
                        int count = names.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getAnimationInfoAtIndex(i, stylerContext);
                            String name = names.get(i);

                            info.animationName = name.trim();
                        }
                    }
                });

                declarationHandlers.put("animation-duration", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<Float> timeValues = declaration.getSecondsListValue();
                        int count = timeValues.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getAnimationInfoAtIndex(i, stylerContext);
                            float time = timeValues.get(i);

                            info.animationDuration = time;
                        }
                    }
                });

                declarationHandlers.put("animation-timing-function", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<PXAnimationTimingFunction> timingFunctions = declaration
                                .getAnimationTimingFunctionList();
                        int count = timingFunctions.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getAnimationInfoAtIndex(i, stylerContext);
                            info.animationTimingFunction = timingFunctions.get(i);
                        }
                    }
                });

                declarationHandlers.put("animation-iteration-count", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<Float> countValues = declaration.getFloatListValue();
                        int count = countValues.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getAnimationInfoAtIndex(i, stylerContext);
                            info.animationDuration = countValues.get(i);
                        }
                    }
                });

                declarationHandlers.put("animation-direction", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<PXAnimationDirection> values = declaration.getAnimationDirectionList();
                        int count = values.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getAnimationInfoAtIndex(i, stylerContext);
                            info.animationDirection = values.get(i);
                        }
                    }
                });

                declarationHandlers.put("animation-play-state", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<PXAnimationPlayState> values = declaration.getAnimationPlayStateList();
                        int count = values.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getAnimationInfoAtIndex(i, stylerContext);
                            info.animationPlayState = values.get(i);
                        }
                    }
                });

                declarationHandlers.put("animation-delay", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<Float> timeValues = declaration.getSecondsListValue();
                        int count = timeValues.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getAnimationInfoAtIndex(i, stylerContext);
                            float time = timeValues.get(i);

                            info.animationDelay = time;
                        }
                    }
                });

                declarationHandlers.put("animation-fill-mode", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<PXAnimationFillMode> values = declaration.getAnimationFillModeList();
                        int count = values.size();

                        for (int i = 0; i < count; i++) {
                            PXAnimationInfo info = getAnimationInfoAtIndex(i, stylerContext);
                            info.animationFillMode = values.get(i);
                        }
                    }
                });

            }

            return declarationHandlers;
        }
    }

    @Override
    public void applyStylesWithContext(PXStylerContext stylerContext) {
        // remove invalid animation infos.
        List<PXAnimationInfo> infos = stylerContext.getAnimationInfos();
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
        stylerContext.setAnimationInfos(infos);

        // @formatter:off
        //if (stylerInvocation != null) {
            // Proceed with default behavior
            super.applyStylesWithContext(stylerContext);
            
        //} else {
            // TODO animation stuff.  PXKeyframe, PXKeyframeAnimation etc., if needed.
            
        //}
        // @formatter:on
    }

    // Private

    private PXAnimationInfo getAnimationInfoAtIndex(int index, PXStylerContext context) {
        List<PXAnimationInfo> infos = context.getAnimationInfos();

        if (infos == null) {
            infos = new ArrayList<PXAnimationInfo>();
            context.setAnimationInfos(infos);
        }

        while (infos.size() < index) {
            infos.add(new PXAnimationInfo());
        }

        return infos.get(index);
    }

}
