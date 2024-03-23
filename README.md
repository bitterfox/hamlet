# Hamlet

Waist a time to write Hamcrest Matchers for objects?
Let's use this to reduce the time to write a Matchers and enjoy coding tests!

Hamlet provides fluent interface to write Hamcrest Matchers for any object in Java, with Java 1.8's Lambda
and Method Reference.

You'll write assertion for object with Hamcrest Matchers like this:

```java
User user;
assertThat(user, is(new User(...)));

// However, construction of object might be noisy
// Since too large constructor or requires many setters call

// Sometimes you write assert for a only few fields
assertThat(user.getId(), is(1L));
assertThat(user.getName(), is("name"));

// Let's assume user has Follower(s) and you'd like to test follow list
// Again, we check some fields of Follower only
// We want to assert user has follower with id=5

// You may write like this nowadays
List<User> followers = user.getFollowers();
assertThat(followers.stream().map(User::getId).collect(Collectors.toList()), hasItem(is(5L)));
```

Hamlet let you write above scenario in better way!

```java
User user;

assertThat(
        user,
        Hamlet.let(User::getId, is(1L))
              .let(User::getName, is("name")));

// Hamlet#let starts fluent interface to build Matcher for object
// You can specify the getter using the method reference for 1st argment and
// specify Matcher for returned value by the method reference
// So this call chain creats Matcher for User assert id is 1L and name is "name"

// This makes you to write assertion for follower simpler and more declarative way

assertThat(
        user,
        Hamlet.let(User::getId, is(1L))
              .let(User::getName, is("name"))
              .let(User::getFollowers,
                   hasItem(Hamlet.let(User::getId, is(5))
              )));

// You can read this assertion as
// assert user that id is 1L, name is "name" and follower has item that id is 5L

// Also Hamlet can generate better output to diagnosis assertion failure
// Let's assume user doesn't have such follower in above example
// Hamlet can generate following description for Hamcrest and assertThat

java.lang.AssertionError:
Expected:
        io.github.bitterfox.hamlet.HamletTest.test(HamletTest.java:106) is <1L>
        io.github.bitterfox.hamlet.HamletTest.test(HamletTest.java:107) is "name"
        io.github.bitterfox.hamlet.HamletTest.test(HamletTest.java:108) a collection containing
            io.github.bitterfox.hamlet.HamletTest.test(HamletTest.java:109) is <5L>]
but:
        io.github.bitterfox.hamlet.HamletTest.test(HamletTest.java:108) mismatches were: [
        io.github.bitterfox.hamlet.HamletTest.test(HamletTest.java:109) was <90L>]
at org.hamcrest.MatcherAssert.assertThat(MatcherAssert.java:20)
at org.hamcrest.MatcherAssert.assertThat(MatcherAssert.java:6)
at io.github.bitterfox.hamlet.HamletTest.test(HamletTest.java:104)

// You see where is the underlying Matcher defined and you can jump to the code from IDE output panel
```

# Getting started

Add dependency to your project
```kotlin
dependencies {
    implementation("io.github.bitterfox:hamlet:0.0.2")
}
```

# Interface

# Hamlet static methods
You can create Hamlet Matcher from `io.github.bitterfox.hamlet.Hamlet#let`.
`Hamlet#let` has 2 overloads.

```java
Hamlet.let(User.class); // Hamlet.let(Class<T>)
// You can start Hamlet Matcher specifying class `isA` Matcher is added

Hamlet.let(User::getId, is(1L)) // Hamlet.let(Function<T, U>, Matcher<U>)
// You can also start Hamlet Matcher for specifying Function and Matcher
```

`Hamlet.let` returns `HamletMatcher` with following methods

## HamletMatcher#as
```java
hamletMatcher.as(AnotherClass.class); // HamletMatcher.as(Class<T>)
// You can change the expected type

hamletMatcher.let(User::name, is("name")) // HamletMatcher.let(Function<T, U>, Matcher<U>)

hamletMatcher.letIn(User::getFollowers) // HamletMatcher.letIn(Function<T, U>)
// This map object to another type using the function and you can continue adding matcher

hamletMatcher.is(is("name")) // HamletMatcher.is(Matcher<T>)
// Add matcher for current mapped value

hamletMatcher.end()
// Finish letIn context and back to previous mapping
```

For `letIn`, explain it with example.
In above example, we assert followers in User.
But if the object structure is deep, the indent goes to too right in the editor.
`letIn` allows you to write matcher flatten with mapping object.
```java
assertThat(
        user,
        Hamlet.let(User::getId, is(1L))
              .let(User::getName, is("name"))
              .letIn(User::getFollowers) // Map to follower: List<User>
              .is(hasItem(Hamlet.let(User::getId, is(5)))) // Add Matcher for User::getFollowers
              .let(List::size, is(3L))
              .end() // Back to User
              .let(User::updatedTime, is(timestamp))
);
```
