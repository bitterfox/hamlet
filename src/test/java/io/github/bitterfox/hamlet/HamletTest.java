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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.MatcherAssert;
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

        public long createdTime() {
            return createdTime;
        }

        public long updatedTime() {
            return updatedTime;
        }

        public List<BankAccount> bankAccounts() {
            return bankAccounts;
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


        MatcherAssert.assertThat(
                user,
                Hamlet.let(User::id, is(10L))
                      .let(User::name, is("myname"))
                      .let(User::createdTime, is(1234L))
                      .let(User::bankAccounts, hasItem(
                              Hamlet.let(BankAccount::id, is(90L))))
                      .letIn(User::bankAccounts)
                      .is(Matchers.hasItem(Hamlet.let(BankAccount::id, is(90L))))
                      .letIn(l -> l.get(1))
                      .let(BankAccount::currency, is("$"))
                      .end()
                      .end()
                      .end()
        );

        MatcherAssert.assertThat(
                user,
                Hamlet.let(Id::id, is(10L))
        );
    }
}