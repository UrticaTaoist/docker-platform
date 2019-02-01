package cn.luutqf.docker.platform.rancher;

import cn.luutqf.docker.platform.rancher.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: ZhenYang
 * @date: 2019/1/30
 * @description:
 */
@Component
public class RedisMessageListener implements MessageListener {

    @Resource
    private ContainerService containerService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        containerService.delete(message.toString());
    }
}
