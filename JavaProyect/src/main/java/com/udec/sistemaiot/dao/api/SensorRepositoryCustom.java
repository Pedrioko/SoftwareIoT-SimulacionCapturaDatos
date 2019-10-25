package com.udec.sistemaiot.dao.api;

public interface SensorRepositoryCustom {

    Double getAvgTemp(String puerto);

    Double getAvgHum(String puerto);
}