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
package com.pixate.freestyle.styling.animation;

import java.util.ArrayList;
import java.util.List;

import com.pixate.freestyle.parsing.PXSourceWriter;

public class PXKeyframe {

    private String name;
    private List<PXKeyframeBlock> blocks;

    public PXKeyframe(String name) {
        this.name = name;
    }

    public void addKeyframeBlock(PXKeyframeBlock block) {
        if (blocks == null) {
            blocks = new ArrayList<PXKeyframeBlock>();
        }
        blocks.add(block);
    }

    public List<PXKeyframeBlock> getBlocks() {
        return blocks != null ? new ArrayList<PXKeyframeBlock>(blocks) : null;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        PXSourceWriter writer = new PXSourceWriter();
        writer.print(String.format("@keyframes %s ", name));
        writer.println("{");
        if (blocks != null) {
            for (PXKeyframeBlock block : blocks) {
                writer.println(block.toString());
            }
        }
        writer.print("}");
        return writer.toString();
    }

}
