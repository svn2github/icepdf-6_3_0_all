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
import java.util.logging.Logger;

/**
 * The ImageStreamReference class is a rudimentary Image Proxy which will
 * try and decode the image data into an Buffered image using a worker thread.
 * The intent is that the content parser will continue parsing the content stream
 * while the worker thread handles the image decode work.  However the drawImage
 * method will block until the worker thread returns.  So generally put not
 * a true image proxy but we do get significantly faster load times with the
 * current implementation.
 *
 * @since 5.0
 */
public class ImageStreamReference extends CachedImageReference implements Runnable {

    private static final Logger logger =
            Logger.getLogger(ImageStreamReference.class.toString());

    protected ImageStreamReference(ImageStream imageStream, Color fillColor, Resources resources) {
        super(imageStream, fillColor, resources);

        // kick off a new thread to load the image, if not already in pool.
        ImagePool imagePool = imageStream.getLibrary().getImagePool();
        if (imagePool.get(reference) == null) {
            imagePool.execute(this);
        }
    }

    @Override
    public int getWidth() {
        return imageStream.getWidth();
    }

    @Override
    public int getHeight() {
        return imageStream.getHeight();
    }

    public void run() {
        lock.lock();
        try {
            image = imageStream.getImage(fillColor, resources);
        } catch (Throwable e) {
            logger.warning("Error loading image: " + imageStream.getPObjectReference() +
                    " " + imageStream.toString());
        } finally {
            lock.unlock();
        }

    }
}
