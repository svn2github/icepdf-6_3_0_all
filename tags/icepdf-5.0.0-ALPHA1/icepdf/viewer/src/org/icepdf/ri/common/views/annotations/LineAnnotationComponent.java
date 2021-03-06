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
package org.icepdf.ri.common.views.annotations;

import org.icepdf.core.pobjects.annotations.Annotation;
import org.icepdf.core.pobjects.annotations.LineAnnotation;
import org.icepdf.ri.common.views.AbstractPageViewComponent;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.common.views.DocumentViewModel;

import java.awt.*;

/**
 * The LineAnnotationComponent encapsulates a LineAnnotation objects.  It
 * also provides basic editing functionality such as resizing, moving and change
 * the border color and style.  The start and end line cab can also be changed
 * to one of the name types defined in the LineAnnotation class.
 * <p/>
 * The Viewer RI implementation contains a LineAnnotationPanel class which
 * can edit the various properties of this component.
 *
 * @see org.icepdf.ri.common.utility.annotation.LineAnnotationPanel
 * @since 5.0
 */
public class LineAnnotationComponent extends MarkupAnnotationComponent {


    public LineAnnotationComponent(Annotation annotation, DocumentViewController documentViewController,
                                   AbstractPageViewComponent pageViewComponent, DocumentViewModel documentViewModel) {
        super(annotation, documentViewController, pageViewComponent, documentViewModel);

        isEditable = true;
        isRollover = false;
        isMovable = true;
        isResizable = false;
        isShowInvisibleBorder = false;
    }

    @Override
    public void paintComponent(Graphics g) {

    }

    @Override
    public void resetAppearanceShapes() {
        LineAnnotation textMarkupAnnotation = (LineAnnotation) annotation;
        textMarkupAnnotation.resetAppearanceStream();
    }
}
