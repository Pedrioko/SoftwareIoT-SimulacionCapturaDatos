package com.udec.sistemaiot.dao.api;

import com.udec.sistemaiot.domain.query.AvgData;

import java.util.List;

public interface SensorRepositoryCustom {

    List<AvgData> avgtemp(String puerto);

    List<AvgData> avghum(String puerto);
}