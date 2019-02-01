package cn.luutqf.docker.platform.common.binder;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import static cn.luutqf.docker.platform.common.constant.BinderConstants.REDIS_BINDER;
import static cn.luutqf.docker.platform.common.constant.BinderConstants.REDIS_SET_BINDER;

/**
 * @Author: ZhenYang
 * @date: 2019/2/1
 * @description:
 */
public interface SinkSender {

    @Output(REDIS_BINDER)
    MessageChannel output();

    @Output(REDIS_SET_BINDER)
    MessageChannel redisSet();
}
