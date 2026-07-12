/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 */
package com.bergerkiller.bukkit.common.dep.cloud.setting;

import com.bergerkiller.bukkit.common.dep.cloud.setting.Setting;
import org.apiguardian.api.API;

@API(status=API.Status.STABLE)
public enum ManagerSetting implements Setting
{
    FORCE_SUGGESTION,
    ALLOW_UNSAFE_REGISTRATION,
    OVERRIDE_EXISTING_COMMANDS,
    LIBERAL_FLAG_PARSING;

}

