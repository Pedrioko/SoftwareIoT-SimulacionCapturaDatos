/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.sistemaiot.ui.domain;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;

/**
 * @author PDMS-LAPTOP
 */
public class DatasetHistoric {
    private DefaultCategoryDataset datasetTemperatura;
    private DefaultCategoryDataset datasetHumedad;

    public DatasetHistoric(DefaultCategoryDataset datasetTemperatura, DefaultCategoryDataset datasetHumedad) {
        this.datasetTemperatura = datasetTemperatura;
        this.datasetHumedad = datasetHumedad;
    }

    public DefaultCategoryDataset getDatasetTemperatura() {
        return datasetTemperatura;
    }

    public void setDatasetTemperatura(DefaultCategoryDataset datasetTemperatura) {
        this.datasetTemperatura = datasetTemperatura;
    }

    public DefaultCategoryDataset getDatasetHumedad() {
        return datasetHumedad;
    }

    public void setDatasetHumedad(DefaultCategoryDataset datasetHumedad) {
        this.datasetHumedad = datasetHumedad;
    }
}
