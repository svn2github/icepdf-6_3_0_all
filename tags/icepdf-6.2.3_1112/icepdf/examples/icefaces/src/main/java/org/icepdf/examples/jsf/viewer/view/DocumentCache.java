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

package org.icepdf.examples.jsf.viewer.view;

import org.icepdf.core.pobjects.Document;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.util.HashMap;

/**
 * Simple document cache which is used to limit the total number of open
 * document on the public server and hopefully reduce memory for a large
 * number of viewers.
 *
 */
public class DocumentCache extends HashMap<String, Document> {

    public static final String BEAN_NAME = "documentCache";

}
