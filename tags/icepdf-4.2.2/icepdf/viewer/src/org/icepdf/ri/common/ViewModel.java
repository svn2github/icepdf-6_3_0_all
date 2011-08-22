/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEpdf 3.0 open source software code, released
 * May 1st, 2009. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2011 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */
package org.icepdf.ri.common;

import java.io.File;

/**
 * Data model for the view, which maintains state on how a Document is being
 * presented to the user.
 * <p/>
 * The default value of isShrinkToPrintableArea is true.
 *
 * @author Mark Collette
 * @since 2.0
 */
public class ViewModel {

    // Store current directory path
    private static File defaultFile = null;

    // Store current URL path
    private static String defaultURL = null;

    // store for shrink to fit setting for SwingController prints.
    private boolean isShrinkToPrintableArea = true;

    private PrintHelper printHelper;

    static File getDefaultFile() {
        return defaultFile;
    }

    public static String getDefaultFilePath() {
        if (defaultFile == null)
            return null;
        return defaultFile.getAbsolutePath();
    }

    public static String getDefaultURL() {
        return defaultURL;
    }

    static void setDefaultFile(File f) {
        defaultFile = f;
    }

    public static void setDefaultFilePath(String defFilePath) {
        if (defFilePath == null || defFilePath.length() == 0)
            defaultFile = null;
        else
            defaultFile = new File(defFilePath);
    }

    public static void setDefaultURL(String defURL) {
        if (defURL == null || defURL.length() == 0)
            defaultURL = null;
        else
            defaultURL = defURL;
    }

    public PrintHelper getPrintHelper() {
        return printHelper;
    }

    public void setPrintHelper(PrintHelper printHelper) {
        this.printHelper = printHelper;
    }

    /**
     * Indicates the currently stored state of the shrink to fit printable area
     * property.
     *
     * @return true, to enable shrink to fit printable area;
     *         false, otherwise.
     */
    public boolean isShrinkToPrintableArea() {
        return isShrinkToPrintableArea;
    }

    /**
     * Can be set before a SwingController.print() is called to enable/disable
     * shrink to fit printable area.
     *
     * @param shrinkToPrintableArea true, to enable shrink to fit printable area;
     *                              false, otherwise.
     */
    public void setShrinkToPrintableArea(boolean shrinkToPrintableArea) {
        isShrinkToPrintableArea = shrinkToPrintableArea;
    }
}
