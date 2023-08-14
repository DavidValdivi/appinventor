// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2023 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.LOBFValues;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.YailDictionary;
import com.google.appinventor.components.runtime.util.YailList;
import gnu.lists.LList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A data science component to apply different regression models.
 * The component only requires a data source to apply the model on.
 *
 * The component is only responsible for the statistical calculations and
 * provides the following properties for line of best fit:
 * "slope", "Yintercept", "correlation coefficient", and "predictions"
 *
 * To draw the line of best fit use the drawing block in ChartData2D component
 */
@DesignerComponent(version = YaVersion.REGRESSION_COMPONENT_VERSION,
    description = "A component that contains regression models",
    category = ComponentCategory.DATASCIENCE,
    iconName = "images/regression.png",
    nonVisible = true)
@SimpleObject
@SuppressWarnings("checkstyle:JavadocParagraph")
public final class Regression extends DataCollection {

  /**
   * Creates a new Regression component.
   */
  public Regression(ComponentContainer container) {
    super();
  }

  /**
   * Calculates the line of best fit.
   *
   * @param xEntries - the list of x values
   * @param yEntries - the list of y values
   * @return list. 1st element of the list is the slope, 2nd element is the intercept, 3rd correlation coefficient, 4th element is the line of best fit prediction values
   */
  public static YailDictionary ComputeLineOfBestFit(final YailList xEntries, final YailList yEntries) {
    LList xValues = (LList) xEntries.getCdr();
    List<Double> x = castToDouble(xValues);

    LList yValues = (LList) yEntries.getCdr();
    List<Double> y = castToDouble(yValues);

    if (xValues.size() != yValues.size()) {
      throw new IllegalStateException("Must have equal X and Y data points");
    }
    if (xValues.size() == 0 || xValues.size() == 0) {
      throw new IllegalStateException("List must have at least one element");
    }
    int n = xValues.size();

    double sumx = 0.0, sumy = 0.0, sumXY = 0.0, squareSum_X = 0.0, squareSum_Y = 0.0;
    for (int i = 0; i < n; i++) {
      sumx += x.get(i);
      sumXY = sumXY + x.get(i) * y.get(i);
      sumy += y.get(i);
      squareSum_X = squareSum_X + x.get(i) * x.get(i);
      squareSum_Y = squareSum_Y + y.get(i) * y.get(i);
    }
    double xmean = sumx / n;
    double ymean = sumy / n;

    double xxmean = 0.0, xymean = 0.0;
    List predictions = new ArrayList();
    for (int i = 0; i < n; i++) {
      xxmean += (x.get(i) - xmean) * (x.get(i) - xmean);
      xymean += (x.get(i) - xmean) * (y.get(i) - ymean);
    }
    double slope = xymean / xxmean;
    double intercept = ymean - slope * xmean;
    // use formula for calculating correlation coefficient.
    double corr = (n * sumXY - sumx * sumy) / (Math.sqrt((n * squareSum_X - sumx * sumx) * (n * squareSum_Y - sumy * sumy)));

    for (int i = 0; i < n; i++) {
      double prediction = slope * x.get(i) + intercept;
      predictions.add(prediction);
    }

    YailDictionary resultDic = new YailDictionary();
    resultDic.put("slope",slope);
    resultDic.put("Yintercept", intercept);
    resultDic.put("correlation coefficient", corr);
    resultDic.put("predictions", predictions);

    return resultDic;
  }

  /**
   * Returns one of the Line of Best Fit values.
   * A value could be "slope", "Yintercept", "correlation coefficient", "predictions" or a dictionary with all values above if nothing specific provided.
   *
   * @param xList - the list of x values
   * @param yList - the list of y values
   * @param value - the string name of the line of best fit property
   * @return Double slope
   */
  @SimpleFunction(description = "Returns one of the Line of Best Fit values. A value could be \"slope\", \"Yintercept\", \"correlation coefficient\"or \"predictions\". " +
      "The block returns the complete dictionary with all values if no specific value string is provided")
  public Object CalculateLineOfBestFitValue(final YailList xList, final YailList yList, LOBFValues value) {
    YailDictionary result = ComputeLineOfBestFit(xList, yList);
    if (result.containsKey(value.toUnderlyingValue())){
        return result.get(value.toUnderlyingValue());
    } else {
      return result;
    }
  }

  @Override
  public HandlesEventDispatching getDispatchDelegate() {
    return container.$form();
  }

  @Override
  public void onDataChange() {

  }
}


