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

public class HamletMatcherStageRoot<S, M extends Matcher<S>> extends HamletMatcherStage<S, S, S, S, M> {
    public HamletMatcherStageRoot() {
        super(null, null);
    }

    public HamletMatcherStageRoot(Matcher<? super S> matcher) {
        super(null, new LetMatcher<>(matcher));
    }

    @Override
    protected boolean internalMatches(MappedValue<S, ?, ?, ?> value, HamletDescription mismatchDescription,
                                      boolean upstreamMatched) {
        return matcher == null ? upstreamMatched : (upstreamMatched && matcher.matches(value.value, mismatchDescription));
    }

    @Override
    MappedValue<S, S, S, ?> requestValue(MappedValue<S, ?, ?, ?> upstreamValue) {
        throw new IllegalStateException("requestValue for root should not be called");
    }

    @Override
    protected void describeMismatchLetIn(MappedValue<S, ?, ?, ?> value, HamletDescription mismatchDescription) {
        // This is called when there's mismatch in downstream
        // Let's print root object value

        mismatchDescription.setDepth(4);
        mismatchDescription.appendLocation(location);
        mismatchDescription.appendValue(value.value());
    }

    @Override
    protected void internalDescribeTo(HamletDescription description) {
        description.setDepth(8);
    }
}
