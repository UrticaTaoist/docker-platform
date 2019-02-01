package cn.luutqf.docker.platform.rancher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import static cn.luutqf.docker.platform.common.constant.RedisConstants.DEFAULT_CHANNEL_TOPIC;

/**
 * @Author: ZhenYang
 * @date: 2019/1/30
 * @description:
 */
@Configuration
public class PubsubConfiguration  {

    private final RedisMessageListener redisMessageListener;

    @Autowired
    public PubsubConfiguration(RedisMessageListener redisMessageListener) {
        this.redisMessageListener = redisMessageListener;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Autowired RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(DEFAULT_CHANNEL_TOPIC));
        return redisMessageListenerContainer;
    }


}
