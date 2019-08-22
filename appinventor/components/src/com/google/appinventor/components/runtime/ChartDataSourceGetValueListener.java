package com.google.appinventor.components.runtime;

/**
 * Interface for receiving real time data from a Chart Data Source.
 */
public interface ChartDataSourceGetValueListener {
  /**
   * Event called when a new real time value is sent to the observer.
   *
   * @param component  component that triggered the event
   * @param key  identifier of the value
   * @param value  value received
   */
  public void onReceiveValue(RealTimeChartDataSource component, String key, Object value);
}
