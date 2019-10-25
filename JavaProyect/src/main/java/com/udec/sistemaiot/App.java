package com.udec.sistemaiot;

import com.udec.sistemaiot.dao.SensorRepository;
import com.udec.sistemaiot.domain.query.AvgData;
import com.udec.sistemaiot.ui.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.util.List;

@SpringBootApplication
public class App {


    public static ConfigurableApplicationContext Ctx;

    public static void main(String[] args) {
        Ctx = new SpringApplicationBuilder(App.class)
                .headless(false)
                .run(args);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
     //   List<AvgData> com5 = Ctx.getBean(SensorRepository.class).avghum("COM5");
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }

}