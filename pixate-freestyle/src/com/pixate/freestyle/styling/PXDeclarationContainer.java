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
package com.pixate.freestyle.styling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PXDeclarationContainer {

    protected List<PXDeclaration> declarations;
    protected Set<String> names;

    /**
     * Add a declaration to the list of declarations associated with this
     * container
     * 
     * @param declaration The declaration to add
     */
    public void addDeclaration(PXDeclaration declaration) {
        if (declaration != null) {
            if (declarations == null) {
                declarations = new ArrayList<PXDeclaration>();
                names = new HashSet<String>();
            }

            // check for dups
            PXDeclaration addedDeclaration = getDeclarationForName(declaration.getName());

            // declarations that come later win, unless the earlier one is
            // important and this new one is not
            if (addedDeclaration != null) {
                if (!addedDeclaration.isImportant() || declaration.isImportant()) {
                    removeDeclaration(addedDeclaration);
                }
            }

            declarations.add(declaration);
            names.add(declaration.getName());
        }
    }

    public List<PXDeclaration> getDeclarations() {
        return declarations == null ? Collections.<PXDeclaration>emptyList() : new ArrayList<PXDeclaration>(
                declarations);
    }

    /**
     * Remove the specified declaration from this container
     */
    public void removeDeclaration(PXDeclaration declaration) {
        if (declaration != null && declarations != null) {
            declarations.remove(declaration);
            names.remove(declaration.getName());
        }
    }

    /**
     * A predicate used to determine if this container contains a declaration
     * for a given property name
     * 
     * @param name The name of the property to look for
     */
    public boolean hasDeclarationForName(String name) {
        return getDeclarationForName(name) != null;
    }

    /**
     * Return the declaration associated with a specified name. null will be
     * returned if the container does not contain a declaration for the given
     * name.
     * 
     * @param name The name of the property to return
     */
    public PXDeclaration getDeclarationForName(String name) {

        if (name != null && declarations != null) {
            for (PXDeclaration declaration : declarations) {
                if (name.equals(declaration.getName())) {
                    return declaration;
                }
            }
        }
        return null;
    }

}
