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
package com.pixate.freestyle.styling.media;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.pixate.freestyle.util.StringUtil;

public class PXMediaExpressionGroup implements PXMediaExpression {

    private List<PXMediaExpression> expressions;

    public void addExpression(PXMediaExpression expression) {
        if (expression != null) {
            if (expressions == null) {
                expressions = new ArrayList<PXMediaExpression>();
            }
            expressions.add(expression);
        }
    }

    public List<PXMediaExpression> getExpressions() {
        return expressions;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.media.PXMediaExpression#matches(android.content
     * .Context)
     */
    public boolean matches(Context context) {
        for (PXMediaExpression expression : expressions) {
            if (!expression.matches(context)) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String[] parts = new String[expressions.size()];

        for (int i = 0; i < expressions.size(); i++) {
            parts[i] = expressions.get(i).toString();
        }

        return StringUtil.join(parts, " and ");
    }
}
