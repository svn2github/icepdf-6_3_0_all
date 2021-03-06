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
 * 2004-2009 ICEsoft Technologies Canada, Corp. All Rights Reserved.
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
package org.icepdf.core.pobjects;

import org.icepdf.core.util.Library;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>The <code>Destination</code> class defines a particular view of a
 * PDF document consisting of the following items:</p>
 * <ul>
 * <li>The page of the document to be displayed.</li>
 * <li>The location of the document window on that page.</li>
 * <li>The magnification (zoom) factor to use when displaying the page
 * Destinations may be associated with outline items, annotations,
 * or actions. </li>
 * </ul>
 * <p>Destination can be associated with outline items, annotations, or actions.
 * In each case the destination specifies the view of the document to be presented
 * when one of the respective objects is activated.</p>
 * <p>The Destination class currently only supports the Destination syntaxes,
 * [page /XYZ left top zoom], other syntax will be added in future releases. The
 * syntax [page /XYZ left top zoom] is defined as follows:</p>
 * <ul>
 * <li>page - designated page to show (Reference to a page).</li>
 * <li>/XYZ - named format of destination syntax.</li>
 * <li>left - x value of the upper left-and coordinate.</li>
 * <li>top - y value of the upper left-hand coordinate.</li>
 * <li>zoom - zoom factor to magnify page by.</li>
 * </ul>
 * <p>A null value for left, top or zoom specifies that the current view values
 * will be unchanged when navigating to the specified page. </p>
 *
 * @see org.icepdf.core.pobjects.annotations.Annotation
 * @see org.icepdf.core.pobjects.OutlineItem
 * @see org.icepdf.core.pobjects.actions.Action
 * @since 1.0
 */
public class Destination {

    private static final Logger logger =
            Logger.getLogger(Destination.class.toString());

    // Vector destination type formats.
    public static final Name TYPE_XYZ = new Name("XYZ");
    public static final Name TYPE_FIT = new Name("Fit");
    public static final Name TYPE_FITH = new Name("FitH");
    public static final Name TYPE_FITV = new Name("FitV");
    public static final Name TYPE_FITR = new Name("FitR");
    public static final Name TYPE_FITB = new Name("FitB");
    public static final Name TYPE_FITBH = new Name("FitBH");
    public static final Name TYPE_FITBV = new Name("FitBV");

    // library of all PDF document objects
    private Library library;

    // object containing all of the destinations parameters
    private Object object;

    // Reference object for destination
    private Reference ref;

    // type, /XYZ, /Fit, /FitH... 
    private Name type;

    // Specified by /XYZ in the core, /(left)(top)(zoom)
    private Float left = null;
    private Float bottom = null;
    private Float right = null;
    private Float top = null;
    private Float zoom = null;

    // named Destination name, can be a name or String
    private Name namedDestination;

    // initiated flag
    private boolean inited;

    /**
     * Creates a new instance of a Destination.
     *
     * @param l document library.
     * @param h Destination dictionary entries.
     */
    public Destination(Library l, Object h) {
        library = l;
        object = h;
        init();
    }

