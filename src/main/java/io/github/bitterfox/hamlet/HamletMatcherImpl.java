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

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

class HamletMatcherImpl<S, T, M extends Matcher<S>> extends DiagnosingMatcher<S> implements HamletMatcher<S, T, M> {
    private final HamletMatcherImpl<S, ?, ?> upstream;
    private final Function<S, ?, T> function;
    private final LetMatcher<? super T, ?> matcher;

    public HamletMatcherImpl(HamletMatcherImpl<S, ?, ?> upstream, Function<S, ?, T> function, LetMatcher<? super T, ?> matcher) {
        this.upstream = upstream;
        this.function = function;
        this.matcher = matcher;
    }

    public HamletMatcherImpl(HamletMatcherImpl<S, ?, ?> upstream, Function<S, ?, T> function, Matcher<? super T> matcher) {
        this.upstream = upstream;
        this.function = function;
        this.matcher = new LetMatcher<>(java.util.function.Function.identity(), matcher);
    }

    @Override
    public <U> HamletMatcherImpl<S, U, M> as(Class<U> clazz) {
        return new HamletMatcherImpl<>(
                this,
                function.andThen(t -> (U) t),
                Matchers.instanceOf(clazz));
    }

    @Override
    public <U> HamletMatcherImpl<S, T, M> let(java.util.function.Function<? super T, ? extends U> function,
                                              Matcher<? super U> matcher) {
        if (this.matcher == null) {
            return new HamletMatcherImpl<>(this.is(Matchers.notNullValue()), this.function, new LetMatcher<>(function, matcher));
        } else {
            return new HamletMatcherImpl<>(this, this.function, new LetMatcher<>(function, matcher));
        }
    }

    @Override
    public <U> HamletMatcher<S, U, HamletMatcher<S, T, M>> letIn(
            java.util.function.Function<? super T, ? extends U> function) {
        return new HamletMatcherImpl<>(this, this.function.andThen(function), null);
    }

    @Override
    public HamletMatcherImpl<S, T, M> is(Matcher<? super T> matcher) {
        return new HamletMatcherImpl<>(this, function, new LetMatcher<>(java.util.function.Function.identity(), matcher));
    }

    @Override
    public M end() {
        return (M) new HamletMatcherImpl<>(this, function.previousFunction(), null);
    }

    @Override
    public void describeTo(Description description) {
        if (!(description instanceof HamletDescription)) {
            description = new HamletDescription(description);
        }

        if (upstream != null) {
            upstream.describeTo(description);
            if (upstream.matcher != null) {
            }
        }
        if (matcher != null) {
            matcher.describeTo(description);
        }

        // TODO
    }

    @Override
    protected boolean matches(Object item, Description mismatchDescription) {
        if (!(mismatchDescription instanceof HamletDescription)) {
            mismatchDescription = new HamletDescription(mismatchDescription);
        }
        if (upstream != null) {
            boolean matched = upstream.matches(item, mismatchDescription);
            if (!matched) {
                return false;
            }
        }
        if (matcher != null) {
            return matcher.matches(function.apply((S) item), mismatchDescription);
        }
        return true;
    }
}
