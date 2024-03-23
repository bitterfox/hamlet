# Hamlet

Are you tired of writing verbose Hamcrest Matchers for objects? Hamlet is here to streamline the process, saving you time and making test code writing more enjoyable!

Hamlet provides a fluent interface for creating Hamcrest Matchers for any Java object, utilizing Java 1.8's Lambda and Method Reference features.

Normally, you might write assertions for an object using Hamcrest Matchers like this:

```java
User user;
assertThat(user, is(new User(...)));

// However, constructing objects can be cumbersome
// due to large constructors or the need for multiple setter calls.

// Sometimes you only want to assert a few fields
assertThat(user.getId(), is(1L));
assertThat(user.getName(), is("name"));

// Imagine you want to test a user's list of followers
// and only check specific fields of the Follower object.
// For instance, you want to verify that the user has a follower with id=5.

// You might currently write something like this:
List<User> followers = user.getFollowers();
assertThat(followers.stream().map(User::getId).collect(Collectors.toList()), hasItem(is(5L)));
```

With Hamlet, you can express the above scenario in a cleaner and more elegant way!

```java
User user;

assertThat(
        user,
        Hamlet.let(User::getId, is(1L))
              .let(User::getName, is("name")));

// Hamlet#let initiates a fluent interface to construct a Matcher for an object.
// You can designate a getter with a method reference as the first argument and
// pair it with a Matcher for the method's return value.
// This chain of calls creates a Matcher to assert that the User's id is 1L and name is "name".

// This approach simplifies and makes assertions for followers more declarative.

assertThat(
        user,
        Hamlet.let(User::getId, is(1L))
              .let(User::getName, is("name"))
              .let(User::getFollowers,
                   hasItem(Hamlet.let(User::getId, is(5L))
              )));

// This assertion reads as:
// Assert that the user has an id of 1L, a name of "name", and a follower with an id of 5L.

// Hamlet also generates more informative output for diagnosing assertion failures.
// If the user does not have the expected follower, Hamlet can generate a description like the following:


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

// This output shows exactly where the mismatch occurred, allowing you to quickly navigate to the code from your IDE's output panel.
```

# Getting Started

Add the following dependency to your project:
```kotlin
dependencies {
    implementation("io.github.bitterfox:hamlet:0.0.3")
}
```

# Interface

## Hamlet Static Methods
Create a Hamlet Matcher using `io.github.bitterfox.hamlet.Hamlet#let`. There are three overloads of `Hamlet#let`.

```java
Hamlet.let(); // Starts a Hamlet Matcher without any predefined matcher.
// Useful for testing null values: Hamlet.let().is(nullValue())
// Generally not useful otherwise.

Hamlet.let(User.class); // Initializes a Hamlet Matcher that expects instances of the specified class, i.e. expect non null.

Hamlet.let(User::getId, is(1L)); // Starts a Hamlet Matcher with a notNullValue Matcher, a specified Function, and a Matcher.
```

`Hamlet.let` returns a `HamletMatcher` with the following methods:

## HamletMatcher Methods
```java
hamletMatcher.as(AnotherClass.class); // Changes the expected type.

hamletMatcher.let(User::getName, is("name")); // Adds a Matcher for a specific getter.

hamletMatcher.letIn(User::getFollowers); // Maps the object to another type and allows for continued Matcher addition for the type.

hamletMatcher.it(is("name")); // Adds a Matcher for the current mapped value.

hamletMatcher.end(); // Ends the letIn context and returns to the previous mapping.
```

For `letIn`, consider the following example. Here, we assert properties of the followers within a User object. If the object structure is deeply nested, code indentation can become unwieldy. `letIn` allows you to write matchers in a flattened manner by mapping the object.

```java
assertThat(
        user,
        Hamlet.let(User::getId, is(1L))
              .let(User::getName, is("name"))
              .letIn(User::getFollowers) // Maps to followers: List<User>
              .it(hasItem(Hamlet.let(User::getId, is(5L)))) // Adds a Matcher for User::getFollowers
              .let(List::size, is(3L))
              .end() // Returns to User context
              .let(User::updatedTime, is(timestamp))
);
```
