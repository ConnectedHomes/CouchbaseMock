/*
 * Copyright 2017 Couchbase, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.couchbase.mock.memcached.protocol;

import com.couchbase.mock.memcached.Item;

import java.nio.ByteBuffer;

/**
 * @author Trond Norbye
 */
public class BinaryGetResponse extends BinaryResponse {
    public BinaryGetResponse(BinaryCommand command, ErrorCode error) {
        super(command, error);
    }

    public BinaryGetResponse(BinaryCommand command, ErrorCode error, String errorContext) {
        super(command, error, errorContext);
    }

    public BinaryGetResponse(BinaryCommand command, Item item) {
        super(create(command, item));
    }

    public BinaryGetResponse(BinaryGetCommand cmd, Item item, long casOverride) {
        super(create(cmd, item, casOverride));
    }

    private static ByteBuffer create(BinaryCommand command, Item item) {
        return create(command, item, null);
    }

    private static ByteBuffer create(BinaryCommand command, Item item, Long casOverride) {
        int keySize;
        byte[] keyBytes;
        switch (command.getComCode()) {
            case GETK:
            case GETKQ:
            case GET_REPLICA:
                keyBytes = command.getKey().getBytes();
                keySize = command.getKey().length();
                break;
            case GET_RANDOM:
                keyBytes = item.getKeySpec().key.getBytes();
                keySize = item.getKeySpec().key.length();
                break;
            default:
                keySize = 0;
                keyBytes = null;
        }
        final ByteBuffer message = create(command, ErrorCode.SUCCESS,
                4 /* flags */,
                keySize,
                item.getValue().length, casOverride == null ? item.getCas() : casOverride);
        message.putInt(item.getFlags());
        if (keySize > 0) {
            message.put(keyBytes);
        }
        message.put(item.getValue());
        message.rewind();
        return message;
    }
}
