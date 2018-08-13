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

import org.icepdf.core.pobjects.graphics.GraphicsState;

import java.awt.image.BufferedImage;

public abstract class AbstractImageDecoder implements ImageDecoder {

    protected ImageStream imageStream;
    protected GraphicsState graphicsState;

    public AbstractImageDecoder(ImageStream imageStream, GraphicsState graphicsState) {
        this.imageStream = imageStream;
        this.graphicsState = graphicsState;
    }

    @Override
    public abstract BufferedImage decode();

    public ImageStream getImageStream() {
        return imageStream;
    }
}
