package cn.luutqf.docker.platform.rancher.service.impl;


import cn.luutqf.docker.platform.common.binder.SinkSender;
import cn.luutqf.docker.platform.common.entity.MyContainer;
import cn.luutqf.docker.platform.common.exception.RancherException;
import cn.luutqf.docker.platform.common.utils.LogsUtil;
import cn.luutqf.docker.platform.common.utils.WebSocketClientUtil;
import cn.luutqf.docker.platform.rancher.service.ContainerService;
import io.rancher.Rancher;
import io.rancher.base.TypeCollection;
import io.rancher.service.ProjectApi;
import io.rancher.type.*;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.luutqf.docker.platform.common.constant.RancherConstants.*;

/**
 * @Author: ZhenYang
 * @date: 2019/1/25
 * @description:
 */
@Service("ContainerServiceImpl")
@Slf4j
@EnableBinding(value = SinkSender.class)
public class ContainerServiceImpl implements ContainerService {

    @Autowired
    private SinkSender sinkSender;

    @Value("${rancher.projectId}")
    private String project;

    private final ProjectApi projectApi;

    private final StringRedisTemplate stringRedisTemplate;


    @Autowired
    public ContainerServiceImpl(Rancher rancher, StringRedisTemplate stringRedisTemplate) {
        projectApi = rancher.type(ProjectApi.class);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Optional<Container> add(MyContainer myContainer) {
        Container container = new Container();
        Map<String,Object> labels = new HashMap<>();
        container.setName(myContainer.getName());
        container.setImageUuid(myContainer.getImage());
        container.setVolumeDriver(myContainer.getVolumeDriver());
        List<String> ports = new ArrayList<>();
        if(Optional.ofNullable(myContainer.getUiPort()).isPresent()){
            ports.add(myContainer.getUiPort());
            labels.put("ui",myContainer.getUiPort());
        }
        if(Optional.ofNullable(myContainer.getSshPort()).isPresent()){
            ports.add(myContainer.getSshPort());
            labels.put("ssh",myContainer.getSshPort());
        }
        if(Optional.ofNullable(myContainer.getPorts()).isPresent()){
            labels.put("otherPort",myContainer.getPorts());
            ports.addAll(myContainer.getPorts());
        }
        container.setPorts(ports);
        List<String> dataVolumes = myContainer.getDataVolumes();
        List<String> dataVolumes2 = Arrays.asList(LOCAL_TIME, CPU_INFO, DISK_STATS, MEM_INFO, STAT, SWAPS, UP_TIME);
        if (Optional.ofNullable(dataVolumes).isPresent()) {
            dataVolumes2.addAll(dataVolumes);
            labels.put("hasVolumes",true);
        }
        if (Optional.ofNullable(myContainer.getCpuShares()).isPresent()) {
            container.setCpuShares(myContainer.getCpuShares());
        } else {
            container.setCpuShares(CPU_SHARES);
        }
        if (Optional.ofNullable(myContainer.getMemoryReservation()).isPresent()) {
            container.setMemoryReservation(myContainer.getMemoryReservation());
        } else {
            container.setMemoryReservation(MEMORY);
        }
        if (Optional.ofNullable(myContainer.getMemory()).isPresent()) {
            container.setMemory(myContainer.getMemory());
        } else {
            container.setMemory(MEMORY);
        }
        container.setDataVolumes(dataVolumes2);
        Map<String, Object> environment = new HashMap<String, Object>() {{
            put("LANGUAGE", LANGUAGE);
            put("LANG", LANG);
            labels.put("LANG",LANG);
        }};
        if (Optional.ofNullable(myContainer.getEnv()).isPresent()) {
            environment.putAll(myContainer.getEnv());
        }
        container.setEnvironment(environment);
        if(Optional.ofNullable(myContainer.getLabels()).isPresent()){
            labels.putAll(myContainer.getLabels());
        }
        container.setLabels(labels);
        try {
            Container body = projectApi.createContainer(project, container).execute().body();
            if (!Optional.ofNullable(body).isPresent()) {
                log.info("can not create container");
                Optional<Container> byName = findByName(myContainer.getName());
                if (Optional.ofNullable(byName).isPresent()) {
                    log.info("found a created container");
                    return byName;
                }
            } else {
                log.info("container created");
                //todo prefix
                stringRedisTemplate.opsForValue().set(body.getId(), myContainer.toString(), myContainer.getTtl(), TimeUnit.SECONDS);
                sinkSender.output().send(MessageBuilder.withPayload("produce a message ：s当联考卷速度ace.com").build());
                return Optional.of(body);
            }
        } catch (IOException | NullPointerException e) {
            //todo field
            delete(container.getId());
            throw new RancherException(512);
        }
        return Optional.empty();
    }

    @Override
    public Object delete(String id) {

        Container body;
        try {
            body = projectApi.deleteContainer(project, id).execute().body();
        } catch (IOException e) {
            //todo field
            throw new RancherException(id, 512);
        }
        if (Optional.ofNullable(body).isPresent()) {
            log.info("deleted：{}", id);
            return body;
        }
        return Optional.empty();
    }

    @Override
    public Object start(String id) {
        try {
            return projectApi.startContainer(project, id).execute().body();
        } catch (IOException e) {
            throw new RancherException(e.getMessage(), 512);
        }
    }

    @Override
    public Object startByName(String name) {
        Optional<Container> byName = findByName(name);
        if (byName.isPresent()) {
            start(byName.get().getId());
            return byName.get();
        }
        return Optional.empty();
    }

    @Override
    public Object stopByName(String name) {
        Optional<Container> byName = findByName(name);
        if (byName.isPresent()) {
            stop(byName.get().getId());
            return byName.get();
        }
        return Optional.empty();
    }

    @Override
    public Object stop(String id) {
        try {
            return projectApi.stopContainer(project, id, new InstanceStop(false, new BigInteger("0"))).execute().isSuccessful();
        } catch (IOException e) {
            throw new RancherException(e.getMessage(), 512);
        }
    }

    @Override
    public String logs(String id) {
        Optional<Container> byId = findById(id);
        if (byId.isPresent()) {
            Container container = byId.get();
            ContainerLogs logs = new ContainerLogs();
            HostAccess body;
            try {
                body = projectApi.logsContainer(project, id, logs).execute().body();
            } catch (IOException e) {
                throw new RancherException(e.getMessage(), 512);
            }
            LogsUtil.logMap2.put(container.getId(), new StringBuffer());
            if (Optional.ofNullable(body).isPresent()) {
                Optional<WebSocketClient> webSocketClient = WebSocketClientUtil.getWebSocketClient(body.getUrl() + "?token=" + body.getToken(), container.getId());
                if (webSocketClient.isPresent()) {
                    while (!LogsUtil.logMap2.containsKey(container.getId())) {
                        try {
                            Thread.sleep(MinRequestTime);
                        } catch (InterruptedException e) {
                            throw new RancherException(e.getMessage(), 512);
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (LogsUtil.logMap2.containsKey(container.getId())) {
                return LogsUtil.logMap2.get(container.getId()).toString();
            }
        }
        return "";
    }

    @Override
    public Optional<Container> findById(String id) {
        try {
            return Optional.ofNullable(projectApi.findContainerById(project, id).execute().body());
        } catch (IOException e) {
            throw new RancherException(512);
        }
    }

    @Override
    public Optional<Container> findByName(String name) {
        TypeCollection<Container> body;
        try {
            body = projectApi.listContainers(project).execute().body();
        } catch (IOException e) {
            throw new RancherException(e.getMessage(), 512);
        }
        if (!Optional.ofNullable(body).isPresent()) {
            return Optional.empty();
        }
        for (Container c : body.getData()) {
            if (Optional.ofNullable(c.getName()).isPresent()) {
                if (c.getName().equals(name)) {
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }
}
