package com.udec.sistemaiot.listener;


import com.udec.sistemaiot.domain.Sensor;
import com.udec.sistemaiot.services.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;


@Component
public class SensorModelListener extends AbstractMongoEventListener<Sensor> {

    private SequenceGeneratorService sequenceGenerator;

    @Autowired
    public SensorModelListener(SequenceGeneratorService sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Sensor> event) {
        if (event.getSource().getId() < 1) {
            event.getSource().setId(sequenceGenerator.generateSequence(Sensor.SEQUENCE_NAME));
        }
    }


}