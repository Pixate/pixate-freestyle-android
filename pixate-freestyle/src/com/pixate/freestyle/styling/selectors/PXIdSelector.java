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

public class PXIdSelector extends PXSelector {

    private String attributeValue;

    public PXIdSelector(String value) {
        super(PXSpecificityType.ID);
        this.attributeValue = value;
    }

    public String getIdValue() {
        return attributeValue;
    }

    @Override
    public boolean matches(Object element) {
        boolean result = false;
        if (element != null && attributeValue != null) {
            result = attributeValue.equals(PXStyleAdapter.getStyleAdapter(element).getStyleId(
                    element));
        }

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXIdSelector.class.getSimpleName(), "%s matched %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXIdSelector.class.getSimpleName(), "%s did not match %s", toString(),
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
        writer.print("(ID #");
        writer.print(attributeValue);
        writer.print(")");
        writer.println();
    }

    @Override
    public String toString() {
        return '#' + attributeValue;
    }
}
