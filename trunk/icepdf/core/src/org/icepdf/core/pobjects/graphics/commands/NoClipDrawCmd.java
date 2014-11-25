/*
 * Copyright 2006-2014 ICEsoft Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.icepdf.core.pobjects.graphics.commands;

import org.icepdf.core.pobjects.Page;
import org.icepdf.core.pobjects.graphics.OptionalContentState;
import org.icepdf.core.pobjects.graphics.PaintTimer;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * NoClipDrawCmd when execute will apply a no clip operation to the Graphics2D
 * context.  A no clips operation will restore the graphics clip back to the
 * original clip which is the page size but the base Affine transform must
 * be first applied and then backed out after the clip has been removed.
 *
 * @since 5.0
 */
public class NoClipDrawCmd extends AbstractDrawCmd {

    @Override
    public Shape paintOperand(Graphics2D g, Page parentPage, Shape currentShape,
                              Shape clip, AffineTransform base,
                              OptionalContentState optionalContentState, boolean paintAlpha, PaintTimer paintTimer) {

        AffineTransform af = new AffineTransform(g.getTransform());

        g.setTransform(base);
//        if (!g.getClip().getBounds().equals(clip.getBounds())) {
        g.setClip(clip);
//        }
        g.setTransform(af);
        return currentShape;
    }
}
