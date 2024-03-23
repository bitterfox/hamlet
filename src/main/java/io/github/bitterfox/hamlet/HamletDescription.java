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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

public class HamletDescription implements Description {
    private final Description delegate;
    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 8);

    public HamletDescription(Description delegate) {
        this.delegate = delegate;
    }

    @Override
    public Description appendText(String text) {
        return delegate.appendText(text);
    }

    public Description appendLocation(StackTraceElement location) {
        return appendText(System.lineSeparator())
                .appendText(Stream.generate(() -> " ").limit(DEPTH.get()).collect(Collectors.joining()))
                .appendText(location == null ? "(unknown)" : location.toString())
                .appendText(" ");
    }

    public Description getDelegate() {
        return delegate;
    }

    public void plusDepth(int depth) {
        DEPTH.set(DEPTH.get() + depth);
    }
    public void minusDepth(int depth) {
        DEPTH.set(DEPTH.get() - depth);
    }

    @Override
    public Description appendDescriptionOf(SelfDescribing value) {return delegate.appendDescriptionOf(value);}

    @Override
    public Description appendValue(Object value) {return delegate.appendValue(value);}

    @Override
    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        return delegate.appendValueList(start, separator, end, values);
    }

    @Override
    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        return delegate.appendValueList(start, separator, end, values);
    }

    @Override
    public Description appendList(String start, String separator, String end,
                                  Iterable<? extends SelfDescribing> values) {
        return delegate.appendList(start, separator, end, values);
    }
}
