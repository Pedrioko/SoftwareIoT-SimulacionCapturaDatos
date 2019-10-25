/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.sistemaiot.ui.domain;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author PDMS-LAPTOP
 */
public class DatasetLinear {
    private XYSeriesCollection datasetTemperatura;
    private XYSeriesCollection datasetHumedad;

    public DatasetLinear(XYSeriesCollection datasetTemperatura, XYSeriesCollection datasetHumedad) {
        this.datasetTemperatura = datasetTemperatura;
        this.datasetHumedad = datasetHumedad;
    }

    public XYSeriesCollection getDatasetTemperatura() {
        return datasetTemperatura;
    }

    public void setDatasetTemperatura(XYSeriesCollection datasetTemperatura) {
        this.datasetTemperatura = datasetTemperatura;
    }

    public XYSeriesCollection getDatasetHumedad() {
        return datasetHumedad;
    }

    public void setDatasetHumedad(XYSeriesCollection datasetHumedad) {
        this.datasetHumedad = datasetHumedad;
    }


}
