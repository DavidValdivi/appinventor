package com.google.appinventor.client.editor.simple.components;

import org.pepstock.charba.client.colors.ColorBuilder;
import org.pepstock.charba.client.colors.IsColor;
import org.pepstock.charba.client.data.Data;

/**
 * Chart Data Model for the Mock Area Chart view.
 *
 * Responsible for handling data operations on the Data
 * of the Area Chart.
 */
public class MockAreaChartDataModel extends MockLineChartBaseDataModel {
    /**
     * Creates a new MockAreaChartDataModel instance.
     *
     * @param chartData  Data object of the Chart view.
     */
    public MockAreaChartDataModel(Data chartData) {
        super(chartData);
    }

    @Override
    protected void setDefaultStylingProperties() {
        super.setDefaultStylingProperties();
        dataSeries.setFill(true); // To produce the Area Chart visual, fill has to be enabled
    }

    @Override
    public void changeColor(String color) {
        color = getHexColor(color);

        // Construct an IsColor object from the hex color value,
        // and set an alpha value of 60% (255*0.6) for consistency
        // with the Android implementation.
        // The solution is Microsoft Edge compatible.
        IsColor colorObject = ColorBuilder.parse(color);
        colorObject = colorObject.alpha(0.6);

        dataSeries.setBackgroundColor(colorObject);
        dataSeries.setPointBackgroundColor(color);
        dataSeries.setBorderColor(color);
    }

    @Override
    protected void addDataSeriesToChart() {
        if (chartData.getDatasets().size() == 0) {
            chartData.setDatasets(dataSeries);
        } else {
            // Area Chart draw order is reversed in MPAndroidChart,
            // so we have to reverse the order in which we add
            // Data Series in the MockAreaChart component to
            // preserve the drawing order.
            chartData.getDatasets().add(0, dataSeries);
        }
    }
}
