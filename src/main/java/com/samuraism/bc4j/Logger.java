package com.samuraism.bc4j;

/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.logging.Level;

/**
 * @since 1.17
 */
final class Logger {
    private final java.util.logging.Logger jul;
    private final org.slf4j.Logger slf4j;

    Logger(java.util.logging.Logger julLogger, org.slf4j.Logger slf4jLogger) {
        this.jul = julLogger;
        this.slf4j = slf4jLogger;
    }

    public static void main(String[] args) {
        Logger.getLogger(Logger.class).debug("debug");
        Logger.getLogger(Logger.class).info("info");
        Logger.getLogger(Logger.class).warn("warn");
        Logger.getLogger(Logger.class).error("error");
    }

    static final boolean SLF4J_EXISTS_IN_CLASSPATH;

    static {
        boolean useSLF4J = false;
        try {
            // use SLF4J if it's found in the classpath
            Class.forName("org.slf4j.Logger");
            useSLF4J = true;
            getLogger(Logger.class).info("SLF4J Logger selected");
        } catch (ClassNotFoundException ignore) {
        }
        SLF4J_EXISTS_IN_CLASSPATH = useSLF4J;
        getLogger(Logger.class).info(SLF4J_EXISTS_IN_CLASSPATH ? "SLF4J Logger selected" : "jul Logger selected");
    }

    /**
     * Returns a Logger instance associated with the specified class.
     *
     * @param clazz class
     * @return logger instance
     */
    static Logger getLogger(Class<?> clazz) {
        return SLF4J_EXISTS_IN_CLASSPATH ? 
                new Logger(null, org.slf4j.LoggerFactory.getLogger(clazz)) :
                new Logger(java.util.logging.Logger.getLogger(clazz.getName()), null);
    }

    boolean isDebugEnabled() {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            return slf4j.isDebugEnabled();

        }
        return jul.isLoggable(Level.FINEST);
    }

    boolean isInfoEnabled() {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            return slf4j.isInfoEnabled();
        }
        return jul.isLoggable(Level.INFO);
    }

    boolean isWarnEnabled() {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            return slf4j.isWarnEnabled();
        }
        return jul.isLoggable(Level.WARNING);
    }

    boolean isErrorEnabled() {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            return slf4j.isErrorEnabled();
        }
        return jul.isLoggable(Level.SEVERE);
    }

    void debug(String message) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            slf4j.debug(message);
        } else {
            jul.finest(message);
        }
    }

    void info(String message) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            slf4j.info(message);
        } else {
            jul.info(message);
        }
    }

    void warn(String message) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            slf4j.warn(message);
        } else {
            jul.warning(message);
        }
    }

    void warn(String message, Throwable th) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            slf4j.warn(message, th);
        } else {
            jul.log(Level.WARNING, message, th);
        }
    }

    void error(String message) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            slf4j.error(message);
        } else {
            jul.severe(message);
        }
    }

    void error(String message, Throwable th) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            slf4j.error(message, th);
        } else {
            jul.log(Level.SEVERE, message, th);
        }
    }
}