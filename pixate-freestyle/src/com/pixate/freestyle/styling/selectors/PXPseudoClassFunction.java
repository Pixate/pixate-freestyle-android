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

import java.util.ArrayList;
import java.util.List;

import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.PXStyleUtils.PXStyleableChildrenInfo;
import com.pixate.freestyle.styling.selectors.PXSpecificity.PXSpecificityType;
import com.pixate.freestyle.util.CollectionUtil;
import com.pixate.freestyle.util.PXLog;

/**
 * A PXPseudoClassFunction is used to select styleables based on their positions
 * or pattern of positions from the start or end of their list of siblings
 */
public class PXPseudoClassFunction extends PXSelector {

    /**
     * The PXPseudoClassFunctionType enumeration specifies what nth-child
     * function should be applied
     */
    public enum PXPseudoClassFunctionType {
        NTH_CHILD,
        NTH_LAST_CHILD,
        NTH_OF_TYPE,
        NTH_LAST_OF_TYPE
    };

    private PXPseudoClassFunctionType functionType;
    private int modulus;
    private int remainder;

    /**
     * Initialize a newly allocation PXPseudoClassFunction
     * 
     * @param type The nth-child operator type
     * @param modulus The modulus of the nth-child operation
     * @param remainder The remainder of the nth-child operation
     */
    public PXPseudoClassFunction(PXPseudoClassFunctionType type, int modulus, int remainder) {
        super(PXSpecificityType.CLASS_OR_ATTRIBUTE);
        this.functionType = type;
        this.modulus = modulus;
        this.remainder = remainder;
    }

    public boolean matches(Object element) {
        boolean result = false;
        PXStyleableChildrenInfo info = PXStyleUtils.getChildrenInfoForStyleable(element);
        if (modulus != 0 || remainder != 0) {
            switch (functionType) {
                case NTH_LAST_CHILD:
                    info.childrenIndex = info.childrenCount - info.childrenIndex + 1;
                    // fall through

                case NTH_CHILD: {
                    if (modulus == 1) {
                        result = (info.childrenIndex == remainder);
                    } else {
                        int diff = info.childrenIndex - remainder;
                        int diffMod = (modulus != 0) ? diff % modulus : diff;

                        if ((diff <= 0 && modulus < 0) || (diff >= 0 && modulus > 0)) {
                            result = (diffMod == 0);
                        }
                    }
                    break;
                }

                case NTH_LAST_OF_TYPE:
                    info.childrenOfTypeIndex = info.childrenOfTypeCount - info.childrenOfTypeIndex
                            + 1;
                    // fall through

                case NTH_OF_TYPE: {
                    if (modulus == 1) {
                        result = (info.childrenOfTypeIndex == remainder);
                    } else {
                        int diff = info.childrenOfTypeIndex - remainder;
                        int diffMod = (modulus != 0) ? diff % modulus : diff;

                        if ((diff <= 0 && modulus < 0) || (diff >= 0 && modulus > 0)) {
                            result = (diffMod == 0);
                        }
                    }
                    break;
                }
            }
        }

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXPseudoClassFunction.class.getSimpleName(), "%s matched %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXPseudoClassFunction.class.getSimpleName(), "%s did not match %s",
                        toString(), PXStyleUtils.getDescriptionForStyleable(element));
            }
        }

        return result;
    }

    /**
     * Returns the type of nth-child operation that this selector will perform
     * during matching
     */
    public PXPseudoClassFunctionType getFunctionType() {
        return functionType;
    }

    /**
     * Returns the modulus. In the expression 'an + b', the modulus corresponds
     * to the 'n' value
     */
    public int getModulus() {
        return modulus;
    }

    /**
     * Returns the remainder. In the expression 'an + b', the remainder
     * corresponds to the 'b' value
     */
    public int getRemainder() {
        return remainder;
    }

    @Override
    public String toString() {
        List<String> parts = new ArrayList<String>();

        switch (functionType) {
            case NTH_CHILD:
                parts.add(":nth-child(");
                break;

            case NTH_LAST_CHILD:
                parts.add(":nth-last-child(");
                break;

            case NTH_OF_TYPE:
                parts.add(":nth-of-type(");
                break;

            case NTH_LAST_OF_TYPE:
                parts.add(":nth-last-of-type(");
                break;
        }

        if (modulus == 0) {
            parts.add(String.format("%d", remainder));
        } else if (remainder == 0) {
            parts.add(String.format("%dn", modulus));
        } else {
            parts.add(String.format("%dn+%d", modulus, remainder));
        }

        parts.add(")");

        return CollectionUtil.toString(parts, "");
    }
}
