/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 */
package com.bergerkiller.bukkit.tc.offline.sign;

import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.tc.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

class OfflineSignStoreUpgradeV1ToV2 {
    OfflineSignStoreUpgradeV1ToV2() {
    }

    public static DataInputStream upgrade(DataInputStream stream) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        Util.writeVariableLengthInt(outByteStream, 2);
        while (stream.available() > 0) {
            byte[] metadataContents;
            String[] signLines;
            OfflineBlock signBlock;
            byte[] encodedData = Util.readByteArray(stream);
            try (ByteArrayInputStream m_b_stream = new ByteArrayInputStream(encodedData);
                 InflaterInputStream m_d_stream = new InflaterInputStream(m_b_stream);
                 DataInputStream m_stream = new DataInputStream(m_d_stream);){
                int b;
                signBlock = OfflineBlock.readFrom((DataInputStream)m_stream);
                signLines = new String[4];
                for (int n = 0; n < 4; ++n) {
                    signLines[n] = m_stream.readUTF();
                }
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                while ((b = m_stream.read()) != -1) {
                    data.write(b);
                }
                metadataContents = data.toByteArray();
            }
            byte[] upgradedData = OfflineSignStoreUpgradeV1ToV2.encodeMetadata(signBlock, signLines, metadataContents);
            Util.writeByteArray(outByteStream, upgradedData);
        }
        return new DataInputStream(new ByteArrayInputStream(outByteStream.toByteArray()));
    }

    private static byte[] encodeMetadata(OfflineBlock signBlock, String[] signLines, byte[] metadata) throws IOException {
        try (ByteArrayOutputStream b_stream = new ByteArrayOutputStream();){
            try (DeflaterOutputStream d_stream = new DeflaterOutputStream(b_stream);
                 DataOutputStream stream = new DataOutputStream(d_stream);){
                OfflineBlock.writeTo((DataOutputStream)stream, (OfflineBlock)signBlock);
                stream.writeBoolean(true);
                for (String line : signLines) {
                    stream.writeUTF(line);
                }
                stream.write(metadata);
            }
            byte[] byArray = b_stream.toByteArray();
            return byArray;
        }
    }
}

