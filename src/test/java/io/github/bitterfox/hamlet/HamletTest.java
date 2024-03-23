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

import static io.github.bitterfox.hamlet.Hamlet.let;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

class HamletTest {
    private record User(
            long id,
            String name,
            long createdTime,
            long updatedTime,
            List<BankAccount> bankAccounts
    ) {}

    private record BankAccount(
            long id,
            String currency,
            long amount
    ) {}

    @Test
    void test() {
        User user = null;

        assertThat(user,
                Hamlet.let(User::id, is(10))
                      .let(User::name, is("myname"))
                      .let(User::createdTime, is(1234))
                      .let(User::bankAccounts,
                           contains(
                                   let(BankAccount::id, is(90))))
                      .letIn(User::bankAccounts)
                      .is(contains(let(BankAccount::id, is(90))))
                      .letIn(List::getFirst)
                      .let(BankAccount::currency, is("$"))
                      .end()
                      .end()
                      .end()
        );
    }

    private <T> void assertThat(T t, Matcher<? super T> matcher) {}
}
