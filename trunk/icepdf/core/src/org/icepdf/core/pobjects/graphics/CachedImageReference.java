/*
 * Copyright 2006-2012 ICEsoft Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.icepdf.core.pobjects.graphics;

import org.icepdf.core.pobjects.ImageStream;
import org.icepdf.core.pobjects.Resources;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * the Abstract CachedImageReference stores the decoded BufferedImage data in
 * an ImagePool referenced by the images PDF object number to insure that if
 * a page is garbage collected the image can re fetched from the pool if
 * necessary.
 *
 * @since 5.0
 */
public abstract class CachedImageReference extends ImageReference {

    private ImagePool imagePool;
    private boolean isNull;

    protected CachedImageReference(ImageStream imageStream, Color fillColor, Resources resources) {
        super(imageStream, fillColor, resources);
        imagePool = imageStream.getLibrary().getImagePool();
        this.reference = imageStream.getPObjectReference();
    }

    public BufferedImage getImage() {
        if (isNull) {
            return null;
        }
        BufferedImage cached = imagePool.get(reference);
        if (cached != null) {
            return cached;
        } else {
            BufferedImage im = createImage();
            if (im != null) {
                imagePool.put(reference, im);
            } else {
                isNull = true;
            }
            return im;
        }
    }

}
