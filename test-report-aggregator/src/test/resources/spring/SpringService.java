package restahead.spring;

import io.github.zskamljic.restahead.client.responses.Response;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

interface SpringService {
    @DeleteMapping("/delete")
    Response performDelete(
        @RequestHeader("custom") String header1,
        @RequestHeader("header2") String header2
    );

    @GetMapping("/{get}/{hello}")
    Response performGet(
        @PathVariable String get,
        @PathVariable("hello") String second
    );

    @PatchMapping("/patch")
    Response performPatch(
        @RequestBody Map<String, String> body
    );

    @PostMapping("/post")
    Response performPost(
        @RequestPart String part,
        @RequestPart MultipartFile file
    );

    @PutMapping("/put")
    Response performPut();

    @RequestMapping(method = RequestMethod.GET, value = "/get")
    Response customGet();
}