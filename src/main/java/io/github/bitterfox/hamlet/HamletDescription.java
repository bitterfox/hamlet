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
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

class HamletDescription implements Description {
    private final Description delegate;
    private int depth = 0;

    public HamletDescription(Description delegate) {
        this.delegate = delegate;
    }

    @Override
    public Description appendText(String text) {
        delegate.appendText(text);
        return this;
    }

    public Description appendIndent() {
        appendText(spaces(depth));
        return this;
    }

    public Description appendLocation(StackTraceElement location) {
        appendText(System.lineSeparator())
                .appendText(spaces(depth))
                .appendText(location == null ? "(unknown)" : location.toString())
                .appendText(" ");
        return this;
    }

    public Description getDelegate() {
        return delegate;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
    public void plusDepth(int depth) {
        this.depth += depth;
    }
    public void minusDepth(int depth) {
        this.depth -= depth;
    }

    public int getDepth() {
        return depth;
    }

    private String spaces(int len) {
        return Stream.generate(() -> " ").limit(len).collect(Collectors.joining());
    }

    @Override
    public Description appendDescriptionOf(SelfDescribing value) {
        value.describeTo(this);
        return this;

//        return delegate.appendDescriptionOf(value);
    }

    @Override
    public Description appendValue(Object value) {
        delegate.appendValue(value);
        return this;
    }

    @Override
    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        delegate.appendValueList(start, separator, end, values);
        return this;
    }

    @Override
    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        delegate.appendValueList(start, separator, end, values);
        return this;
    }

    @Override
    public Description appendList(String start, String separator, String end,
                                  Iterable<? extends SelfDescribing> values) {
        delegate.appendList(start, separator, end, values);
        return this;
    }

    public void appendException(Exception failure) {
        appendText(failure.getClass().getName()).appendText(System.lineSeparator());
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            failure.printStackTrace(pw);

            try (StringReader sr = new StringReader(sw.toString());
                 BufferedReader br = new BufferedReader(sr)) {
                br.lines().forEach(l -> this.appendText(spaces(depth + 4))
                                            .appendText(l)
                                            .appendText(System.lineSeparator()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
