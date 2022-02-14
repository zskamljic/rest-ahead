# Custom dialects

Defining a new dialect is possible by depending on rest-ahead-processor. Required steps are as following:

1. Create a class implementing `io.github.zskamljic.restahead.polyglot.Dialect`.
2. Create a file in `main/resources/META-INF/services` named `io.github.zskamljic.restahead.polyglot.Dialect` (note that
   this is a file name, not a Java package)
3. In created file add the line with FQCN of the class created in #1.
4. In project where you want the dialect to be used add the dependency with at least `provided` scope.

For sample implementation see Spring Dialect.