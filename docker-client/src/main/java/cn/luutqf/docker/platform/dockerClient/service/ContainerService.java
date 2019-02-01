package cn.luutqf.docker.platform.dockerClient.service;

import cn.luutqf.docker.platform.common.entity.MyContainer;

/**
 * @Author: ZhenYang
 * @date: 2019/2/1
 * @description:
 */
public interface ContainerService {

    Boolean create(MyContainer myContainer);

    Boolean delete(String name);

    Boolean start(String name);

    Boolean stop(String name);

    MyContainer get(String name);

    Boolean delay(String name,Integer time);

    String logs(String name,Integer raw);

    String getUrl(String name);

    String tokenByLog(String prefix);
}