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
 * The Original Code is ICEpdf 4.1 open source software code, released
 * May 1st, 2009. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2010 ICEsoft Technologies Canada, Corp. All Rights Reserved.
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
package org.icepdf.core.pobjects.actions;

import org.icepdf.core.pobjects.LiteralStringObject;
import org.icepdf.core.pobjects.Name;
import org.icepdf.core.pobjects.StringObject;
import org.icepdf.core.util.Library;

import java.util.Hashtable;

/**
 * <p>The launch action launches an applicaiton or opens or prints a
 * document.</p>
 * <p/>
 * <p>There are optional Win which allow for platform specific parameters for
 * launching the designated application. </p>
 *
 * @author ICEsoft Technologies, Inc.
 * @since 2.6
 */
public class LaunchAction extends Action {

    // file specification or file name.
    public static final Name FILE_KEY = new Name("F");
    // windows specific files properties.
    public static final Name WIN_KEY = new Name("Win");
    // mac specific file properties.
    public static final Name MAC_KEY = new Name("Mac");
    // unix specific file properties.
    public static final Name UNIX_KEY = new Name("Unix");
    // if false the destination document replaces the current.
    public static final Name NEW_WINDOW_KEY = new Name("NewWindow");

    // path to external file, see section 3.10.1 for more details on
    // resolving paths
    private String externalFile;
    private FileSpecification fileSpecification;

    // new window?
    private Boolean isNewWindow;

    // launch parameters specific to Windows.
    private WindowsLaunchParameters winLaunchParameters;

    // mac and unix are not defined by the specification and thus not here
    // either. 

    /**
     * Creates a new instance of a Action.
     *
     * @param l document library.
     * @param h Action dictionary entries.
     */
    public LaunchAction(Library l, Hashtable h) {
        super(l, h);
        winLaunchParameters = new WindowsLaunchParameters();
    }

    /**
     * Gets the applicaiton to be launched or the document to be opened or
     * printed.  This value can either come from the F key entry of the
     * launch action or the F key of the file specification.
     *
     * @return file specification
     */
    public String getExternalFile() {
        Object value = getObject(FILE_KEY);
        if (value instanceof StringObject) {
            externalFile = ((StringObject) value).getDecryptedLiteralString(
                    library.getSecurityManager());
        } else if (getFileSpecification() != null) {
            externalFile = getFileSpecification().getFileSpecification();
        }
        return externalFile;
    }

    /**
     * Sets the external file flag of the action.  At this time it is not
     * possible to set a FileSpecification object but could be added at a later
     * date if deamed necessary.
     *
     * @param externalFile external file path and or name to be associated
     *                     with this launch action.
     */
    public void setExternalFile(String externalFile) {
        StringObject tmp = new LiteralStringObject(
                externalFile, getPObjectReference(), library.securityManager);
        entries.put(FILE_KEY, tmp);
        this.externalFile = externalFile;
    }

    /**
     * Specifies whether or not a new window should be opend.
     *
     * @return true indicates a new window should be used, false otherwise.
     */
    public boolean getNewWindow() {
        Object value = getObject(NEW_WINDOW_KEY);
        if (value instanceof Boolean) {
            isNewWindow = (Boolean) value;
        }
        return isNewWindow;
    }

    /**
     * Gets an object which hold the windows-specific launch parameters.
     *
     * @return window specific launch parameters.
     */
    public WindowsLaunchParameters getWinLaunchParameters() {
        return winLaunchParameters;
    }

    /**
     * Gets the file specification of the destination file.  This objects should
     * be interigated to deside what should be done
     *
     * @return file specification, maybe nukll if external file was specified.
     */
    public FileSpecification getFileSpecification() {
        Object value = getObject(FILE_KEY);
        if (value instanceof Hashtable) {
            fileSpecification = new FileSpecification(library, (Hashtable) value);
        }
        return fileSpecification;
    }

    /**
     * <p>Paramaters specific to launching files on windows.  These parameters
     * specify what application should load the file as well what any special
     * load commands.</p>
     *
     * @since 2.6
     */
    public class WindowsLaunchParameters {

        private final Name FILE_KEY = new Name("F");
        private final Name DIRECTORY_KEY = new Name("D");
        private final Name OPEN_KEY = new Name("O");
        private final Name PARAMETER_KEY = new Name("P");

        private FileSpecification launchFileSpecification;

        private String launchFile;

        // default directory in standard dos syntax
        private String defaultDirectory;

        // open or print
        private String operation;

        // launch parameters
        private String parameters;

        /**
         * Creates a new instance of a Action.
         */
        public WindowsLaunchParameters() {

//            Hashtable winLaunch = library.getDictionary(entries, "Win");
            Object value = getObject(FILE_KEY);
            if (value instanceof Hashtable) {
                launchFileSpecification = new FileSpecification(library,
                        (Hashtable)value);
            } else if (value instanceof StringObject) {
                launchFile = ((StringObject) value).getDecryptedLiteralString(
                        library.getSecurityManager());
            }
            value = getObject(DIRECTORY_KEY);
            if (value instanceof StringObject) {
                defaultDirectory = ((StringObject) value)
                        .getDecryptedLiteralString(library.getSecurityManager());
            }
            value = getObject(OPEN_KEY);
            if (value instanceof StringObject) {
                operation = ((StringObject) value)
                        .getDecryptedLiteralString(library.getSecurityManager());
            }
            value = getObject(PARAMETER_KEY);
            if (value instanceof StringObject) {
                parameters =
                        ((StringObject) value)
                                .getLiteralString();
            }
        }

        /**
         * Gets the file name of the application to be launched or the document
         * to be opened or printed, in standard Windows pathname format.
         *
         * @return fiel or application to launch
         */
        public String getLaunchFile() {
            return launchFile;
        }

        /**
         * Gets a string specifying the default directory in standard DOS
         * syntax(Optional).
         *
         * @return default directory.
         */
        public String getDefaultDirectory() {
            return defaultDirectory;
        }

        /**
         * Indicates the operation to perform (Optional).
         *
         * @return opertation to perform, either "open" or "print".
         */
        public String getOperation() {
            return operation;
        }

        /**
         * Gets a parameter string to be passed to the application designated by
         * the fileName entry.(Optional).
         *
         * @return paramater string associated with this action
         */
        public String getParameters() {
            return parameters;
        }

        /**
         * Gets the file specification of the destination file.  This objects should
         * be interigated to deside what should be done
         *
         * @return file specification, maybe nukll if external file was specified.
         */
        public FileSpecification getLaunchFileSpecification() {
            return launchFileSpecification;
        }
    }
}
