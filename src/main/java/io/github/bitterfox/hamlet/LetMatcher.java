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
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

public class LetMatcher<T, U> extends DiagnosingMatcher<T> {
    private final Function<? super T, ? extends U> function;
    private final Matcher<U> matcher;
    private final StackTraceElement location;

    public LetMatcher(Function<? super T, ? extends U> function, Matcher<U> matcher) {
        this.function = function;
        this.matcher = matcher;
        this.location = findLocation();
    }

    @Override
    protected boolean matches(Object item, Description mismatchDescription) {
        U value = function.apply((T) item);
        if (matcher.matches(value)) {
            return true;
        }

        HamletDescription desc;
        if (mismatchDescription instanceof HamletDescription) {
            desc = (HamletDescription) mismatchDescription;
        } else {
            desc = new HamletDescription(mismatchDescription);
        }
        desc.appendLocation(location);
        desc.plusDepth(4);
        try {
            matcher.describeMismatch(value, mismatchDescription);
        } finally {
            desc.minusDepth(4);
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        HamletDescription desc;
        if (description instanceof HamletDescription) {
            desc = (HamletDescription) description;
        } else {
            desc = new HamletDescription(description);
        }
        desc.appendLocation(location);
        desc.plusDepth(4);
        try {
            matcher.describeTo(desc);
        } finally {
            desc.minusDepth(4);
        }
    }

    private StackTraceElement findLocation() {
        for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
            if (!stackTrace.getClassName().equals(Thread.class.getName())
                && !stackTrace.getClassName().equals(LetMatcher.class.getName())
                && !stackTrace.getClassName().equals(Hamlet.class.getName())
                && !stackTrace.getClassName().equals(HamletMatcherImpl.class.getName())) {
                return stackTrace;
            }
        }
        return null;
    }
}
