package cn.luutqf.docker.platform.dockerClient.web;

import cn.luutqf.docker.platform.common.entity.MyContainer;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: ZhenYang
 * @date: 2019/2/1
 * @description:
 */
@RestController
@RequestMapping("container")
public class ContainerController {

    @PostMapping
    public Object create(@RequestBody MyContainer myContainer) {

        return null;
    }

    @DeleteMapping("{name}")
    Boolean delete(@PathVariable("name") String name) {
        System.out.println(name);
        return null;
    }

    @GetMapping("start/{name}")
    Boolean start(@PathVariable("name") String name) {

        return null;
    }

    @GetMapping("stop/{name}")
    Boolean stop(String name) {

        return null;
    }

    @GetMapping("{name}")
    MyContainer get(@PathVariable("name") String name) {

        return null;
    }

    @GetMapping("{name}/{time}")
    Boolean delay(@PathVariable("name") String name, @PathVariable(value = "time") Integer time) {

        return null;
    }

    @GetMapping("logs/{name}/{raw}")
    String logs(String name, Integer raw) {

        return null;
    }

    @GetMapping("logs/token/{prefix}")
    String tokenByLog(@PathVariable("prefix")String prefix){

        return null;
    }

    @GetMapping("url/{name}")
    String getUrl(@PathVariable("name") String name) {

        return null;
    }
}