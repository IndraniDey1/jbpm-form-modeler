/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.core.processing.fieldHandlers;

import org.jbpm.formModeler.api.model.Field;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.core.processing.DefaultFieldHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for dates
 */
@Named("org.jbpm.formModeler.core.processing.fieldHandlers.DateFieldHandler")
public class DateFieldHandler extends DefaultFieldHandler {
    private static transient Logger log = LoggerFactory.getLogger(DateFieldHandler.class);

    public static final String HAS_CHANGED_PARAM = "_hasChanged";
    public static final String DATE_PATTERN_SUFFIX = "_pattern";
    public static final Date DEFAULT_MIN_DATE = new Date(0);

    protected String defaultPattern;
    protected String defaultPatterTimeSuffix;
    protected String defaultJQueryPattern;

    public DateFieldHandler() {
        defaultPattern = "MM-dd-yy";
        defaultPatterTimeSuffix = "HH:mm:ss";
        defaultJQueryPattern="mm-dd-y";
    }

    /**
     * Read a parameter value (normally from a request), and translate it to
     * an object with desired class (that must be one of the returned by this handler)
     *
     * @return a object with desired class
     * @throws Exception
     */
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        String[] hasChangedParam = (String[]) parametersMap.get(inputName + HAS_CHANGED_PARAM);
        
        try {
            boolean hasChanged = (!ArrayUtils.isEmpty(hasChangedParam) && Boolean.TRUE.equals(Boolean.parseBoolean(hasChangedParam[0])));
            
            SimpleDateFormat sdf = getSimpleDateFormat(field, hasChanged, field.getFieldPattern());
            String[] dateValue = (String[]) parametersMap.get(inputName);
            if (!ArrayUtils.isEmpty(dateValue)) return getTheDate(dateValue, sdf);
        } catch (ParseException e) {
            log.debug("Error:", e);
        }
        return previousValue;
    }
    
    /**
     * Determine the list of class types this field can generate. That is, normally,
     * a field can generate multiple outputs (an input text can generate Strings,
     * Integers, ...)
     *
     * @return the set of class types that can be generated by this handler.
     */
    public String[] getCompatibleClassNames() {
        return new String[]{Date.class.getName()};
    }

    public boolean isEmpty(Object value) {
        if (value == null) return true;
        if (value instanceof Object[]) {
            Object[] values = (Object[]) value;
            for (int i = 0; i < values.length; i++) {
                Object date = values[i];
                if (date != null) return false;
            }
            return true;
        }
        return false;
    }
 
    public String getDefaultPattern() {
        return defaultPattern;
    }

    public void setDefaultPattern(String defaultPattern) {
        this.defaultPattern = defaultPattern;
    }

    public String getDefaultJQueryPattern() {
        return defaultJQueryPattern;
    }

    public void setDefaultJQueryPattern(String defaultJQueryPattern) {
        this.defaultJQueryPattern = defaultJQueryPattern;
    }

    protected String getPattern(Field field, boolean useDefault, String pattern) {
        if (!useDefault && field != null && !StringUtils.isEmpty(field.getPattern())) pattern = field.getPattern();
        
        return StringUtils.defaultString(pattern);
    }
    
    protected SimpleDateFormat getSimpleDateFormat(Field field, boolean useDefault, String pattern) {
        return new SimpleDateFormat(getPattern(field, useDefault, pattern));
    }
    
    public Object getTheDate(String[] values, SimpleDateFormat sdf) throws Exception {
        String date = !ArrayUtils.isEmpty(values) ? values[0] : null;
        return (!StringUtils.isEmpty(date)) ? sdf.parse(date) : null;
    }

    /**
     * Determine the value as a parameter map for a given input value. This is like the inverse operation of getValue()
     *
     * @param objectValue Object value to represent
     * @param pattern     Pattern to apply if any
     * @return a Map representing the parameter values expected inside a request that would cause the form
     *         to generate given object value as a result.
     */
    public Map getParamValue(String inputName, Object objectValue, String pattern) {
        Map m = new HashMap();
        if (objectValue != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(StringUtils.defaultString(pattern, defaultPattern));
            m.put(inputName, new String[]{sdf.format(objectValue)});
        }
        return m;
    }

    public String getDefaultPatterTimeSuffix() {
        return defaultPatterTimeSuffix;
    }

    public void setDefaultPatterTimeSuffix(String defaultPatterTimeSuffix) {
        this.defaultPatterTimeSuffix = defaultPatterTimeSuffix;
    }
}
