package com.tecdes.sistema_bancada.dto;

public class ClpConfigDTO {
    private String ip;
    private long delay;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
