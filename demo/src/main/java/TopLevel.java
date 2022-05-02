import io.github.zskamljic.restahead.RestAhead;

public class TopLevel {
    public static void main(String[] args) {
        var service = RestAhead.builder("https://httpbin.org")
            .build(TopLevelService.class);

        service.get();
    }
}
