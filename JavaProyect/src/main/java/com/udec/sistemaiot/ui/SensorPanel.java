/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.sistemaiot.ui;

import com.udec.sistemaiot.App;
import com.udec.sistemaiot.dao.SensorRepository;
import com.udec.sistemaiot.domain.Dato;
import com.udec.sistemaiot.domain.Sensor;
import com.udec.sistemaiot.math.Stats;
import com.udec.sistemaiot.services.RealtimeService;
import com.udec.sistemaiot.ui.domain.DatasetHistoric;
import com.udec.sistemaiot.ui.domain.DatasetLinear;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.DataUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author PDMS-LAPTOP
 */
public class SensorPanel extends javax.swing.JPanel {

    private final RealtimeService realtimeService;
    private final SensorRepository sensorRepository;
    private Sensor sensor;

    /**
     * Creates new form SensorPanel
     */
    public SensorPanel(Sensor sensor, SensorRepository sensorRepository) {
        this.sensor = sensor;
        this.sensorRepository = sensorRepository;
        initComponents();
        jlNombre.setText(this.sensor.getNombre());
        jlPuerto.setText(this.sensor.getPuerto());
        realtimeService = App.Ctx.getBean(RealtimeService.class);
        realtimeService.putHumedadLabel(this.sensor.getPuerto(), this.jlHumedad);
        realtimeService.putTempLabel(this.sensor.getPuerto(), this.jlTemp);
        DatasetHistoric dataset = createDatasetHistorical(sensor.getHistorial());

        JFreeChart charthistorictemp = createHistoric("Temperatura historica", "# Dato", "Temperatura", dataset.getDatasetHumedad());
        ChartPanel panelhistorictemp = new ChartPanel(charthistorictemp);
        panelhistorictemp.setPreferredSize(new Dimension(460, 350));
        jpHistoricoTemp.setLayout(new BorderLayout());
        jpHistoricoTemp.add(panelhistorictemp, BorderLayout.CENTER);
        jpHistoricoTemp.validate();
        JFreeChart charthistorichum = createHistoric("Humedad historica", "# Dato", "Humedad", dataset.getDatasetHumedad());
        ChartPanel panelhistorichum = new ChartPanel(charthistorichum);
        panelhistorichum.setPreferredSize(new Dimension(460, 350));
        jpHistoricoHum.setLayout(new BorderLayout());
        jpHistoricoHum.add(panelhistorichum, BorderLayout.CENTER);
        jpHistoricoHum.validate();

        DatasetLinear inputData = createDatasetLinear(this.sensor.getHistorial());
        JFreeChart chartlinear = createLinear("Humedad linear", "#Dato", "Humedad", inputData.getDatasetHumedad());

        XYPlot plot = chartlinear.getXYPlot();
        plot.getRenderer().setSeriesPaint(0, Color.blue);
        ChartPanel panelLinear = new ChartPanel(chartlinear);
        panelLinear.setPreferredSize(new Dimension(460, 350));
        jpLinealHum.setLayout(new BorderLayout());
        jpLinealHum.add(panelLinear, BorderLayout.CENTER);
        jpLinealHum.validate();

        JFreeChart chartlinearTemp = createLinear("Temperatura linear", "#Dato", "Temperatura", inputData.getDatasetTemperatura());

        XYPlot plotTemp = chartlinearTemp.getXYPlot();
        plotTemp.getRenderer().setSeriesPaint(0, Color.BLUE);
        ChartPanel panelLinearTemp = new ChartPanel(chartlinearTemp);
        panelLinearTemp.setPreferredSize(new Dimension(460, 350));
        jpLinealTemp.setLayout(new BorderLayout());
        jpLinealTemp.add(panelLinearTemp, BorderLayout.CENTER);
        jpLinealTemp.validate();

        loadMedias(this.sensor);
        realtimeService.putActionMap(this.sensor.getPuerto(), e -> {
            List<Dato> historialTotal = ((Sensor) e).getHistorial();
            List<Dato> historial = getUsableList(historialTotal);
            DatasetHistoric datasetHistorical = createDatasetHistorical(historial);
            charthistorichum.getCategoryPlot().setDataset(datasetHistorical.getDatasetHumedad());
            panelhistorichum.repaint();
            charthistorictemp.getCategoryPlot().setDataset(datasetHistorical.getDatasetTemperatura());
            panelhistorictemp.repaint();

            DatasetLinear datasetLinear = createDatasetLinear(historial);
            chartlinear.getXYPlot().setDataset(datasetLinear.getDatasetHumedad());
            panelLinear.repaint();
            chartlinearTemp.getXYPlot().setDataset(datasetLinear.getDatasetTemperatura());
            panelLinearTemp.repaint();

            loadMedias((Sensor) e);
        });

    }

