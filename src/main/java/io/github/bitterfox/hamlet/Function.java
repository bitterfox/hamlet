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

public interface Function<S, T, U> {
    U apply(S s);
    Function<S, ?, T> previousFunction();

    <R> Function<S, U, R> andThen(java.util.function.Function<? super U, ? extends R> function);

    static <T> Function<T, T, T> identity() {
        return new Function<T, T, T>() {
            @Override
            public T apply(T t) {
                return t;
            }

            @Override
            public Function<T, ?, T> previousFunction() {
                return Function.identity();
            }

            @Override
            public <R> Function<T, T, R> andThen(java.util.function.Function<? super T, ? extends R> function) {
                return new FunctionImpl<>(this, function);
            }
        };
    }
}
