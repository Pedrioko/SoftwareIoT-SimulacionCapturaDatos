package com.udec.sistemaiot.dao;

import com.udec.sistemaiot.domain.Dato;
import com.udec.sistemaiot.domain.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DatoRepository extends MongoRepository<Dato, String> {

}