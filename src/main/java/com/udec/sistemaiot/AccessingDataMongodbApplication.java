package com.udec.sistemaiot;

import com.udec.sistemaiot.dao.SensorRepository;
import com.udec.sistemaiot.domain.Dato;
import com.udec.sistemaiot.domain.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Date;

@SpringBootApplication
public class AccessingDataMongodbApplication implements CommandLineRunner {


    @Autowired
    private SensorRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(AccessingDataMongodbApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();

        // save a couple of customers
        Sensor s1 = new Sensor("11", "Alice", "Smith");
        ArrayList<Dato> historial = new ArrayList<>();
		historial.add(new Dato(20, 12, new Date()));
		historial.add(new Dato(20, 12, new Date()));
		historial.add(new Dato(20, 12, new Date()));
		historial.add(new Dato(20, 12, new Date()));
		historial.add(new Dato(20, 12, new Date()));
        s1.setHistorial(historial);
        repository.save(s1);
        s1 = new Sensor("11", "Alice", "Ssmith");
        repository.save(s1);
        s1 = new Sensor("11", "Alice", "Sm222ith");
        repository.save(s1);
        s1 = new Sensor("11", "Alice", "Smitqwqwh");
        repository.save(s1);

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (Sensor s : repository.findAll()) {
            System.out.println(s);
            System.out.println(s.getHistorial());
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("Customer found with findByFirstName('Alice'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByPuerto("Smith"));

        System.out.println("Customers found with findByLastName('Smith'):");
        System.out.println("--------------------------------");
        for (Sensor customer : repository.findByNombre("11")) {
            System.out.println(customer);
        }

    }
}