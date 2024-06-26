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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;

import org.hamcrest.Matcher;

public class Hamlet {
    public static <T> HamletMatcher<T, T, ?> let() {
        return new HamletMatcherStageRoot<>();
    }

    public static <T> HamletMatcher<T, T, ?> let(Class<T> clazz) {
        return new HamletMatcherStageRoot<>(isA(clazz));
    }

    public static <T, U> HamletMatcher<T, T, ?> let(MyFunction<? super T, ? extends U> function, Matcher<? super U> matcher) {
        return new HamletMatcherStageRoot<T, Matcher<T>>(is(notNullValue()))
                .let(function, matcher);
    }
}
