package com.udec.sistemaiot.services;

import com.fazecast.jSerialComm.SerialPort;
import com.udec.sistemaiot.dao.SensorRepository;
import com.udec.sistemaiot.domain.Dato;
import com.udec.sistemaiot.domain.Sensor;
import com.udec.sistemaiot.ui.SensorForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RealtimeService {
    private SensorRepository sensorRepository;

    private Map<String, JLabel> humedadLabel = new HashMap<>();
    private Map<String, JLabel> tempLabel = new HashMap<>();
    private Map<String, Thread> threadMap = new HashMap<>();

    @Autowired
    public RealtimeService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @PostConstruct
    public void init() {
        List<Sensor> listall = sensorRepository.findAll();

        listall.forEach(sensor -> {
            Thread thread = threadMap.get(sensor.getPuerto());
            if (thread == null) {
                Thread hilo = new Thread() {

                    @Override
                    public synchronized void start() {
                        try {
                            SerialPort sp = SerialPort.getCommPort(sensor.getPuerto().trim());
                            sp.setComPortParameters(9600, 8, 1, 0);
                            sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
                            if (sp.openPort()) {
                                while (true) {
                                    Thread.currentThread().sleep(2000);
                                    if (sp.bytesAvailable() > 0) {
                                        Sensor sensorrefesh = sensorRepository.findByPuerto(sensor.getPuerto()).get(0);
                                        BufferedReader br = new BufferedReader(new InputStreamReader(sp.getInputStream()));
                                        String value = br.readLine();
                                        System.out.println("" + value);
                                        List<Dato> historial = sensorrefesh.getHistorial();
                                        if (historial == null)
                                            historial = new ArrayList<>();

                                        historial.add(new Dato(Double.valueOf(value), Double.valueOf(value), new Date()));
                                        sensorrefesh.setHistorial(historial);
                                        sensorRepository.save(sensorrefesh);
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };
                threadMap.put(sensor.getPuerto(), hilo);
                hilo.start();
            }
        });
    }

    public JLabel putHumedadLabel(String key, JLabel value) {
        return humedadLabel.put(key, value);
    }

    public JLabel putTempLabel(String key, JLabel value) {
        return tempLabel.put(key, value);
    }
}
