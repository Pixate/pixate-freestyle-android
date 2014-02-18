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
package com.pixate.freestyle.styling.selectors;

import com.pixate.freestyle.parsing.PXSourceWriter;
import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.selectors.PXSpecificity.PXSpecificityType;
import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.StringUtil;

public class PXClassSelector extends PXSelector {

    private String className;
    private boolean canMatch;

    public PXClassSelector(String name) {
        super(PXSpecificityType.CLASS_OR_ATTRIBUTE);
        if (name != null) {
            this.className = name.replaceAll("\\\\.", ".");
            this.canMatch = !(PXStyleUtils.PATTERN_WHITESPACE.matcher(name).find());
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className != null ? className.replaceAll("\\\\.", ".") : null;
    }

    // Overrides

    @Override
    public boolean matches(Object element) {

        boolean result = false;
        if (canMatch && element != null && !StringUtil.isEmpty(className)) {
            String classValue = PXStyleAdapter.getStyleAdapter(element).getStyleClass(element);

            if (!StringUtil.isEmpty(classValue)) {
                String[] components = PXStyleUtils.PATTERN_WHITESPACE_PLUS.split(classValue);
                result = StringUtil.contains(components, this.className);
            }
        }

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXClassSelector.class.getSimpleName(), "%s matched %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXClassSelector.class.getSimpleName(), "%s did not match %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.selectors.PXSelector#sourceWithSourceWriter
     * (com.pixate.freestyle.parsing.PXSourceWriter)
     */
    @Override
    public void getSourceWithSourceWriter(PXSourceWriter writer) {
        writer.printIndent();
        writer.print("(CLASS ");
        writer.print(className);
        writer.print(")");
    }

    @Override
    public String toString() {
        return String.format(".%s", className);
    }

}
