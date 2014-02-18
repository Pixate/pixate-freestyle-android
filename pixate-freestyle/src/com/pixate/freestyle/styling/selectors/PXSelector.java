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

import com.pixate.freestyle.parsing.PXSourceEmitter;
import com.pixate.freestyle.parsing.PXSourceWriter;
import com.pixate.freestyle.styling.selectors.PXSpecificity.PXSpecificityType;

/**
 * The PXSelector defines a method used to determine if a given object matches a
 * specific selector expression as captured by the class that conforms to this
 * class.
 */
public abstract class PXSelector implements PXSourceEmitter {

    protected PXSpecificityType specificityType;

    /**
     * Constructs a new PXSelector with a given {@link PXSpecificityType}
     * 
     * @param specificityType
     */
    protected PXSelector(PXSpecificityType specificityType) {
        this.specificityType = specificityType;
    }

    /**
     * Update the specified PXSpecificity instance as is appropriate for the
     * class that conforms to this protocol
     */
    public void incrementSpecificity(PXSpecificity specificity) {
        if (specificity != null) {
            specificity.incrementSpecifity(specificityType);
        }
    }

    /**
     * Outputs a description of this selector to a source writer. The default
     * implementation does nothing. Subclasses should overwrite when needed.
     * 
     * @param writer
     */
    public void getSourceWithSourceWriter(PXSourceWriter writer) {
        // Np-op here. Subclasses may overwrite.
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.parsing.PXSourceEmitter#getSource()
     */
    public String getSource() {
        PXSourceWriter writer = new PXSourceWriter();
        getSourceWithSourceWriter(writer);
        return writer.toString();
    }

    /**
     * Determine if the specified element matches this PXSelector
     * 
     * @param element
     */
    public abstract boolean matches(Object element);
}
