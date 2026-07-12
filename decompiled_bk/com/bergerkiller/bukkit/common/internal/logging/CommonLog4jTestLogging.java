/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.core.Logger
 *  org.apache.logging.log4j.core.LoggerContext
 *  org.apache.logging.log4j.message.MessageFactory
 *  org.apache.logging.log4j.spi.ExtendedLogger
 *  org.apache.logging.log4j.spi.LoggerContext
 *  org.apache.logging.log4j.spi.LoggerContextFactory
 */
package com.bergerkiller.bukkit.common.internal.logging;

import com.bergerkiller.bukkit.common.internal.logging.CommonLog4jCoreLogger;
import com.bergerkiller.bukkit.common.internal.logging.CommonLog4jExtendedLogger;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContextFactory;

public class CommonLog4jTestLogging {
    public static void initLog4jLegacy() {
        LogManager.setFactory((LoggerContextFactory)new LoggerContextFactory(){

            public org.apache.logging.log4j.spi.LoggerContext getContext(String arg0, ClassLoader arg1, Object arg2, boolean arg3) {
                return new org.apache.logging.log4j.spi.LoggerContext(){

                    public Object getExternalContext() {
                        return null;
                    }

                    public ExtendedLogger getLogger(String name) {
                        return new CommonLog4jExtendedLogger(name);
                    }

                    public ExtendedLogger getLogger(String arg0, MessageFactory arg1) {
                        return this.getLogger(arg0);
                    }

                    public boolean hasLogger(String arg0) {
                        return true;
                    }

                    public boolean hasLogger(String arg0, MessageFactory arg1) {
                        return true;
                    }

                    public boolean hasLogger(String arg0, Class<? extends MessageFactory> arg1) {
                        return true;
                    }
                };
            }

            public org.apache.logging.log4j.spi.LoggerContext getContext(String arg0, ClassLoader arg1, Object arg2, boolean arg3, URI arg4, String arg5) {
                return this.getContext(arg0, arg1, arg2, arg3);
            }

            public void removeContext(org.apache.logging.log4j.spi.LoggerContext arg0) {
            }
        });
    }

    public static void initLog4j() {
        final LoggerContext context = new LoggerContext("Test"){

            public Object getExternalContext() {
                return null;
            }

            public Logger getLogger(String name) {
                return new CommonLog4jCoreLogger(this, name);
            }

            public Logger getLogger(String arg0, MessageFactory arg1) {
                return this.getLogger(arg0);
            }

            public boolean hasLogger(String arg0) {
                return true;
            }

            public boolean hasLogger(String arg0, MessageFactory arg1) {
                return true;
            }

            public boolean hasLogger(String arg0, Class<? extends MessageFactory> arg1) {
                return true;
            }
        };
        LogManager.setFactory((LoggerContextFactory)new LoggerContextFactory(){

            public LoggerContext getContext(String arg0, ClassLoader arg1, Object arg2, boolean arg3) {
                return context;
            }

            public LoggerContext getContext(String arg0, ClassLoader arg1, Object arg2, boolean arg3, URI arg4, String arg5) {
                return context;
            }

            public void removeContext(org.apache.logging.log4j.spi.LoggerContext context2) {
            }
        });
    }
}

