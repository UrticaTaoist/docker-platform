package cn.luutqf.docker.platform.common.binder;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

import static cn.luutqf.docker.platform.common.constant.BinderConstants.REDIS_BINDER;
import static cn.luutqf.docker.platform.common.constant.BinderConstants.REDIS_SET_BINDER;

/**
 * @Author: ZhenYang
 * @date: 2019/1/27
 * @description:
 */
public interface Sink {

    @Input(REDIS_BINDER)
    SubscribableChannel input();

    @Input(REDIS_SET_BINDER)
    SubscribableChannel input2();



}
