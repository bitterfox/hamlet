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

public class MappedValue<T, P, V extends MappedValue<P, ?, ?>> {
    V previousValue;
    T value;
    int letInScope;

    public MappedValue(V previousValue, T value, int letInScope) {
        this.previousValue = previousValue;
        this.value = value;
        this.letInScope = letInScope;
    }

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

    <U> MappedValue<U, T, MappedValue<T, P, V>> identity() {
        return new MappedValue<>(this, (U) this.value, this.letInScope());
    }

    <U> MappedValue<U, T, MappedValue<T, P, V>> letIn(U value) {
        return new MappedValue<>(this, value, this.letInScope + 1);
    }

    <U> MappedValue<U, T, MappedValue<T, P, V>> end() {
        MappedValue letInValue = this;
        while (letInValue != null && this.letInScope <= letInValue.letInScope()) {
            letInValue = letInValue.previousValue;
        }
        if (letInValue == null) {
            return new MappedValue(this, null, this.letInScope - 1);
        } else {
            return new MappedValue(this, letInValue.value(), letInScope - 1);
        }
    }
}
