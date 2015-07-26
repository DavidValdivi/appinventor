// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2011-2015 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.ErrorMessages;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * A component for a touch sensor of an FTC robot.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
@DesignerComponent(version = YaVersion.FTC_TOUCH_SENSOR_COMPONENT_VERSION,
    description = "A component for a touch sensor of an FTC robot.",
    category = ComponentCategory.FIRSTTECHCHALLENGE,
    nonVisible = true,
    iconName = "images/ftc.png")
@SimpleObject
@UsesLibraries(libraries = "FtcRobotCore.jar")
public final class FtcTouchSensor extends FtcHardwareDevice {

  private volatile TouchSensor touchSensor;

  /**
   * Creates a new FtcTouchSensor component.
   */
  public FtcTouchSensor(ComponentContainer container) {
    super(container.$form());
  }

  // Properties

  /**
   * Value property getter.
   */
  @SimpleProperty(description = "Represents how much force is applied to the touch sensor; " +
      "for some touch sensors this value will only ever be 0 or 1.",
      category = PropertyCategory.BEHAVIOR)
  public double Value() {
    if (touchSensor != null) {
      try {
        return touchSensor.getValue();
      } catch (Throwable e) {
        e.printStackTrace();
        form.dispatchErrorOccurredEvent(this, "Value",
            ErrorMessages.ERROR_FTC_UNEXPECTED_ERROR, e.toString());
      }
    }
    return 0;
  }

  /**
   * IsPressed property getter.
   */
  @SimpleProperty(description = "Return true if the touch sensor is being pressed.",
      category = PropertyCategory.BEHAVIOR)
  public boolean IsPressed() {
    if (touchSensor != null) {
      try {
        return touchSensor.isPressed();
      } catch (Throwable e) {
        e.printStackTrace();
        form.dispatchErrorOccurredEvent(this, "IsPressed",
            ErrorMessages.ERROR_FTC_UNEXPECTED_ERROR, e.toString());
      }
    }
    return false;
  }

  // FtcRobotController.HardwareDevice implementation

  @Override
  public void initHardwareDevice(HardwareMap hardwareMap) {
    if (hardwareMap != null) {
      touchSensor = hardwareMap.touchSensor.get(getDeviceName());
      if (touchSensor == null) {
        deviceNotFound("TouchSensor", hardwareMap.touchSensor);
      }
    }
  }

  @Override
  public void clearHardwareDevice() {
    touchSensor = null;
  }
}
