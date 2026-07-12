/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.logging;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.lang.ref.WeakReference;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class WeakLoggingHandler
extends Handler {
    private static final Handler NOOP_HANDLER = new Handler(){

        @Override
        public void publish(LogRecord record) {
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    };
    private final Logger logger;
    private final WeakReference<Handler> handler;

    public static void addHandler(Logger logger, Handler handler) {
        logger.addHandler(new WeakLoggingHandler(logger, handler));
    }

    public static void removeHandler(Logger logger, Handler handler) {
        for (Handler existingHandler : logger.getHandlers()) {
            if (!(existingHandler instanceof WeakLoggingHandler)) continue;
            WeakLoggingHandler weakHandler = (WeakLoggingHandler)existingHandler;
            if (weakHandler.handler.get() != handler) continue;
            weakHandler.handler.clear();
            logger.removeHandler(weakHandler);
            return;
        }
    }

    public static Handler[] unwrapHandlers(Logger logger) {
        Handler[] handlers = logger.getHandlers();
        for (int n = handlers.length - 1; n >= 0; --n) {
            Handler h = handlers[n];
            if (!(h instanceof WeakLoggingHandler)) continue;
            h = (Handler)((WeakLoggingHandler)h).handler.get();
            if (h == null) {
                handlers = LogicUtil.removeArrayElement(handlers, n);
                continue;
            }
            handlers[n] = h;
        }
        return handlers;
    }

    private WeakLoggingHandler(Logger logger, Handler handler) {
        this.logger = logger;
        this.handler = new WeakReference<Handler>(handler);
    }

    private Handler access() {
        Handler realHandler = (Handler)this.handler.get();
        if (realHandler == null) {
            this.logger.removeHandler(this);
            return NOOP_HANDLER;
        }
        return realHandler;
    }

    @Override
    public void publish(LogRecord record) {
        this.access().publish(record);
    }

    @Override
    public void flush() {
        this.access().flush();
    }

    @Override
    public void close() throws SecurityException {
        this.access().close();
    }
}

