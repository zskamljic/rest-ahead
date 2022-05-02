import io.github.zskamljic.restahead.annotations.request.Query;
import io.github.zskamljic.restahead.annotations.verbs.Delete;

public interface EnumQuery {
    @Delete("/delete")
    void delete(@Query("q") Sample query);

    enum Sample {
        ONE
    }
}