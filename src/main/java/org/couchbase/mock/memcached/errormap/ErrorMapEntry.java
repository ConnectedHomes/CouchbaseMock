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

package org.couchbase.mock.memcached.errormap;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;
import java.util.Set;

/**
 * Created by mnunberg on 4/12/17.
 */
public class ErrorMapEntry {
    private String name;

    @SerializedName("desc")
    private String description;

    private Set<String> attrs;

    private RetrySpec retry;

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Collection<String> getAttrs() {
        return attrs;
    }
    public boolean hasAttr(String attr) {
        return attrs.contains(attr);
    }
    public RetrySpec getRetrySpec() {
        return retry;
    }
}