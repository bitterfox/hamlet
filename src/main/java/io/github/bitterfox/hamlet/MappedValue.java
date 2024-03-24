/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.github.bitterfox.hamlet;

public class MappedValue<T, U, V extends MappedValue<U, ?, ?>> {
    V previousValue;
    T value;
    int letInScope;

    public MappedValue(V previousValue, T value, boolean letIn, boolean end) {
        this(previousValue, value, letIn);

        if (end) {
            letInScope = letInScope - 1;
        }
    }
    public MappedValue(V previousValue, T value, boolean letIn) {
        this.previousValue = previousValue;
        this.value = value;
        if (previousValue != null) {
            if (letIn) {
                letInScope = previousValue.letInScope() + 1;
            } else {
                letInScope = previousValue.letInScope();
            }
        } else {
            letInScope = 0;
        }
    }

    public T value() {
        return value;
    }
    public V previousValue() {
        return previousValue;
    }

    public int letInScope() {
        return letInScope;
    }
}