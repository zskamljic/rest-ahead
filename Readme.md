# RestAhead - compile time generated REST client

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=zskamljic_rest-ahead&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=zskamljic_rest-ahead)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=zskamljic_rest-ahead&metric=coverage)](https://sonarcloud.io/summary/new_code?id=zskamljic_rest-ahead)

This project draws inspiration from projects such as [Retrofit](https://square.github.io/retrofit/)
and [Feign](https://github.com/OpenFeign/feign), but with a twist: your services are generated at compile time,
preventing any issues from being found at runtime.

The aim of this project is to have as much validation done at compile time, rather than runtime. Additionally, since the
code is generated at compile time there is no need for reflection. The generated code can be inspected for a no-magic
approach.

## Features

- [Adapters](#adapters)
- [Body](#body)
- [Headers](#headers)
- [Interceptors](#interceptors)
- [Path](#paths)
- [Query](#queries)
- [Responses](#response-types)
- [Spring Boot](#spring-boot)

## Introduction

Much like other clients, the service is declared as following:

```java
public interface HttpBinService {
    @Get("/get")
    Future<Response> performGet();
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

Samples of services can be found in `demo` project [here](demo/src/main/java/io/github/zskamljic/restahead/demo/clients)
, examples of obtaining their instances are
in [this directory](demo/src/test/java/io/github/zskamljic/restahead/demo/clients).

## Options

There are multiple options you have when generating requests, all of which will be done automatically when building your
project.

### Response types

Out of the box the following types are supported:

- void
- Response
- Future&lt;Response&gt;
- CompletableFuture&lt;Response&gt;

Other types require you to specify an instance of Converter (rest-ahead-jackson-converter contains an implementation for
Jackson library). This will allow you to use virtually any type that the converter can construct.

Example of using a return type:

```java
interface Service {
    @Get
    void requestIgnoringResponse();

    @Get
    Response requestFullResponse();

    @Get
    Map<String, Object> performGet();

    @Get
    CustomResponseType performGetWithSpecificTarget();
}
```

If you require response headers and the status code as well, two more types can be used along with custom response
types:

```java
interface Service {
    // BodyResponse.body() is an optional that contains the deserialized type in case of success.
    // If non 2xx code is returned InputStream errorBody will be present, that contains untouched response body.
    @Get
    BodyResponse<CustomResponseType> get();

    // BodyAndErrorResponse.body() is an optional that contains the deserialized body in case of success.
    // If non 2xx code is returned the errorBody will contain the deserialized body
    @Get
    BodyAndErrorResponse<CustomResponseType, CustomErrorType> getErrors();
}
```

### Body

You can specify a request body by annotating it with `@Body`. Doing so will make the service require a converter to
serialize the body.

```java
public interface HttpBinService {
    @Post("/post")
    Future<Response> performPost(@Body CustomRequest request);
}
```

#### Form encoding

Sending a form-url-encoded body can be done by adding `@FormUrlEncoded` annotation to the body:

```java
public interface HttpBinService {
    @Post("/post")
    Future<Response> performPost(@FormUrlEncoded @Body CustomRequest request);
}
```

Changing the name of parameters in the form line can be done by using `@FormName` annotation on desired fields:

```java
// This will cause the body to send "first=<value of first>&second=<value of b>"
record SampleFormBody(String first, @FormName("second") String b) {
}

// This will cause the body to send "customName=hello"
class SampleFormClass {
    @FormName("customName")
    String getSomething() {
        return "hello";
    }
}
```

Such bodies do not require a converter, one will be generated for the given type.

Supported types:

- Map<String, String> and inherited classes
- Records composed of primitives, boxed values, String or UUID
- Classes with public, non-static getters returning only primitives, boxed values, String or UUID

#### Multipart encoding

Multipart requests can be executed as following:

```java
public interface MultipartService {
    @Post("/post")
    HttpBinResponse postMultiPart(
        @Part String part,
        @Body @Part("two") String part2,
        @Part File file,
        @Part Path path,
        @Part FilePart part
    );
}
```

Parts can be added manually by using `FilePart` for example, it allows usage of `InputStreams`, `byte[]` etc.

Note that files and paths will be read when the request reads the body - meaning their evaluation is lazy.

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

### Adapters

The default type for all services is `Future<Response>`. While the value can be mapped to `Future<YourObject>` using a
converter directly, sometimes interop with other libraries is required, or maybe you need a blocking call and don't want
to type `.get()` all the time, as well as catch the exceptions. For these cases a default adapter is included, to allow
for blocking calls as evident here:

```java
import java.util.concurrent.CompletableFuture;

interface SampleBlocking {
    @Get
    Future<Response> getFuture();

    @Get
    CompletableFuture<Response> getCompletableFuture();

    @Get
    Response getBlocking();
}
```

All three examples above will perform the same request, but `Future` and `CompletableFuture` will attempt to do this in
non-blocking manner (this depends on the client, default JavaHttpClient supports this), but the last, `Response` will
execute a blocking call.

If you wish to declare your own adapters simply create a class with a method annotated with `@Adapter`:

```java
public class CustomAdapter {
    @Adapter
    public <T> Supplier<T> adapt(Future<T> future) {
        return () -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RestException(e);
            }
        };
    }
}
```

Adapter will also need to be added to RestAhead builder, via the `addAdapter(Object object)` method. Exceptions can be
thrown by declared adapters and can be propagated via the service (see [Call exceptions](#call-exceptions)).

### Interceptors

Interceptors can be added to the client to perform common logic before, after or around a request. Interceptor should
implement the `Interceptor` interface and be added to the client like so:

```jshelllanguage
var client = new JavaHttpClient();
    client.addInterceptor(new PreRequestInterceptor());

    var service = RestAhead.builder("https://httpbin.org/")
        .client(client)
        .converter(new JacksonConverter())
        .build(InterceptedService.class);
```

### Paths

Path parameters can also be provided through requests by using the `@Path` annotation on a parameter:

```java
interface PathExample {
    @Get("/{path1}/{path2}")
    Response performGet(@Path("path1") String path, @Path String path2); // value can be omitted in favor or parameter name
}
```

### Call exceptions

By default, no exceptions need to be declared to execute calls, but beware! An unchecked exception (RestException) will
be thrown in case there was an exception thrown during execution. You can also add `throws` declaration for either or
both exceptions that are likely to occur: `ExecutionException`, `InterruptedException`, to make sure they are handled.
If either of these is not specified in the signature, `RestException` will still be thrown, wrapping the other one, for
example:

```java
import java.util.concurrent.ExecutionException;

public interface HttpBinService {
    // Will throw a RestException if any errors occur
    @Get("/get")
    Response performGet();

    // Allows you to handle IOException, RestException wrapping InterruptedException may still occur
    @Get("/get")
    Response performGet2() throws ExecutionException;

    // Allows you to handle both exception, no RestException will be thrown
    @Get("/get")
    Response performGet3() throws ExecutionException, InterruptedException;
}
```

A failed request, with custom responses will throw RequestFailedException, that contains a code and the input stream
from the request.

### Custom client

The `RestAhead` builder declares an interface `Client` that allows you to implement custom clients. By default, if no
client is specified, Java HTTP client is used.

### Spring Boot

For compatibility with spring boot you can add the following to your pom.xml:

```xml

<dependency>
    <groupId>io.github.zskamljic</groupId>
    <artifactId>rest-ahead-spring</artifactId>
    <version>${rest.ahead.version}</version>
</dependency>
```

To enable automatic creation of Spring beans add the `@EnableRestAhead` annotation to your application class as
following:

```java

@EnableRestAhead
@SpringBootApplication
public class SpringApplicationDemo {
    public static void main(String[] args) {
        SpringApplication.run(SpringApplicationDemo.class, args);
    }
}
```

Finally, to have services available as injectable beans add the `@RestAheadService` annotation to the service:

```java

@RestAheadService(url = "https://httpbin.org", converter = JacksonConverter.class)
public interface DemoService {
    @Get("/get")
    Map<String, Object> performGet();
}
```

`DemoService` will then be injectable wherever you use it as a bean - either constructor injection or `@Autowired`
injection. URL property needs to be provided to have a baseUrl configured, converter property is optional and is
required only if the service requires one, see [response types](#response-types).

## Adding to project

Add the dependencies as following:

```xml

<dependencies>
    <!-- other dependencies -->
    <dependency>
        <groupId>io.github.zskamljic</groupId>
        <artifactId>rest-ahead-client</artifactId>
        <version>${rest.ahead.version}</version>
    </dependency>
    <dependency>
        <groupId>io.github.zskamljic</groupId>
        <artifactId>rest-ahead-processor</artifactId>
        <version>${rest.ahead.version}</version>
        <scope>provided</scope>
    </dependency>
    <!-- If you want to use the Jackson converter -->
    <dependency>
        <groupId>io.github.zskamljic</groupId>
        <artifactId>rest-ahead-jackson-converter</artifactId>
        <version>${rest.ahead.version}</version>
    </dependency>
</dependencies>
```

Also add the maven-compiler-plugin if not present:

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>${compiler.plugin.version}</version>
</plugin>
```

### Snapshots

Snapshots can be accessed by adding the snapshot repository:

```xml

<repositories>
    <repository>
        <id>oss.sonatype.org-snapshot</id>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>
```

## License

Project uses Apache 2.0 license. More info in [license file](LICENSE)