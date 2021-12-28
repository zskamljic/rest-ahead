# RestAhead - an AOT REST client

This project draws inspiration from projects such as [Retrofit](https://square.github.io/retrofit/)
and [Feign](https://github.com/OpenFeign/feign), but with a twist: your services are generated at compile time,
preventing any issues from being found at runtime.

The aim of this project is to have as much validation done at compile time, rather than runtime. Additionally, since the
code is generated at compile time there is no need for reflection. The generated code can be inspected for a no-magic
approach, which also means easier debugging and easier integration with tools like [GraalVM](https://www.graalvm.org/)

## Features

- [Headers](#headers)
- [Query](#queries)

## Introduction

Much like other clients, the service is declared as following:

```java
public interface HttpBinService {
    @Get("/get")
    Response performGet();
}
```

An instance of this class can be generated by using RestAhead class.

```jshelllanguage
var service = RestAhead.builder("https://httpbin.org")
    .build(HttpBinService.class);
```

Calls can then be performed simply by calling the instance of the interface:

```jshelllanguage
var response = service.performGet();
```

## Options

There are multiple options you have when generating requests, all of which will be done automatically when building your
project.

### Function return type

Currently, there are two supported return types, selected based on your interface declaration:

- void
- Response

### Headers

Adding headers is possible by using the `@Header` annotation. Valid parameters for headers are either primitive types,
their boxed counterparts, Strings, instances of UUID or collections/arrays of them.

Using multiple annotations with the same value will add extra headers. The following declarations will generate requests
that behave the same:

```java
interface Service {
    @Get
    void performGet(@Header("Some-Header") String first, @Header("Some-Header") String second);

    @Get
    void performGetVarargs(@Header("Some-Header") String... headers);

    @Get
    void performGetArray(@Header("Some-Header") String[] headers);

    // Can also use List, Set etc.
    @Get
    void performGetCollection(@Header("Some-Header") Collection<String> headers);
}
```

### Queries

Queries can be added to a request in two ways, seen below. Collections, arrays and varargs types are allowed.

```java
interface Service {
    @Get("/query?q=value")
    void getWithQuery(); // will use the preset value from path

    @Get("/query")
    void getWithParam(@Query("q") String query); // will use the parameter
}
```

### Call exceptions

By default, no exceptions need to be declared to execute calls, but beware! An unchecked exception (RestException) will
be thrown in case there was an exception thrown during execution. You can also add `throws` declaration for either or
both exceptions that are likely to occur: `IOException`, `InterruptedException`, to make sure they are handled. If
either of these is not specified in the signature, `RestException` will still be thrown, wrapping the other one, for
example:

```java
public interface HttpBinService {
    // Will throw a RestException if any errors occur
    @Get("/get")
    Response performGet();

    // Allows you to handle IOException, RestException wrapping InterruptedException may still occur
    @Get("/get")
    Response performGet2() throws IOException;

    // Allows you to handle both exception, no RestException will be thrown
    @Get("/get")
    Response performGet3() throws IOException, InterruptedException;
}
```

### Custom client

The `RestAhead` builder declares an interface `RestClient` that allows you to implement custom clients. By default, if
no client is specified, Java HTTP client is used.

## License

Project uses Apache 2.0 license. More info in [license file](LICENSE)