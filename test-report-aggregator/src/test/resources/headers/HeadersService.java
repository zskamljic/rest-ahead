import io.github.zskamljic.restahead.annotations.request.Headers;
import io.github.zskamljic.restahead.annotations.verbs.Get;

interface HeadersService {
    @Get
    @Headers("Authorization: none")
    void performGet();

    @Get
    @Headers({"Authorization: none", "Authorization: some", "Test: value"})
    void performGetMultipleValues();
}