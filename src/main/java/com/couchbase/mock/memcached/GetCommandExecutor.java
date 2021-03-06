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
package com.couchbase.mock.memcached;

import com.couchbase.mock.memcached.protocol.BinaryCommand;
import com.couchbase.mock.memcached.protocol.BinaryGetCommand;
import com.couchbase.mock.memcached.protocol.BinaryGetResponse;
import com.couchbase.mock.memcached.protocol.BinaryResponse;
import com.couchbase.mock.memcached.protocol.CommandCode;
import com.couchbase.mock.memcached.protocol.ErrorCode;

import java.security.AccessControlException;

/**
 * @author Trond Norbye
 */
public class GetCommandExecutor implements CommandExecutor {

    @Override
    public void execute(BinaryCommand command, MemcachedServer server, MemcachedConnection client) {
        BinaryGetCommand cmd = (BinaryGetCommand) command;
        VBucketStore cache;
        CommandCode cc = cmd.getComCode();

        if (cc == CommandCode.GET_REPLICA) {
            cache = server.getStorage().getCache(cmd.getVBucketId());
            if (!server.getStorage().getVBucketInfo(cmd.getVBucketId()).hasAccess(server)) {
                throw new AccessControlException("we're not a master or replica");
            }
        } else {
            cache = server.getStorage().getCache(server, cmd.getVBucketId());
        }

        Item item = cache.get(cmd.getKeySpec());

        if (item == null) {
            if (cc != CommandCode.GETKQ && cc != CommandCode.GETQ && cc != CommandCode.GATQ) {
                client.sendResponse(new BinaryGetResponse(cmd, ErrorCode.KEY_ENOENT,
                        server.isEnhancedErrorsEnabled() ? "Failed to lookup item" : null));
            }
            return;
        }

        if (cc == CommandCode.GETL) {
            ErrorCode ec = cache.lock(item, cmd.getExpiration());
            if (ec != ErrorCode.SUCCESS) {
                client.sendResponse(new BinaryResponse(cmd, ec,
                        server.isEnhancedErrorsEnabled() ? "Failed to lock item" : null));
                return;
            }
            client.sendResponse(new BinaryGetResponse(cmd, item, item.getCasReal()));
            return;
        } else if (cc == CommandCode.TOUCH || cc == CommandCode.GAT || cc == CommandCode.GATQ) {
            ErrorCode ec = cache.touch(item, cmd.getExpiration(), client.supportsXerror());
            if (ec != ErrorCode.SUCCESS) {
                client.sendResponse(new BinaryResponse(cmd, ec));
                return;
            }
        }

        if (cc == CommandCode.TOUCH) {
            client.sendResponse(new BinaryGetResponse(cmd, ErrorCode.SUCCESS));
        } else {
            client.sendResponse(new BinaryGetResponse(cmd, item));
        }
    }
}
