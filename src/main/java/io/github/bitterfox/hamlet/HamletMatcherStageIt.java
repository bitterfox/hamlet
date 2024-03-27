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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class HamletMatcherStageIt<S, P, T, M extends Matcher<S>> extends HamletMatcherStage<S, P, T, T, M> {
    public HamletMatcherStageIt(HamletMatcherStage<S, ?, P, ?, ?> upstream,
                                LetMatcher<? super T, ?> mather) {
        super(upstream, mather);
    }

    @Override
    protected void internalDescribeTo(HamletDescription description) {
        super.internalDescribeTo(description);

        matcher.describeTo(description);
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
            this.describeMismatchLetIn(value, mismatchDescription);
            String string = desc.toString();
//            mismatchDescription.appendText(string);
            mismatchDescription.plusDepth(4);
            try (BufferedReader br = new BufferedReader(new StringReader(string))) {
                br.lines()
                  .forEach(l -> mismatchDescription.appendIndent().appendText(l)
                                                   .appendText(System.lineSeparator()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return match;
    }

    @Override
    MappedValue<T, P, T, ?> requestValue(MappedValue<P, ?, ?, ?> upstreamValue) {
        return upstreamValue.identity();
    }
}
