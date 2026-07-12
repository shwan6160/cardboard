/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.server.CommonServerBase;
import java.lang.reflect.Constructor;
import org.bukkit.Bukkit;

public class UnknownServer
extends CommonServerBase {
    public String PACKAGE_VERSION;
    public String NMS_ROOT_VERSIONED;
    public String CB_ROOT_VERSIONED;

    @Override
    public String getServerVersion() {
        if (Bukkit.getServer() == null) {
            return "UNKNOWN";
        }
        return Bukkit.getServer().getVersion();
    }

    @Override
    public String getServerName() {
        return "Unknown Server";
    }

    @Override
    public boolean isMojangMappings() {
        return false;
    }

    @Override
    public boolean isForgeServer() {
        return false;
    }

    @Override
    public String getServerDescription() {
        if (Bukkit.getServer() == null) {
            return "NULL";
        }
        return Bukkit.getServer().getVersion();
    }

    @Override
    public String getMinecraftVersion() {
        return "UNKNOWN";
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void postInit(CommonServer.PostInitEvent event) {
        event.signalIncompatible("Server software type could not be identified");
        if (CommonServerBase.SERVER_CLASS != null) {
            this.CB_ROOT_VERSIONED = CommonServerBase.SERVER_CLASS.getPackage().getName();
            this.NMS_ROOT_VERSIONED = "net.minecraft.server" + this.CB_ROOT_VERSIONED.substring(Common.CB_ROOT.length());
            block0: for (Constructor<?> c : CommonServerBase.SERVER_CLASS.getDeclaredConstructors()) {
                for (Class<?> type : c.getParameterTypes()) {
                    if (!type.getName().startsWith(Common.NMS_ROOT)) continue;
                    this.NMS_ROOT_VERSIONED = type.getPackage().getName();
                    continue block0;
                }
            }
        } else {
            this.CB_ROOT_VERSIONED = Common.CB_ROOT;
            this.NMS_ROOT_VERSIONED = Common.NMS_ROOT;
        }
    }

    @Override
    public String getNMSRoot() {
        return null;
    }

    @Override
    public String getCBRoot() {
        return null;
    }
}

