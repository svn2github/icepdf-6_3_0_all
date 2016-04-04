/*
 * Copyright 2006-2016 ICEsoft Technologies Inc.
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
package org.icepdf.core.util.loggers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BriefestLogFormatter extends Formatter {

    private static final DateFormat format = new SimpleDateFormat("h:mm:ss");
    private static final String lineSep = System.getProperty("line.separator");

    /**
     * A Custom format implementation that is designed for brevity.
     */
    public String format(LogRecord record) {
        String loggerName = record.getLoggerName();
        if (loggerName == null) {
            loggerName = "root";
        }
        StringBuilder output = new StringBuilder()
                .append(record.getMessage())
                .append(lineSep);
        return output.toString();
    }

}