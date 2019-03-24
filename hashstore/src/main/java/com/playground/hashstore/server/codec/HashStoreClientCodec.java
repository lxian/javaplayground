package com.playground.hashstore.server.codec;

import io.netty.channel.CombinedChannelDuplexHandler;


public class HashStoreClientCodec extends CombinedChannelDuplexHandler<HashStoreResponseDecoder, HashStoreByteBufSerializableEncoder> {

    public HashStoreClientCodec() {
        super(new HashStoreResponseDecoder(), new HashStoreByteBufSerializableEncoder());
    }
}
