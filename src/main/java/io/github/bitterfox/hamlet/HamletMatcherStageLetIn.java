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

public class HamletMatcherStageLetIn<S, P, T, M extends Matcher<S>> extends HamletMatcherStage<S, P, T, T, M> {
    private final MyFunction<? super P, ? extends T> function;

    public HamletMatcherStageLetIn(HamletMatcherStage<S, ?, P, ?, ?> upstream,
                                   MyFunction<? super P, ? extends T> function) {
        super(upstream, null);
        this.function = function;
    }

    @Override
    MappedValue<T, P, T, ?> requestValue(MappedValue<P, ?, ?, ?> upstreamValue) {
        return upstreamValue.letIn(function);
    }

    @Override
    protected void internalDescribeTo(HamletDescription description) {
        super.internalDescribeTo(description);

        description.appendLocation(location).appendText("let it = " + LanguageUtil.describeMethodReference(function) + " in");
        description.plusDepth(4);
    }

    @Override
    protected String describeMethodReference() {
        return LanguageUtil.describeMethodReference(function);
    }

    @Override
    protected void describeMismatchLetIn(MappedValue<T, ?, ?, ?> value, HamletDescription mismatchDescription) {
//        mismatchDescription.minusDepth(4);
        try {
            super.describeMismatchLetIn(value, mismatchDescription);
//            mismatchDescription.appendLocation(location)
//                               .appendText(
//                                       "let it = " + LanguageUtil.describeMethodReference(function) + " in");
            mismatchDescription.plusDepth(4);
            mismatchDescription.appendLocation(location)
                               .appendText(
                                       "let it = " + value.value() + " in");
        } finally {
//            mismatchDescription.plusDepth(4);
        }
    }
}
