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


import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;

import javax.swing.*;


/**
 * The <code>ViewerComponentExample</code> class is an example of how to use
 * <code>SwingController</code> and <code>SwingViewBuilder</code>
 * to build a PDF viewer component.  A file specified at the command line is
 * opened in a JFrame which contains the viewer component.
 *
 * @since 2.0
 */
public class ViewerComponentExample {
    public static void main(String[] args) {
        // Get a file from the command line to open
        String filePath = args[0];

        // build a component controller
        SwingController controller = new SwingController();
        controller.setIsEmbeddedComponent(true);

        // set the viewController embeddable flag.
        org.icepdf.core.views.DocumentViewController viewController =
                controller.getDocumentViewController();

        JPanel viewerComponentPanel = new JPanel();
        viewerComponentPanel.add(viewController.getViewContainer());

        // add copy keyboard command
        ComponentKeyBinding.install(controller, viewerComponentPanel);

        // add interactive mouse link annotation support via callback
        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                        controller.getDocumentViewController()));

        // build a containing JFrame for display
        JFrame applicationFrame = new JFrame();
        applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applicationFrame.getContentPane().add(viewerComponentPanel);

        // Now that the GUI is all in place, we can try openning a PDF
        controller.openDocument(filePath);

        // hard set the page view to single page which effectively give a single
        // page view. This should be done after openDocument as it has code that
        // can change the view mode if specified by the file.
        controller.setPageViewMode(
                DocumentViewControllerImpl.ONE_PAGE_VIEW,
                false);

        // show the component
        applicationFrame.pack();
        applicationFrame.setVisible(true);
    }
}
