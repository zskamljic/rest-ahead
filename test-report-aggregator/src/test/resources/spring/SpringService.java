package restahead.spring;

import io.github.zskamljic.restahead.annotations.verbs.Patch;
import io.github.zskamljic.restahead.client.responses.Response;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

interface SpringService {
    @DeleteMapping("/delete")
    Response performDelete();

    @GetMapping("/get")
    Response performGet();

    @PatchMapping("/patch")
    Response performPatch();

    @PostMapping("/post")
    Response performPost();

    @PutMapping("/put")
    Response performPut();

    @RequestMapping(method = RequestMethod.GET, value = "/get")
    Response customGet();
}