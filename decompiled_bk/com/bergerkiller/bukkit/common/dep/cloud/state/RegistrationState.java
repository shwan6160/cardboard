/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 */
package com.bergerkiller.bukkit.common.dep.cloud.state;

import com.bergerkiller.bukkit.common.dep.cloud.state.State;
import org.apiguardian.api.API;

@API(status=API.Status.STABLE)
public enum RegistrationState implements State
{
    BEFORE_REGISTRATION,
    REGISTERING,
    AFTER_REGISTRATION;

}

