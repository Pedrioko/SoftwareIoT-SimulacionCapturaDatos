package com.udec.sistemaiot.services;

import com.fazecast.jSerialComm.SerialPort;
import com.udec.sistemaiot.dao.SensorRepository;
import com.udec.sistemaiot.domain.Dato;
import com.udec.sistemaiot.domain.Sensor;
import com.udec.sistemaiot.events.EventAction;
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
    private Map<String, EventAction> actionMap = new HashMap<>();

    @Autowired
    public RealtimeService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @PostConstruct
    public void init() {
        List<Sensor> listall = sensorRepository.findAll();

        listall.forEach(sensor -> {
            Thread thread = threadMap.get(sensor.getPuerto());
            if (thread != null) thread.stop();
            Thread hilo = new Thread(() -> {
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
                                String[] value = br.readLine().split(":");
                                List<Dato> historial = sensorrefesh.getHistorial();
                                if (historial == null)
                                    historial = new ArrayList<>();

                                Dato dato = new Dato(Double.valueOf(value[1]), Double.valueOf(value[0]), new Date());
                                historial.add(dato);

                                JLabel jLabel = humedadLabel.get(sensorrefesh.getPuerto());
                                if (jLabel != null)
                                    jLabel.setText(new StringBuilder().append(dato.getHumedad()).append(" - ").append(historial.stream().map(Dato::getHumedad).max(Double::compareTo).get()).toString());
                                JLabel jLabel1 = tempLabel.get(sensorrefesh.getPuerto());
                                if (jLabel1 != null)
                                    jLabel1.setText(new StringBuilder().append(dato.getTemperatura()).append(" - ").append(historial.stream().map(Dato::getTemperatura).max(Double::compareTo).get()).toString());

                                sensorrefesh.setHistorial(historial);
                                sensorRepository.save(sensorrefesh);
                                if (dato.getHumedad() > sensorrefesh.getMaxValueHum())
                                    JOptionPane.showMessageDialog(null, "¡Alerta!\nHumedad se encuentra por encima de lo establecido en el sensor " + sensorrefesh.getNombre(), "Alerta", JOptionPane.WARNING_MESSAGE);
                                if (dato.getHumedad() < sensorrefesh.getMinValueHum())
                                    JOptionPane.showMessageDialog(null, "¡Alerta!\nHumedad se encuentra por debajo de lo establecido en el sensor " + sensorrefesh.getNombre(), "Alerta", JOptionPane.WARNING_MESSAGE);

                                if (dato.getTemperatura() > sensorrefesh.getMaxValueTemp())
                                    JOptionPane.showMessageDialog(null, "¡Alerta!\nTemperatura se encuentra por encima de lo establecido en el sensor " + sensorrefesh.getNombre(), "Alerta", JOptionPane.WARNING_MESSAGE);
                                if (dato.getTemperatura() < sensorrefesh.getMinValueTemp())
                                    JOptionPane.showMessageDialog(null, "¡Alerta!\nTemperatura se encuentra por debajo de lo establecido en el sensor " + sensorrefesh.getNombre(), "Alerta", JOptionPane.WARNING_MESSAGE);

                                EventAction action = actionMap.get(sensorrefesh.getPuerto());
                                if (action != null) action.doIt(sensorrefesh);
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            threadMap.put(sensor.getPuerto(), hilo);
            hilo.start();

        });
    }

    public JLabel putHumedadLabel(String key, JLabel value) {
        return humedadLabel.put(key, value);
    }

    public JLabel putTempLabel(String key, JLabel value) {
        return tempLabel.put(key, value);
    }

    public EventAction putActionMap(String key, EventAction value) {
        return actionMap.put(key, value);
    }
}
