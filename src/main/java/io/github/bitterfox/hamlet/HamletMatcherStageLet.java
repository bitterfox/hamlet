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

import org.hamcrest.Matcher;

public class HamletMatcherStageLet<S, P, T, L, M extends Matcher<S>> extends HamletMatcherStage<S, P, T, L, M> {
    MyFunction<? super T, ? extends L> function;
    boolean shortDescription = true;

    public HamletMatcherStageLet(HamletMatcherStage<S, ?, P, ?, ?> upstream, LetMatcher<? super L, ?> matcher,
                                 MyFunction<? super T, ? extends L> function) {
        super(upstream, matcher);
        this.function = function;
    }

    @Override
    protected void internalDescribeTo(HamletDescription description) {
        if (shortDescription) {
            matcher.describeTo(description, LanguageUtil.describeMethodReference(function));

        } else {
            description.appendLocation(location)
                       .appendText("let it = " + LanguageUtil.describeMethodReference(function) + " in");
            description.plusDepth(4);
            try {
                super.internalDescribeTo(description);
            } finally {
                description.minusDepth(4);
            }
        }
    }

    @Override
    protected String describeMethodReference() {
        return LanguageUtil.describeMethodReference(function);
    }

    @Override
    MappedValue<T, P, L, ?> requestValue(MappedValue<P, ?, ?, ?> upstreamValue) {
        return upstreamValue.let(function);
    }
}
