/*
   Copyright 2007 Yusuke Yamamoto

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package one.cafebabe.bc4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @since 1.17
 */
final class Logger {
    static final List<String> logMessages = new ArrayList<>();

    private final java.util.logging.Logger jul;
    private final org.slf4j.Logger slf4j;

    Logger(java.util.logging.Logger julLogger, org.slf4j.Logger slf4jLogger) {
        this.jul = julLogger;
        this.slf4j = slf4jLogger;
    }

    public static void main(String[] args) {
        Logger.getLogger().debug(() -> "debug");
        Logger.getLogger().info(() -> "info");
        Logger.getLogger().warn(() -> "warn");
        Logger.getLogger().error(() -> "error");
    }

    static final boolean SLF4J_EXISTS_IN_CLASSPATH;

    static {
        boolean useSLF4J = false;
        try {
            // use SLF4J if it's found in the classpath
            Class.forName("org.slf4j.impl.StaticLoggerBinder");
            useSLF4J = true;
        } catch (ClassNotFoundException ignore) {
            try {
                // store all log messages to logMessages during test invocation
                Class.forName("org.junit.jupiter.api.Test");
                java.util.logging.Logger.getLogger("").addHandler(new java.util.logging.Handler() {
                    @Override
                    public void publish(LogRecord record) {
                        logMessages.add(record.getMessage());
                    }

                    @Override
                    public void flush() {

                    }

                    @Override
                    public void close() throws SecurityException {

                    }
                });
            } catch (ClassNotFoundException ignore2) {
            }
        }
        SLF4J_EXISTS_IN_CLASSPATH = useSLF4J;
        getLogger().info(() -> SLF4J_EXISTS_IN_CLASSPATH ? "SLF4J Logger selected" : "j.u.l Logger selected");
    }

    /**
     * Returns a Logger instance associated with the specified class.
     *
     * @return logger instance
     */
    static Logger getLogger() {
        final String className = new Throwable().getStackTrace()[1].getClassName();
        return SLF4J_EXISTS_IN_CLASSPATH ?
                new Logger(null, org.slf4j.LoggerFactory.getLogger(className)) :
                new Logger(java.util.logging.Logger.getLogger(className), null);
    }

    void debug(Supplier<String> supplier) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isDebugEnabled()) {
                slf4j.debug(supplier.get());
            }
        } else if (jul.isLoggable(Level.FINEST)) {
            jul.finest(supplier.get());
        }
    }

    void info(Supplier<String> supplier) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isInfoEnabled()) {
                slf4j.info(supplier.get());
            }
        } else if (jul.isLoggable(Level.INFO)) {
            jul.info(supplier.get());
        }
    }

    void warn(Supplier<String> supplier) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isWarnEnabled()) {
                slf4j.warn(supplier.get());
            }
        } else if (jul.isLoggable(Level.WARNING)) {
            jul.warning(supplier.get());
        }
    }

    void warn(Supplier<String> supplier, Throwable th) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isWarnEnabled()) {
                slf4j.warn(supplier.get(), th);
            }
        } else if (jul.isLoggable(Level.WARNING)) {
            jul.log(Level.WARNING, supplier.get(), th);
        }
    }

    void error(Supplier<String> supplier) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isErrorEnabled()) {
                slf4j.error(supplier.get());
            }
        } else if (jul.isLoggable(Level.SEVERE)) {
            jul.severe(supplier.get());
        }
    }

    void error(Supplier<String> supplier, Throwable th) {
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isErrorEnabled()) {
                slf4j.error(supplier.get(), th);
            }
        } else if (jul.isLoggable(Level.SEVERE)) {
            jul.log(Level.SEVERE, supplier.get(), th);
        }
    }
}