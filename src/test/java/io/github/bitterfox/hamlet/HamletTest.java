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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class HamletTest {

    interface Id {
        long id();
    }

    class User implements Id {
        long id;
        String name;
        long createdTime;
        long updatedTime;
        List<BankAccount> bankAccounts;

        public User(long id, String name, long createdTime, long updatedTime, List<BankAccount> bankAccounts) {
            this.id = id;
            this.name = name;
            this.createdTime = createdTime;
            this.updatedTime = updatedTime;
            this.bankAccounts = bankAccounts;
        }

        public long id() {
            return id;
        }

        public String name() {
            return name;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public long createdTime() {
            return createdTime;
        }

        public long updatedTime() {
            return updatedTime;
        }

        public List<BankAccount> bankAccounts() {
            return bankAccounts;
        }

        @Override
        public String toString() {
            return "User{" +
                   "id=" + id +
                   ", name='" + name + '\'' +
                   ", createdTime=" + createdTime +
                   ", updatedTime=" + updatedTime +
                   ", bankAccounts=" + bankAccounts +
                   '}';
        }
    }

    class BankAccount {
        long id;
        String currency;
        long amount;

        public BankAccount(long id, String currency, long amount) {
            this.id = id;
            this.currency = currency;
            this.amount = amount;
        }

        public long id() {
            return id;
        }

        public String currency() {
            return currency;
        }

        public long amount() {
            return amount;
        }

        @Override
        public String toString() {
            return "BankAccount{" +
                   "id=" + id +
                   ", currency='" + currency + '\'' +
                   ", amount=" + amount +
                   '}';
        }
    }

    interface I {}

    @Test
    void test() {
        User user = new User(
                10,
                "myname",
                1234,
                900,
//                Arrays.asList(new BankAccount(90, "$", 10))
                Arrays.asList((BankAccount) null, new BankAccount(90L, "$", 100))
        );

        assertThat(
                user,
                Hamlet.let(User::getId, is(10L))
                      .let(User::getName, is("myname"))
//                      .let(User::bankAccounts,
//                           hasItem(Hamlet.let(BankAccount::id, is(0L))))
                        .letIn(User::bankAccounts)
//                        .let(List::size, is(4))
                        .it(hasItem(Hamlet.let(BankAccount::id, is(0L))))
        );

        HamletMatcherStage<User, ?, ?, ?, ?> matcher = (HamletMatcherStage<User, ?, ?, ?, ?>)
                Hamlet.let(User::id, is(10L))
                      .let(User::name, is("myname"))
                      .let(User::createdTime, is(1234L))
                      .let(User::bankAccounts, hasItem(
                              Hamlet.let(BankAccount::id, is(90L))))
                      .letIn(User::bankAccounts)
                      .it(Matchers.isA(List.class))
                      .it(hasItem(Hamlet.let(BankAccount::id, is(00L))))
                      .letIn(l -> l.get(1))
                      .let(BankAccount::currency, is("$"))
                      .end()
                      .end()
                      .let(User::name, is("myname"));

        MappedValue<?, ?, ?, ?> value = matcher.requestValue(user);

        assertThat(
                user,
                matcher
        );

        assertThat(
                user,
                Hamlet.let(Id::id, is(10L))
        );

        assertThat(
                user.bankAccounts(),
                hasItem(Hamlet.let(BankAccount::id, is(90L)))
        );

        assertThat(
                user,
                Hamlet.let(User.class)
                        .letIn(User::bankAccounts)
                        .letIn(l -> l.get(1))
//                        .letIn(BankAccount::currency)
//                        .it(is("yen"))
                        .let(BankAccount::currency, is("$"))
        );
    }

    @Test
    void testNull() {
        User user = null;
        assertThat(
                user,
                Hamlet.let()
                      .it(nullValue())
        );
    }

    @Test
    void testNoIdempotent() {
        class Test {
            AtomicInteger i = new AtomicInteger();

            int process (){
                return i.incrementAndGet();
            }
        }

        Test test = new Test();

        Matcher<Integer> m = Hamlet.let(Integer.class);
//                                   .it(is(0));

        assertThat(test,
                   Hamlet.let(Test.class)
                         .letIn(Test::process) // 1
                         .it(m)
                         .it(is(1))
                         .end()
                         .letIn(Test::process) // 2
                         .it(m)
                         .it(m)
                         .end()
                         .let(Test::process, m) // 3
                         .let(Test::process, m) // 4
        );
    }
}
