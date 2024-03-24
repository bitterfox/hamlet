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
import org.hamcrest.Description.NullDescription;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

class HamletMatcherImpl<S, P, T, M extends Matcher<S>> extends DiagnosingMatcher<S> implements HamletMatcher<S, T, M> {
    private final HamletMatcherImpl<S, ?, P, ?> upstream;
    private final Function<? super P, ? extends T> letIn;
    private final LetMatcher<? super T, ?> matcher;

    public HamletMatcherImpl(HamletMatcherImpl<S, ?, P, ?> upstream, Function<? super P, ? extends T> letIn, LetMatcher<? super T, ?> matcher) {
        this.upstream = upstream;
        this.letIn = letIn;
        this.matcher = matcher;
    }

    public HamletMatcherImpl(HamletMatcherImpl<S, ?, P, ?> upstream, Function<? super P, ? extends T> letIn, Matcher<? super T> matcher) {
        this.upstream = upstream;
        this.letIn = letIn;
        this.matcher = new LetMatcher<>(java.util.function.Function.identity(), matcher);
    }

    @Override
    public <U> HamletMatcherImpl<S, T, U, M> as(Class<U> clazz) {
        return new HamletMatcherImpl<>(
                this,
                null,
                Matchers.instanceOf(clazz));
    }

    @Override
    public <U> HamletMatcherImpl<S, T, T, M> let(java.util.function.Function<? super T, ? extends U> function,
                                              Matcher<? super U> matcher) {
        if (this.matcher == null) {
            return new HamletMatcherImpl<>(this.it(Matchers.notNullValue()), null, new LetMatcher<>(function, matcher));
        } else {
            return new HamletMatcherImpl<>(this, null, new LetMatcher<>(function, matcher));
        }
    }

    @Override
    public <U> HamletMatcherImpl<S, T, U, HamletMatcher<S, T, M>> letIn(Function<? super T, ? extends U> function) {
        return new HamletMatcherImpl<>(this, function, null);
    }

    @Override
    public HamletMatcherImpl<S, T, T, M> it(Matcher<? super T> matcher) {
        if (matcher instanceof LetMatcher) {
            return new HamletMatcherImpl<>(this, null,
                                           (LetMatcher<? super T, ?>) matcher);
        } else {
            return new HamletMatcherImpl<>(this, null, new LetMatcher<>(java.util.function.Function.identity(), matcher));
        }
    }

    @Override
    public M end() {
        return (M) new HamletMatcherImpl(this, null, null) {
            @Override
            public MappedValue requestValue(Object item) {
                MappedValue mappedValue = super.requestValue(item);

                MappedValue letInValue = mappedValue;
                while (letInValue != null && mappedValue.letInScope <= letInValue.letInScope()) {
                    letInValue = letInValue.previousValue;
                }
                if (letInValue == null) {
                    return new MappedValue(mappedValue, null, false, false);
                } else {
                    return new MappedValue(mappedValue.previousValue, letInValue.value(), false, true);
                }
            }
        };
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
    }

    @Override
    protected boolean matches(Object item, Description mismatchDescription) {
        MappedValue<T, ?, ?> value = requestValue(item);

        HamletDescription desc = mismatchDescription instanceof HamletDescription
                                 ? (HamletDescription) mismatchDescription
                                 : new HamletDescription(mismatchDescription);

        return internalMatches(value, desc);
    }

    private boolean internalMatches(MappedValue<T, ?, ?> value, HamletDescription mismatchDescription) {

        if (upstream != null) {
            boolean matched = upstream.internalMatches((MappedValue<P, ?, ?>) value.previousValue(), mismatchDescription);
            if (!matched) {
                return false;
            }
        }
        if (matcher != null) {
            try {
                return matcher.matches(value.value(), mismatchDescription);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public MappedValue<T, P, ?> requestValue(Object item) {
        if (upstream == null) {
            return new MappedValue<>(null, (T) item, false);
        }

        MappedValue<P, ?, ?> value = upstream.requestValue(item);

        if (letIn == null) {
            // pass through, T == P
            return new MappedValue<>(value, (T) value.value(), false);
        }

        return new MappedValue<>(value, letIn.apply(value.value()), true);
    }
}
