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
import org.icepdf.core.pobjects.acroform.ButtonFieldDictionary;
import org.icepdf.core.util.Library;

import java.awt.geom.AffineTransform;
import java.util.HashMap;

/**
 * Represents a Acroform Button widget and manages the appearance streams
 * for the various appearance states.
 *
 * @since 5.1
 */
public class ButtonWidgetAnnotation extends AbstractWidgetAnnotation {

    public ButtonWidgetAnnotation(Library l, HashMap h) {
        super(l, h);
        fieldDictionary = new ButtonFieldDictionary(library, entries);
    }

    public void resetAppearanceStream(double dx, double dy, AffineTransform pageSpace) {

    }

    public void reset() {
        // todo.
    }

    public void turnOff() {
        Appearance appearance = appearances.get(APPEARANCE_STREAM_NORMAL_KEY);
        if (appearance.hasAlternativeAppearance()) {
            selectedNormalAppearance = appearance.getOffName();
        }
    }

    public Name toggle() {
        Appearance appearance = appearances.get(APPEARANCE_STREAM_NORMAL_KEY);
        if (appearance.hasAlternativeAppearance()) {
            if (!selectedNormalAppearance.equals(appearance.getOffName())) {
                return selectedNormalAppearance = appearance.getOffName();
            } else {
                return selectedNormalAppearance = appearance.getOnName();
            }
        } else {
            appearance = appearances.get(APPEARANCE_STREAM_DOWN_KEY);
            if (appearance.hasAlternativeAppearance()) {
                if (!selectedNormalAppearance.equals(appearance.getOffName())) {
                    return selectedNormalAppearance = appearance.getOffName();
                } else {
                    return selectedNormalAppearance = appearance.getOnName();
                }
            }
        }
        return null;
    }


    public void turnOn() {
        // first check if there are more then one normal streams.
        Appearance appearance = appearances.get(APPEARANCE_STREAM_NORMAL_KEY);
        AppearanceState appearanceState = appearance.getAppearanceState(selectedNormalAppearance);
        if (appearance.hasAlternativeAppearance()) {
            selectedNormalAppearance = appearance.getOnName();
        }
    }
}


