package com.aliyun.tair.taircpc.params;

import redis.clients.jedis.params.Params;
import redis.clients.jedis.util.SafeEncoder;

import java.util.ArrayList;

import static redis.clients.jedis.Protocol.toByteArray;

public class CpcMultiUpdateParams extends Params {
    public CpcMultiUpdateParams() {}

    public static CpcMultiUpdateParams cpcMutiUpdateParams() {
        return new CpcMultiUpdateParams();
    }

    public byte[][] getByteParams(ArrayList<CpcData> keys) {
        ArrayList<byte[]> byteParams = new ArrayList<byte[]>();

        for (CpcData key:keys) {
            byteParams.add(SafeEncoder.encode(key.getKey()));
            byteParams.add(SafeEncoder.encode(key.getItem()));
            byteParams.add(SafeEncoder.encode(key.getExpStr()));
            byteParams.add(toByteArray(key.getExp()));
        }

        return byteParams.toArray(new byte[byteParams.size()][]);
    }
}
