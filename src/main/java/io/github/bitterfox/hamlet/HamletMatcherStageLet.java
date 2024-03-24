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

import org.hamcrest.Matcher;

public class HamletMatcherStageLet<S, P, T, L, M extends Matcher<S>> extends HamletMatcherStage<S, P, T, L, M> {
    Function<? super T, ? extends L> function;

    public HamletMatcherStageLet(HamletMatcherStage<S, ?, P, ?, ?> upstream, LetMatcher<? super L, ?> matcher,
                                 Function<? super T, ? extends L> function) {
        super(upstream, matcher);
        this.function = function;
    }

    @Override
    MappedValue<T, P, L, ?> requestValue(MappedValue<P, ?, ?, ?> upstreamValue) {
        return upstreamValue.let(function);
    }
}
