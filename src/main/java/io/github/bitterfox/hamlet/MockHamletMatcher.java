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

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class MockHamletMatcher<S, T, M extends Matcher<S>> implements HamletMatcher<S, T, M> {
    M m;

    public MockHamletMatcher(M m) {
        this.m = m;
    }

    @Override
    public <U> HamletMatcher<S, U, M> as(Class<U> clazz) {
        return null;
    }

    @Override
    public <U> HamletMatcher<S, T, M> let(Function<? super T, ? extends U> function,
                                          Matcher<? super U> matcher) {
        return this;
    }

    @Override
    public <U> HamletMatcher<S, U, HamletMatcher<S, T, M>> letIn(
            Function<? super T, ? extends U> function) {
        return new MockHamletMatcher<>(this);
    }

    @Override
    public HamletMatcher<S, T, M> it(Matcher<? super T> matcher) {
        return this;
    }

    @Override
    public M end() {
        return m;
    }

    @Override
    public boolean matches(Object actual) {
        return true;
    }

    @Override
    public void describeMismatch(Object actual, Description mismatchDescription) {

    }

    @Override
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

    }

    @Override
    public void describeTo(Description description) {

    }
}
