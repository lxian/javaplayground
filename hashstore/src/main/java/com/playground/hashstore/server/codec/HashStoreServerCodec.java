package com.playground.hashstore.server.codec;

import io.netty.channel.CombinedChannelDuplexHandler;


public class HashStoreServerCodec extends CombinedChannelDuplexHandler<HashStoreCommandDecoder, HashStoreByteBufSerializableEncoder> {

    public HashStoreServerCodec() {
        super(new HashStoreCommandDecoder(), new HashStoreByteBufSerializableEncoder());
    }
}
