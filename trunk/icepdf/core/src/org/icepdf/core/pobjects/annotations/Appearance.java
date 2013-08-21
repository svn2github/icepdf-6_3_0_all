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
package org.icepdf.core.pobjects.annotations;

import org.icepdf.core.pobjects.Name;

import java.util.HashMap;

/**
 * An appearance dictionary dictionary entry for N, R or D can be associated
 * with one or more appearance streams.  For example a Widget btn annotation
 * can have an /ON an /Off state.  This class sill store one or more
 * named appearance streams for a dictionary entry.
 *
 * @since 5.1
 */
public class Appearance {

    private HashMap<Name, AppearanceState> appearance;

    private Name offName;
    private Name onName;

    /**
     * Create a new instance of an Appearance stream.
     */
    public Appearance() {
        appearance = new HashMap<Name, AppearanceState>(2);
    }

    public boolean hasAlternativeAppearance() {
        return appearance.size() > 1;
    }

    public void addAppearance(Name name, AppearanceState appearanceState) {
        appearance.put(name, appearanceState);
        if (name.getName().toLowerCase().equals("off")) {
            offName = name;
        } else {
            onName = name;
        }
    }

    public Name getOffName() {
        return offName;
    }

    public Name getOnName() {
        return onName;
    }

    public AppearanceState getAppearanceState(Name name) {
        AppearanceState state = appearance.get(name);
//        if (state != null){
        return state;
//        }else{
//            state = new AppearanceState(library, entries)
//        }
    }
}
