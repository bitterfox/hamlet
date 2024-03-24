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

import java.util.function.Function;

public class MappedValue<T, P, L, V extends MappedValue<P, ?, ?, ?>> {
    V previousValue;
    T value;
    L letValue;
    int letInScope;

    public MappedValue(V previousValue, T value, L letValue, int letInScope) {
        this.previousValue = previousValue;
        this.value = value;
        this.letValue = letValue;
        this.letInScope = letInScope;
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

    <U, M> MappedValue<U, T, M, MappedValue<T, P, L, V>> identity() {
        return new MappedValue<>(this, (U) this.value, (M) this.value, this.letInScope());
    }

    <U, M> MappedValue<U, T, M, MappedValue<T, P, L, V>> letIn(Function<? super T, ? extends U> f) {
        U letIn = f.apply(value);
        return new MappedValue<>(this, letIn, (M) letIn, this.letInScope + 1);
    }

    <U, M> MappedValue<U, T, M, MappedValue<T, P, L, V>> end() {
        MappedValue<T, P, ?, V> letInValue = this;
        while (letInValue != null && this.letInScope <= letInValue.letInScope()) {
            letInValue = (MappedValue<T, P, ?, V>) letInValue.previousValue;
        }
        if (letInValue == null) {
            return new MappedValue<>(this, null, null, this.letInScope - 1);
        } else {
            return new MappedValue<>(this, (U) letInValue.value(), (M) letInValue.value(), letInScope - 1);
        }
    }

    <U, M> MappedValue<U, T, M, MappedValue<T, P, L, V>> let(Function<? super U, ? extends M> f) {
        return new MappedValue<>(this, (U) this.value, f.apply((U) this.value), this.letInScope());
    }
}
