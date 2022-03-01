import io.github.zskamljic.restahead.annotations.request.Headers;
import io.github.zskamljic.restahead.annotations.verbs.Get;

interface HeadersService {
    @Get
    @Headers("Invalid value")
    void performGetMultipleValues();
}