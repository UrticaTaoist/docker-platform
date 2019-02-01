package cn.luutqf.docker.platform.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @Author: ZhenYang
 * @date: 2019/2/1
 * @description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyContainer {

    private String platform;

    private String uiPort;

    private String sshPort;

    private List<String> ports;

    private Map<String,Object> labels;

    private List<String> dataVolumes;

    private String volumeDriver;

    private String image;

    private String name;

    private Map<String,Object> env;

    private Integer ttl;

    private BigInteger cpuShares;

    private BigInteger memory;

    private BigInteger memoryReservation;

    private String servicePassword;
}