    private void loadMedias(Sensor sensor) {
        List<Double> hum = sensor.getHistorial().stream().map(e -> e.getHumedad()).collect(Collectors.toList());
        double avgHum = Stats.getAvg(hum);
        List<Double> temp = sensor.getHistorial().stream().map(e -> e.getTemperatura()).collect(Collectors.toList());
        double avgTemp = Stats.getAvg(temp);
        jlMediaHum.setText( String.valueOf(Math.round(avgHum * 100) / 100D));
        jlMediaTemp.setText(String.valueOf(Math.round(avgTemp * 100) / 100D));
        jlDesviacionHum.setText(String.valueOf(Math.round(Stats.getStdDev(hum) * 100) / 100D));
        jlDesviacionTemp.setText(String.valueOf(Math.round(Stats.getStdDev(temp) * 100) / 100D));
    }

    private JFreeChart createHistoric(String title, String xaxis, String yaxis, DefaultCategoryDataset dataset) {
        return ChartFactory.createLineChart(
                title, // Chart title
                xaxis, // X-Axis Label
                yaxis, // Y-Axis Label
                dataset
        );
    }

    private JFreeChart createLinear(String title, String xaxis, String yaxis, XYSeriesCollection dataset) {
        JFreeChart scatterPlot = ChartFactory.createXYLineChart(title, xaxis, yaxis, dataset,
                PlotOrientation.VERTICAL, true, true, false);
        return scatterPlot;
    }


    private DatasetLinear createDatasetLinear(List<Dato> datoList) {
        XYSeriesCollection datasetHumedad = new XYSeriesCollection();
        XYSeriesCollection datasetTemperatura = new XYSeriesCollection();
        XYSeries seriesHumedad = new XYSeries("Humedad");
        XYSeries seriesTemperatura = new XYSeries("Temperatura");
        List<Dato> historial = getUsableList(datoList);
        historial.forEach(e -> {
            seriesTemperatura.add(datoList.indexOf(e) + 1, e.getTemperatura());
            seriesHumedad.add(datoList.indexOf(e) + 1, e.getHumedad());
        });
        addLine(datasetHumedad, seriesHumedad);
        addLine(datasetTemperatura, seriesTemperatura);

        datasetHumedad.addSeries(seriesHumedad);
        datasetTemperatura.addSeries(seriesTemperatura);
        return new DatasetLinear(datasetTemperatura, datasetHumedad);
    }

    private void addLine(XYSeriesCollection datasetHumedad, XYSeries seriesHumedad) {
        XYSeriesCollection xyData = new XYSeriesCollection(seriesHumedad);
        double[] coefficients = Regression.getOLSRegression(xyData, 0);
        double b = coefficients[0]; // intercept
        double m = coefficients[1]; // slope
        XYSeries trend = new XYSeries("Linear");
        double x = seriesHumedad.getDataItem(0).getXValue();
        trend.add(x, m * x + b);
        x = seriesHumedad.getDataItem(seriesHumedad.getItemCount() - 1).getXValue();
        trend.add(x, m * x + b);
        datasetHumedad.addSeries(trend);
    }

