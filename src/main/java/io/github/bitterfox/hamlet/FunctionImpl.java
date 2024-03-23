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

public class FunctionImpl<S, T, U> implements Function<S, T, U> {
    private Function<S, ?, T> previousFunction;
    private java.util.function.Function<? super T, ? extends U> function;

    public FunctionImpl(Function<S, ?, T> previousFunction, java.util.function.Function<? super T, ? extends U> function) {
        this.previousFunction = previousFunction;
        this.function = function;
    }

    @Override
    public Function<S, ?, T> previousFunction() {
        return previousFunction;
    }

    @Override
    public <R> Function<S, U, R> andThen(java.util.function.Function<? super U, ? extends R> function) {
        return new FunctionImpl<>(this, function);
    }

    @Override
    public U apply(S s) {
        return function.apply(previousFunction.apply(s));
    }
}
