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
package org.icepdf.core.pobjects.graphics;

import org.icepdf.core.pobjects.functions.Function;
import org.icepdf.core.util.Library;

import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * DeviceN Color Space.
 */
public class DeviceN extends PColorSpace {
    Vector names;
    PColorSpace alternate;
    Function func;
    Hashtable<Object, Object> colorants = new Hashtable<Object, Object>();
    PColorSpace colorspaces[];

    DeviceN(Library l, Hashtable h, Object o1, Object o2, Object o3, Object o4) {
        super(l, h);
        names = (Vector) o1;
        alternate = getColorSpace(l, o2);
        func = Function.getFunction(l, l.getObject(o3));
        if (o4 != null) {
            Hashtable h1 = (Hashtable) library.getObject(o4);
            Hashtable h2 = (Hashtable) library.getObject(h1, "Colorants");
            if (h2 != null) {
                Enumeration e = h2.keys();
                while (e.hasMoreElements()) {
                    Object o = e.nextElement();
                    Object oo = h2.get(o);
                    colorants.put(o, getColorSpace(library, library.getObject(oo)));
                }
            }
        }
        colorspaces = new PColorSpace[names.size()];
        for (int i = 0; i < colorspaces.length; i++) {
            colorspaces[i] = (PColorSpace) colorants.get(names.elementAt(i).toString());
        }
    }

    public int getNumComponents() {
        return names.size();
    }

    public Color getColor(float[] f) {
        if (func == null) {
            // try alternative colour for CMYK
            int comps = alternate.getNumComponents();
            if (comps > f.length && comps == 4) {
                float[] ftmp = new float[alternate.getNumComponents()];
                for (int index = 0; index < f.length && index < names.size(); index++) {
                    if (names.get(index).equals("Cyan")) {
                        ftmp[0] = f[index];
                    }
                    else if (names.get(index).equals("Magenta")) {
                        ftmp[1] = f[index];
                    }
                    else if (names.get(index).equals("Yellow")) {
                        ftmp[2] = f[index];
                    }
                    else if (names.get(index).equals("Black")) {
                        ftmp[3] = f[index];
                    }
                }
                f = ftmp;
                float y[] = new float[alternate.getNumComponents()];
                System.arraycopy(f, 0, y, 0, Math.min(y.length, f.length));
                return alternate.getColor(y);
            }
            // process the alternative colour
            float y[] = new float[alternate.getNumComponents()];
            System.arraycopy(f, 0, y, 0, Math.min(y.length, f.length));
            return alternate.getColor(y);
        }
        float y[] = func.calculate(f);
        if (colorspaces[0] != null) {
            return colorspaces[0].getColor(reverse(y));
        }
        return alternate.getColor(reverse(y));
    }
}



