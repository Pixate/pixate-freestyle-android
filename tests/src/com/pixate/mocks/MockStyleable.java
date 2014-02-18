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
package com.pixate.mocks;

import java.util.HashMap;

import com.pixate.pxengine.styling.stylers.PXStylerBase.PXStylerInvocation;

/**
 * For simple tests to see if, for example, a property value has been set by a
 * {@link PXStylerInvocation#invoke(Object, com.pixate.pxengine.styling.stylers.PXStyler, com.pixate.pxengine.styling.stylers.PXStylerContext)}
 * call.
 */
public class MockStyleable extends HashMap<String, Object> {
    private static final long serialVersionUID = 8894641672108204992L;
}
