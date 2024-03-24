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
import org.hamcrest.StringDescription;

public class HamletMatcherStageIt<S, P, T, M extends Matcher<S>> extends HamletMatcherStage<S, P, T, T, M> {
    public HamletMatcherStageIt(HamletMatcherStage<S, ?, P, ?, ?> upstream,
                                LetMatcher<? super T, ?> mather) {
        super(upstream, mather);
    }

    @Override
    protected boolean internalMatches(MappedValue<T, ?, ?, ?> value, HamletDescription mismatchDescription,
                                      boolean upstreamMatched) {
        if (matcher == null) {
            return upstreamMatched;
        }

        if (!upstreamMatched) {
            return false;
        }

        StringDescription desc = new StringDescription();
        boolean match = matcher.matches(value.value, new HamletDescription(desc));
        if (!match) {
            this.describeMismatchLetIn(mismatchDescription);
            mismatchDescription.appendText(desc.toString());
        }
        return match;
    }

    @Override
    MappedValue<T, P, T, ?> requestValue(MappedValue<P, ?, ?, ?> upstreamValue) {
        return upstreamValue.identity();
    }
}
