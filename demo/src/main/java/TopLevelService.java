import io.github.zskamljic.restahead.annotations.verbs.Get;

public interface TopLevelService {
    @Get("/get")
    void get();
}
