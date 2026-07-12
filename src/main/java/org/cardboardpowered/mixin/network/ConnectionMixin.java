package org.cardboardpowered.mixin.network;

import java.net.SocketAddress;
import java.util.UUID;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.cardboardpowered.bridge.network.ConnectionBridge;
import com.mojang.authlib.properties.Property;

@Mixin(Connection.class)
public class ConnectionMixin implements ConnectionBridge {

    public UUID spoofedUUID;
    public Property[] spoofedProfile;
    public boolean preparing = true;
    public java.net.InetSocketAddress virtualHost;
    public java.net.SocketAddress haProxyAddress;

    @Override
    public SocketAddress getRawAddress() {
        return ((Connection)(Object)this).channel.remoteAddress();
    }

    @Override
    public UUID getSpoofedUUID() {
        return spoofedUUID;
    }

    @Override
    public void setSpoofedUUID(UUID uuid) {
        this.spoofedUUID = uuid;
    }

    @Override
    public Property[] getSpoofedProfile() {
        return spoofedProfile;
    }

    @Override
    public void setSpoofedProfile(Property[] profile) {
        this.spoofedProfile = profile;
    }

    @Override
    public java.net.InetSocketAddress getVirtualHost() {
        return this.virtualHost;
    }

    @Override
    public void setVirtualHost(java.net.InetSocketAddress address) {
        this.virtualHost = address;
    }

    @Override
    public java.net.SocketAddress getHAProxyAddress() {
        return this.haProxyAddress;
    }

    @Override
    public void setHAProxyAddress(java.net.SocketAddress address) {
        this.haProxyAddress = address;
    }

}
