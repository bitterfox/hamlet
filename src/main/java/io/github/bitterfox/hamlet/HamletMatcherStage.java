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

import static io.github.bitterfox.hamlet.LanguageUtil.findLocation;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Description.NullDescription;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

abstract class HamletMatcherStage<S, P, T, L, M extends Matcher<S>> extends DiagnosingMatcher<S> implements HamletMatcher<S, T, M> {
    private final HamletMatcherStage<S, ?, P, ?, ?> upstream;
    protected final LetMatcher<? super L, ?> matcher;
    protected final StackTraceElement location;

    private final ThreadLocal<List<MappedValue<T, ?, L, ?>>> lastValues = ThreadLocal.withInitial(() -> new ArrayList<>());

    public HamletMatcherStage(HamletMatcherStage<S, ?, P, ?, ?> upstream, LetMatcher<? super L, ?> matcher) {
        this.upstream = upstream;
        this.matcher = matcher;
        this.location = findLocation();
    }

    @Override
    public <U> HamletMatcherStage<S, T, U, U, M> as(Class<U> clazz) {
        return new HamletMatcherStageAs<>(this, clazz);
    }

    @Override
    public <U> HamletMatcherStage<S, T, T, U, M> let(MyFunction<? super T, ? extends U> function,
                                                     Matcher<? super U> matcher) {
        if (this.matcher == null) {
            return new HamletMatcherStageLet<>(this.it(Matchers.notNullValue()), new LetMatcher<>(/*function, */matcher), function);
        } else {
            return new HamletMatcherStageLet<>(this, new LetMatcher<>(/*function, */matcher), function);
        }
    }

    @Override
    public <U> HamletMatcherStage<S, T, U, U, HamletMatcher<S, T, M>> letIn(MyFunction<? super T, ? extends U> function) {
        return new HamletMatcherStageLetIn<>(this, function);
    }

    @Override
    public HamletMatcherStage<S, T, T, T, M> it(Matcher<? super T> matcher) {
        return new HamletMatcherStageIt<>(
                this,
                matcher instanceof LetMatcher
                ? (LetMatcher<? super T, ?>) matcher
                : new LetMatcher<>(matcher));
    }

    @Override
    public M end() {
        return (M) new HamletMatcherStageEnd<>(this);
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
        internalDescribeTo((HamletDescription) description);
    }

    protected void internalDescribeTo(HamletDescription description) {
        if (matcher != null) {
            matcher.describeTo(description);
        }
    }

    @Override
    protected boolean matches(Object item, Description mismatchDescription) {
        // matches called twice in case of failure
        // 1st is to check whether object matches
        // 2nd is to create a description when match failed

        // ugly hack to avoid run functions many times
        // 1st is called with NullDescription
        MappedValue<T, ?, L, ?> value;
        if (mismatchDescription instanceof NullDescription) {
            value = requestValue(item);
            lastValues.get().add(value);
        } else {
            value = lastValues.get().remove(lastValues.get().size() - 1);
        }

        HamletDescription desc = mismatchDescription instanceof HamletDescription
                                 ? (HamletDescription) mismatchDescription
                                 : new HamletDescription(mismatchDescription);

        return __matches(value, desc);
    }

    private boolean __matches(MappedValue<T, ?, ?, ?> value, HamletDescription mismatchDescription) {
        boolean matched = true;
        if (upstream != null) {
            matched = upstream.__matches((MappedValue<P, ?, ?, ?>) value.previousValue(), mismatchDescription);
        }
        return internalMatches(value, mismatchDescription, matched);
    }

    protected boolean internalMatches(MappedValue<T, ?, ?, ?> value, HamletDescription mismatchDescription, boolean upstreamMatched) {
        if (!upstreamMatched) {
            return upstreamMatched;
        }

        if (value.failure != null) {
            mismatchDescription
                    .appendLocation(location)
                    .appendText(describeMethodReference())
                    .appendText(" throws ");
            mismatchDescription.appendException(value.failure);
            return false;
        }

        if (matcher != null) {
            try {
                return matcher.matches(value.letValue, mismatchDescription, describeMethodReference());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return upstreamMatched;
    }

    MappedValue<T, P, L, ?> requestValue(Object item) {
        if (upstream == null) {
            return new MappedValue<>(null, (T) item, (L) item, null, 0);
        }

        MappedValue<P, ?, ?, ?> value = upstream.requestValue(item);
        return requestValue(value);
    }

    protected String describeMethodReference() {
        return "Unsupported in " + getClass().getName();
    }

    abstract MappedValue<T, P, L, ?> requestValue(MappedValue<P, ?, ?, ?> upstreamValue);
//    {
//        if (upstream == null) {
//            return new MappedValue<>(null, (T) item, (L) item, 0);
//        }
//
//        MappedValue<P, ?, ?, ?> value = upstream.requestValue(item);
//
//        if (value.value == null) {
//            return value.identity();
//        }
//
//        if (letIn != null) {
//            return value.letIn(letIn);
//        }
//
//        if (let != null) {
//            return value.let(let);
//        }
//
//         value: T == P
//        return value.identity();
//    }
}
