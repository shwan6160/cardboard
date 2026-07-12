/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.proxies;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.proxies.Proxy;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class ProxyBase<T>
implements Proxy<T> {
    protected T base;

    public ProxyBase(T base) {
        this.setProxyBase(base);
    }

    @Override
    public void setProxyBase(T base) {
        this.base = base;
    }

    @Override
    public T getProxyBase() {
        return this.base;
    }

    public String toString() {
        return this.base.toString();
    }

    public int hashCode() {
        return this.base.hashCode();
    }

    public boolean equals(Object object) {
        return this.base.equals(ProxyBase.unwrap(object));
    }

    public static Object unwrap(Object object) {
        if (object instanceof Proxy) {
            return ((Proxy)object).getProxyBase();
        }
        return object;
    }

    public static boolean validate(Class<? extends Proxy<?>> proxy) {
        try {
            boolean succ = true;
            boolean loggedHeader = false;
            for (Method method : proxy.getDeclaredMethods()) {
                if (method.getDeclaringClass() == proxy) continue;
                succ = false;
                if (!loggedHeader) {
                    loggedHeader = true;
                    Common.LOGGER.log(Level.WARNING, "[Proxy] Some method(s) are not overrided in '" + proxy.getName() + "':");
                }
                Common.LOGGER.log(Level.WARNING, "    - '" + method.toGenericString());
            }
            return succ;
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Error while validating proxy class " + proxy, t);
            return false;
        }
    }
}

