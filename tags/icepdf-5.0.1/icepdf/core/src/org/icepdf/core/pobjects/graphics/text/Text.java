/*
 * Copyright 2006-2013 ICEsoft Technologies Inc.
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
package org.icepdf.core.pobjects.graphics.text;

import java.awt.geom.Rectangle2D;

/**
 * Sprite interface which is base for all text sprite types.  Mainly line,
 * text, word and glyph.  They are used for managing text extraction.
 *
 * @since 4.0
 */
public interface Text {

    public Rectangle2D.Float getBounds();

    public boolean isHighlighted();

    public boolean isSelected();

    public void setHighlighted(boolean highlight);

    public void setSelected(boolean selected);

    public boolean hasHighligh();

    public boolean hasSelected();

    public void setHasHighlight(boolean hasHighlight);

    public void setHasSelected(boolean hasSelected);

}