    /**
     * Initiate the Destination. Retrieve any needed attributes.
     */
    private void init() {

        // check for initiation
        if (inited) {
            return;
        }
        inited = true;

        // if vector we have found /XYZ
        if (object instanceof Vector) {
            parse((Vector) object);
        }

        // find named Destinations, this however is incomplete
        // @see #parser for more detailed information
        else if (object instanceof Name || object instanceof StringObject) {
            String s;
            // Make sure to decrypt this attribute
            if (object instanceof StringObject) {
                StringObject stringObject = (StringObject) object;
                s = stringObject.getDecryptedLiteralString(library.securityManager);
            } else {
                s = object.toString();
            }

            // store the name
            namedDestination = new Name(s);

            boolean found = false;
            Catalog catalog = library.getCatalog();
            if (catalog != null) {
                NameTree nameTree = catalog.getNameTree();
                if (nameTree != null) {
                    Object o = nameTree.searchName(s);
                    if (o != null) {
                        if (o instanceof Vector) {
                            parse((Vector) o);
                            found = true;
                        } else if (o instanceof Hashtable) {
                            Hashtable h = (Hashtable) o;
                            Object o1 = h.get("D");
                            if (o1 instanceof Vector) {
                                parse((Vector) o1);
                                found = true;
                            }
                        }
                    }
                }
                if (!found) {
                    Dictionary dests = catalog.getDestinations();
                    if (dests != null) {
                        Object ob = dests.getObject(s);
                        if (ob instanceof Hashtable) {
                            parse((Vector) (((Hashtable) ob).get("D")));
                        } else {
                            if (logger.isLoggable(Level.FINE)) {
                                logger.warning("Destination type missed=" + ob);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the dictionary object, name, string or array. 
     * @return
     */
    public Object getObject() {
        return object;
    }

    /**
     * Utility method for parsing the Destination attributes
     *
     * @param v vector of attributes associated with the Destination
     */
    private void parse(Vector v) {

        // Assign a Reference
        Object ob = v.get(0);
        if (ob instanceof Reference) {
            ref = (Reference) ob;
        }
        // store type.
        ob = v.get(1);
        if (ob instanceof Name) {
            type = (Name) ob;
        } else {
            type = new Name(ob.toString());
        }
        // [page /XYZ left top zoom ]
        if (type.equals(TYPE_XYZ)) {
            ob = v.get(2);
            if (ob != null && !ob.equals("null")) {
                left = ((Number) ob).floatValue();
            }
            ob = v.get(3);
            if (ob != null && !ob.equals("null")) {
                top = ((Number) ob).floatValue();
            }
            ob = v.get(4);
            if (ob != null && !ob.equals("null") && !ob.equals("0")) {
                zoom = ((Number) ob).floatValue();
            }
        }
        // [page /FitH top]
        else if (type.equals(TYPE_FITH)) {
            ob = v.get(2);
            if (ob != null && !ob.equals("null")) {
                top = ((Number) ob).floatValue();
            }
        }
        // [page /FitR left bottom right top]
        else if (type.equals(TYPE_FITR)) {
            ob = v.get(2);
            if (ob != null && !ob.equals("null")) {
                left = ((Number) ob).floatValue();
            }
            ob = v.get(3);
            if (ob != null && !ob.equals("null")) {
                bottom = ((Number) ob).floatValue();
            }
            ob = v.get(4);
            if (ob != null && !ob.equals("null")) {
                right = ((Number) ob).floatValue();
            }
            ob = v.get(5);
            if (ob != null && !ob.equals("null")) {
                top = ((Number) ob).floatValue();
            }
        }
        // [page /FitB]
        else if (type.equals(TYPE_FITB)) {
            // nothing to parse
        }
        // [page /FitBH top]
        else if (type.equals(TYPE_FITBH)) {
            ob = v.get(2);
            if (ob != null && !ob.equals("null")) {
                top = ((Number) ob).floatValue();                
            }
        }
        // [page /FitBV left]
        else if (type.equals(TYPE_FITBV)) {
            ob = v.get(2);
            if (ob != null && !ob.equals("null")) {
                left = ((Number) ob).floatValue();
            }
        }
    }

    /**
     * Gets the name of the named destination.
     *
     * @return name of destination if present, null otherwise.
     */
    public Name getNamedDestination() {
        return namedDestination;
    }

    /**
     * Sets the named destination as a Named destination.  It is assumed
     * the named destination already exists in the document.
     *
     * @param dest destination to associate with.
     */
    public void setNamedDestination(Name dest) {
        namedDestination = dest;
        // only write out destination as names so we don't have worry about
        // encryption.
        object = dest;
        // re-parse as object should point to a new destination.
        inited = false;
        init();
    }

    /**
     * Sets the destination syntax to the specified value.  The Destinatoin
     * object clears the named destination and re initializes itself after the
     * assignment has been made.
     *
     * @param destinationSyntax new vector of destination syntax.
     */
    public void setDestinationSyntax(Vector destinationSyntax) {
        // clear named destination
        namedDestination = null;
        object = destinationSyntax;
        // re-parse as object should point to a new destination.
        inited = false;
        init();
    }

    /**
     * Utility for creating a /Fit or FitB syntax vector.
     *
     * @param page destination page pointer.
     * @param type type of destionation
     * @return new instance of vector containing well formed destination syntax.
     */
    public static Vector<Object> destinationSyntax(
            Reference page, final Name type) {
        Vector<Object> destSyntax = new Vector<Object>(2);
        destSyntax.add(page);
        destSyntax.add(type);
        return destSyntax;
    }

    /**
     * Utility for creating a /FitH, /FitV, /FitBH or /FitBV syntax vector.
     *
     * @param page destination page pointer.
     * @param type type of destionation
     * @param offset offset coordinate value in page space for specified dest type.
     * @return new instance of vector containing well formed destination syntax.
     */
    public static Vector<Object> destinationSyntax(
            Reference page, final Name type, Object offset) {
        Vector<Object> destSyntax = new Vector<Object>(3);
        destSyntax.addElement(page);
        destSyntax.addElement(type);
        destSyntax.addElement(offset);
        return destSyntax;
    }

    /**
     * Utility for creating a /XYZ syntax vector.
     *
     * @param page destination page pointer.
     * @param type type of destionation
     * @param left offset coordinate value in page space for specified dest type.
     * @param top offset coordinate value in page space for specified dest type.
     * @param zoom page zoom, 0 or null indicates no zoom.
     * @return new instance of vector containing well formed destination syntax.
     */
    public static Vector<Object> destinationSyntax(
            Reference page, final Object type, Object left, Object top, Object zoom) {
        Vector<Object> destSyntax = new Vector<Object>(5);
        destSyntax.add(page);
        destSyntax.add(type);
        destSyntax.add(left);
        destSyntax.add(top);
        destSyntax.add(zoom);
        return destSyntax;
    }

    /**
     * Utility for creating a /FitR syntax vector.
     *
     * @param page destination page pointer.
     * @param type type of destionation
     * @param left offset coordinate value in page space for specified dest type.
     * @param top offset coordinate value in page space for specified dest type.
     * @param bottom offset coordinate value in page space for specified dest type.
     * @param right offset coordinate value in page space for specified dest type.
     * @return new instance of vector containing well formed destination syntax.
     */
    public static Vector<Object> destinationSyntax(
            Reference page, final Object type, Object left, Object bottom,
            Object right, Object top) {
        Vector<Object> destSyntax = new Vector<Object>(6);
        destSyntax.add(page);
        destSyntax.add(type);
        destSyntax.add(left);
        destSyntax.add(bottom);
        destSyntax.add(right);
        destSyntax.add(top);
        return destSyntax;
    }

    /**
     * Gets the Page Reference specified by the destination.
     *
     * @return a Reference to the Page Object associated with this destination.
     */
    public Reference getPageReference() {
        return ref;
    }

    /**
     * Gets the left offset from the top, left position of the page specified by
     * this destination.
     *
     * @return the left offset from the top, left position  of the page.  If not
     *         specified Float.NaN is returned.
     */
    public Float getLeft() {
        return left;
    }

    /**
     * Gets the top offset from the top, left position of the page specified by
     * this destination.
     *
     * @return the top offset from the top, left position of the page.  If not
     *         specified Float.NaN is returned.
     */
    public Float getTop() {
        return top;
    }

    /**
     * Gets the zoom level specifed by the destination.
     *
     * @return the specified zoom level, Float.NaN if not specified.
     */
    public Float getZoom() {
        return zoom;
    }

    /**
     * Gets the page reference represented by this destination
     *
     * @return reference of page that destination should show when executed.
     */
    public Reference getRef() {
        return ref;
    }

    /**
     * Gets the type used in a vector of destination syntax.  Will be null
     * if a named destination is used.
     *
     * @return type of destination syntax as defined by class constants.
     */
    public Name getType() {
        return type;
    }

    /**
     * Bottom coordinate of a zoom box, if present left, right and top should
     * also be available.
     *
     * @return bottom coordinate of magnifcation box.
     */
    public Float getBottom() {
        return bottom;
    }

    /**
     * Right coordinate of a zoom box, if present bottom, left and top should
     * also be available.
     *
     * @return rigth coordinate of zoom box
     */
    public Float getRight() {
        return right;
    }

    /**
     * Get the destination properties encoded in post script form.
     *
     * @return either a destination Name or a Vector representing the
     *         destination
     */
    public Object getEncodedDestination() {
        // write out the destination name
        if (namedDestination != null) {
            return namedDestination;
        }
        // build and return a fector of changed valued.
        else if (object instanceof Vector) {
            Vector<Object> v = new Vector<Object>(7);
            if (ref != null) {
                v.add(ref);
            }
            // named dest type
            if (type != null) {
                v.add(type);
            }
            // left
            if (left != Float.NaN) {
                v.add(left);
            }
            // bottom
            if (bottom != Float.NaN) {
                v.add(bottom);
            }
            // right
            if (right != Float.NaN) {
                v.add(right);
            }
            // top
            if (top != Float.NaN) {
                v.add(top);
            }
            // zoom
            if (zoom != Float.NaN) {
                v.add(zoom);
            }
            return v;
        }
        return null;
    }

    /**
     * Returns a summary of the annotation dictionary values.
     *
     * @return dictionary values.
     */
    public String toString() {
        return "Destination  ref: " + getPageReference() + " ,  top: " +
                getTop() + " ,  left: " + getLeft() + " ,  zoom: " + getZoom();
    }
}



