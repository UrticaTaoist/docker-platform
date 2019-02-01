package cn.luutqf.docker.platform.rancher;

import cn.luutqf.docker.platform.common.entity.MyContainer;
import cn.luutqf.docker.platform.rancher.service.ContainerService;
import io.rancher.type.Container;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * @Author: ZhenYang
 * @date: 2019/2/1
 * @description:
 */
@SpringBootApplication
public class PlatformRancherClientBootstrap {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(PlatformRancherClientBootstrap.class, args);
        ContainerService bean = run.getBean(ContainerService.class);
        MyContainer container = new MyContainer();
        container.setImage("docker:luutqf/ubuntu-xfce-vnc");
        container.setName("test0101010101");
        container.setServicePassword("123456");
        container.setUiPort("6901");
        container.setTtl(123);
        Optional<Container> add = bean.add(container);
        System.out.println(add.isPresent());
    }
}