    private List<Dato> getUsableList(List<Dato> datoList) {
        List<Dato> historial = datoList;
        historial.sort((x, y) -> y.getFecha_creacion().compareTo(x.getFecha_creacion()));
        if (historial != null) {
            if (historial.size() > 40)
                historial = historial.subList(0, 40);
            return historial;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private DatasetHistoric createDatasetHistorical(List<Dato> datoList) {

        String series1 = "Humedad";
        String series2 = "Temperatura";

        DefaultCategoryDataset datasetHum = new DefaultCategoryDataset();
        DefaultCategoryDataset datasetTemp = new DefaultCategoryDataset();
        List<Dato> historial = getUsableList(datoList);

        for (Dato e : historial) {
            datasetHum.setValue(e.getHumedad(), series1, String.valueOf(datoList.indexOf(e) + 1));
            datasetTemp.setValue(e.getTemperatura(), series2, String.valueOf(datoList.indexOf(e) + 1));
        }
        return new DatasetHistoric(datasetTemp, datasetHum);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jlNombre = new javax.swing.JLabel();
        jlPuerto = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jlTemp = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jlHumedad = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jlMediaHum = new javax.swing.JLabel();
        jlMediaTemp = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jlDesviacionHum = new javax.swing.JLabel();
        jlDesviacionTemp = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jpLinealTemp = new javax.swing.JPanel();
        jpLinealHum = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jpHistoricoTemp = new javax.swing.JPanel();
        jpHistoricoHum = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtDatos = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(1000, 900));
        setRequestFocusEnabled(false);

        jScrollPane2.setToolTipText("");
        jScrollPane2.setPreferredSize(new java.awt.Dimension(30000, 30000));

        jPanel7.setPreferredSize(new java.awt.Dimension(800, 914));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Sensor");

        jLabel2.setText("Nombre");

        jLabel3.setText("Puerto");

        jlNombre.setText("     ");

        jlPuerto.setText("   ");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Tº");

        jlTemp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlTemp.setText(" ");
        jlTemp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("H%");

        jlHumedad.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlHumedad.setText(" ");
        jlHumedad.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlHumedad, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jlHumedad, jlTemp});

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlHumedad, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jlHumedad, jlTemp});

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Medias");

        jlMediaHum.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlMediaHum.setText("  ");
        jlMediaHum.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jlMediaHum.setMaximumSize(new java.awt.Dimension(20, 18));
        jlMediaHum.setMinimumSize(new java.awt.Dimension(20, 18));
        jlMediaHum.setPreferredSize(new java.awt.Dimension(20, 18));

        jlMediaTemp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlMediaTemp.setText("  ");
        jlMediaTemp.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jlMediaTemp.setMaximumSize(new java.awt.Dimension(20, 18));
        jlMediaTemp.setMinimumSize(new java.awt.Dimension(20, 18));
        jlMediaTemp.setPreferredSize(new java.awt.Dimension(20, 18));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setText("Deviación");

        jlDesviacionHum.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlDesviacionHum.setText("  ");
        jlDesviacionHum.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jlDesviacionHum.setMaximumSize(new java.awt.Dimension(20, 18));
        jlDesviacionHum.setMinimumSize(new java.awt.Dimension(20, 18));
        jlDesviacionHum.setPreferredSize(new java.awt.Dimension(20, 18));

        jlDesviacionTemp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlDesviacionTemp.setText("  ");
        jlDesviacionTemp.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jlDesviacionTemp.setMaximumSize(new java.awt.Dimension(20, 18));
        jlDesviacionTemp.setMinimumSize(new java.awt.Dimension(20, 18));
        jlDesviacionTemp.setPreferredSize(new java.awt.Dimension(20, 18));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Tº");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("H%");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(37, 37, 37)
                                .addComponent(jlPuerto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(32, 32, 32)
                                .addComponent(jlNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jlMediaHum, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jlDesviacionHum, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jlMediaTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jlDesviacionTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jlDesviacionHum, jlDesviacionTemp, jlMediaHum, jlMediaTemp});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel12});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlMediaTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jlNombre))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jlPuerto))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel10))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlDesviacionHum, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlMediaHum, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addGap(18, 18, 18)
                        .addComponent(jlDesviacionTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jlDesviacionHum, jlDesviacionTemp, jlMediaHum, jlMediaTemp});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jLabel12});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setMaximumSize(new java.awt.Dimension(500, 5000));

        jLabel7.setText("Regresion Lineal");

        javax.swing.GroupLayout jpLinealTempLayout = new javax.swing.GroupLayout(jpLinealTemp);
        jpLinealTemp.setLayout(jpLinealTempLayout);
        jpLinealTempLayout.setHorizontalGroup(
            jpLinealTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );
        jpLinealTempLayout.setVerticalGroup(
            jpLinealTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 159, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jpLinealHumLayout = new javax.swing.GroupLayout(jpLinealHum);
        jpLinealHum.setLayout(jpLinealHumLayout);
        jpLinealHumLayout.setHorizontalGroup(
            jpLinealHumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );
        jpLinealHumLayout.setVerticalGroup(
            jpLinealHumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 159, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jpLinealTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jpLinealHum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jpLinealHum, jpLinealTemp});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpLinealHum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpLinealTemp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jpLinealHum, jpLinealTemp});

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setText("Historico");

        javax.swing.GroupLayout jpHistoricoTempLayout = new javax.swing.GroupLayout(jpHistoricoTemp);
        jpHistoricoTemp.setLayout(jpHistoricoTempLayout);
        jpHistoricoTempLayout.setHorizontalGroup(
            jpHistoricoTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpHistoricoTempLayout.setVerticalGroup(
            jpHistoricoTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jpHistoricoHumLayout = new javax.swing.GroupLayout(jpHistoricoHum);
        jpHistoricoHum.setLayout(jpHistoricoHumLayout);
        jpHistoricoHumLayout.setHorizontalGroup(
            jpHistoricoHumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpHistoricoHumLayout.setVerticalGroup(
            jpHistoricoHumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 197, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jpHistoricoTemp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jpHistoricoHum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpHistoricoHum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpHistoricoTemp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setText("Datos");

        jtDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "# Dato", "Humedad", "Temperatura", "Fecha"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jtDatos);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(39, 39, 39))
        );

        jScrollPane2.setViewportView(jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jlDesviacionHum;
    private javax.swing.JLabel jlDesviacionTemp;
    private javax.swing.JLabel jlHumedad;
    private javax.swing.JLabel jlMediaHum;
    private javax.swing.JLabel jlMediaTemp;
    private javax.swing.JLabel jlNombre;
    private javax.swing.JLabel jlPuerto;
    private javax.swing.JLabel jlTemp;
    private javax.swing.JPanel jpHistoricoHum;
    private javax.swing.JPanel jpHistoricoTemp;
    private javax.swing.JPanel jpLinealHum;
    private javax.swing.JPanel jpLinealTemp;
    private javax.swing.JTable jtDatos;
    // End of variables declaration//GEN-END:variables
}
