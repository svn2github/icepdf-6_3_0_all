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
package org.icepdf.core.util;

public class PropertyConstants {

    // property change event names

    public final static String

            DOCUMENT_CURRENT_PAGE = "documentCurrentPage",

    DOCUMENT_TOOL_PAN = "documentToolRotation",
            DOCUMENT_TOOL_ZOOM_IN = "documentToolZoomIn",
            DOCUMENT_TOOL_ZOOM_OUT = "documentToolZoomOut",
//           DOCUMENT_TOOL_DYNAMIC_ROTATION   = "documentToolDynamicRotation",
//           DOCUMENT_TOOL_DYNAMIC_ZOOM       = "documentToolDynamicZoom",

    DOCUMENT_TOOL_NONE = "documentToolNone",
            DOCUMENT_TOOL_TEXT_SELECTION = "documentToolTextSelect",
            DOCUMENT_TOOL_ANNOTATION_SELECTION = "documentToolAnnotationSelect",

    DOCUMENT_INITIALIZING_PAGE = "documentPageInitialization",
            DOCUMENT_PAINTING_PAGE = "documentPagePainting",

    TEXT_DESELECTED = "textDeselected",
            TEXT_SELECTED = "textSelected",
            TEXT_SELECT_ALL = "textSelectAll",

    ANNOTATION_SELECTED = "annotationSelected",
            ANNOTATION_DESELECTED = "annotationDeselected",

    ANNOTATION_DELETED = "annotationDeleted",

    ANNOTATION_BOUNDS = "annotationBounds",

    ANNOTATION_FOCUS_GAINED = "annotationFocusGained",
            ANNOTATION_FOCUS_LOST = "annotationFocusLost",

    // not in use
    ANNOTATION_NEW_LINK = "annotationNewLink";


}