package com.udec.sistemaiot.dao;

import com.udec.sistemaiot.dao.api.SensorRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

public class SensorRepositoryCustomImpl implements SensorRepositoryCustom {
    
    private final MongoTemplate mongoTemplate;
    
    @Autowired
    public SensorRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Double getAvgTemp(String puerto) {
        Criteria p = Criteria.where("puerto").is(puerto).andOperator(Criteria.);
        Aggregation.match(p)
        return null;
    }

    @Override
    public Double getAvgHum(String puerto) {
        return null;
    }
}