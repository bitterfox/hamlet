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

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

class LetMatcher<T, U> extends DiagnosingMatcher<T> {
//    private final Function<? super T, ? extends U> function;
    private final Matcher<U> matcher;
    final StackTraceElement location;

    public LetMatcher(/*Function<? super T, ? extends U> function, */Matcher<U> matcher) {
//        this.function = function;
        this.matcher = matcher;
        this.location = findLocation();
    }

    @Override
    protected boolean matches(Object item, Description mismatchDescription) {
        return matches(item, mismatchDescription, "");
    }

    protected boolean matches(Object item, Description mismatchDescription, String valueDescription) {
        U value;
        try {
//            value = function.apply((T) item);
            value = (U) item;
        } catch (Exception e) {
            throw new RuntimeException(e.getClass() + " at " + location, e);
        }
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
        if (!valueDescription.isEmpty()) {
            desc.appendText(valueDescription)
                    .appendText(" ");
        }
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
        desc.appendLocation(location)
            .appendText("expect it ");
        desc.plusDepth(4);
        try {
            matcher.describeTo(desc);
        } finally {
            desc.minusDepth(4);
        }
    }

}
