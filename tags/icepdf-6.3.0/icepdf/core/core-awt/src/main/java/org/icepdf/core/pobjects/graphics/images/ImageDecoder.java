/*
 * Copyright 2006-2018 ICEsoft Technologies Canada Corp.
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
package org.icepdf.core.pobjects.graphics.images;

import java.awt.image.BufferedImage;

/**
 * Base decoder class for all image handling formats.
 *
 * @since 6.3
 */
public interface ImageDecoder {

    /**
     * Decodes the image data returning a render
     * able BufferedImage.
     *
     * @return imaged stream decoded to a BufferedImage. Null if the image could not be decoded.
     */
    BufferedImage decode();
}
