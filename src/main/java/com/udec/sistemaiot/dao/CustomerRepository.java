package com.udec.sistemaiot.dao;

import java.util.List;
import java.util.Optional;

import com.udec.sistemaiot.domain.Sensor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Sensor, String> {
    @Override
    <S extends Sensor> List<S> saveAll(Iterable<S> iterable);

    @Override
    List<Sensor> findAll();

    @Override
    List<Sensor> findAll(Sort sort);

    @Override
    <S extends Sensor> S insert(S s);

    @Override
    <S extends Sensor> List<S> insert(Iterable<S> iterable);

    @Override
    <S extends Sensor> List<S> findAll(Example<S> example);

    @Override
    <S extends Sensor> List<S> findAll(Example<S> example, Sort sort);

    @Override
    Page<Sensor> findAll(Pageable pageable);

    @Override
    <S extends Sensor> S save(S s);

    @Override
    Optional<Sensor> findById(String s);

    @Override
    boolean existsById(String s);

    @Override
    Iterable<Sensor> findAllById(Iterable<String> iterable);

    @Override
    long count();

    @Override
    void deleteById(String s);

    @Override
    void delete(Sensor sensor);

    @Override
    void deleteAll(Iterable<? extends Sensor> iterable);

    @Override
    void deleteAll();

    @Override
    <S extends Sensor> Optional<S> findOne(Example<S> example);

    @Override
    <S extends Sensor> Page<S> findAll(Example<S> example, Pageable pageable);

    @Override
    <S extends Sensor> long count(Example<S> example);

    @Override
    <S extends Sensor> boolean exists(Example<S> example);
}