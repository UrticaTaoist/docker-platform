package cn.luutqf.docker.platform.task;

import cn.luutqf.docker.platform.common.binder.Sink;
import cn.luutqf.docker.platform.common.entity.MyContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

import static cn.luutqf.docker.platform.common.constant.BinderConstants.REDIS_SET_BINDER;


/**
 * @Author: ZhenYang
 * @date: 2019/1/27
 * @description:
 */
@EnableBinding(Sink.class)
public class SinkReceiver {

    private static Logger logger = LoggerFactory.getLogger(SinkReceiver.class);

    @StreamListener(REDIS_SET_BINDER)
    public void receive(Message<MyContainer> payload) {
        logger.info("Received: " + payload.getPayload());
    }

}
