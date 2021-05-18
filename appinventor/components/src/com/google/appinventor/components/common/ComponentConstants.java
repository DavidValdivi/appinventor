// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2021 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.common;

/**
 * Contains constants related to the persistent format of Simple components.
 *
 */
public class ComponentConstants {
  private ComponentConstants() {
    // nothing
  }

  /**
   * App constants
   */
  public static final int APP_INVENTOR_MIN_SDK = 7;

  /**
   * Layout constants.
   */
  public static final int LAYOUT_ORIENTATION_HORIZONTAL = 0;
  public static final int LAYOUT_ORIENTATION_VERTICAL = 1;

  public static final int DEFAULT_ROW_COLUMN = -1;

  public static final int EMPTY_HV_ARRANGEMENT_WIDTH = 100;
  public static final int EMPTY_HV_ARRANGEMENT_HEIGHT = 100;

  public static final int EMPTY_A_ARRANGEMENT_WIDTH = 100;
  public static final int EMPTY_A_ARRANGEMENT_HEIGHT = 100;

  /**
   * Default value for the top and left coordinates of view components.
   */
  public static final int DEFAULT_X_Y = Integer.MIN_VALUE;

  /**
   * Canvas component.
   */
  public static final int CANVAS_PREFERRED_WIDTH = 32;
  public static final int CANVAS_PREFERRED_HEIGHT = 48;

  /**
   * VideoPlayer component.
   * This is the size generated by iMovie for small videos
   */
  public static final int VIDEOPLAYER_PREFERRED_WIDTH = 176;
  public static final int VIDEOPLAYER_PREFERRED_HEIGHT = 144;

  /**
   * TextBox, PasswordTextBox, and EmailPicker components.
   */
  public static final int TEXTBOX_PREFERRED_WIDTH = 160;

   /**
   * HorizontalArrangement, VerticalArrangement, and Screen
   */
  public static final int GRAVITY_LEFT = 1;
  public static final int GRAVITY_RIGHT = 2;
  public static final int GRAVITY_CENTER_HORIZONTAL = 3;

  public static final int GRAVITY_TOP = 1;
  public static final int GRAVITY_CENTER_VERTICAL = 2;
  public static final int GRAVITY_BOTTOM = 3;

  public static final int HORIZONTAL_ALIGNMENT_DEFAULT = GRAVITY_LEFT;
  public static final int VERTICAL_ALIGNMENT_DEFAULT = GRAVITY_TOP;

  public static final int TEXT_RECEIVING_OFF = 1;
  public static final int TEXT_RECEIVING_FOREGROUND = 2;
  public static final int TEXT_RECEIVING_ALWAYS = 3;

  /**
   * ListView component.
   */
  public static final int LISTVIEW_PREFERRED_WIDTH = 315;
  public static final int LISTVIEW_PREFERRED_HEIGHT = 40;
  public static final int LISTVIEW_FILTER_PREFERRED_HEIGHT = 30;

  /**
   * Scrollable Arrangements
   */

  public static boolean SCROLLABLE_ARRANGEMENT = true;
  public static boolean NONSCROLLABLE_ARRANGEMENT = false;

  /**
   * Themeing
   */
  public static final String DEFAULT_THEME = "Classic";
  public static final String DEFAULT_PRIMARY_COLOR = "&HFF3F51B5";
  public static final String DEFAULT_PRIMARY_DARK_COLOR = "&HFF303F9F";
  public static final String DEFAULT_ACCENT_COLOR = "&HFFFF4081";

  /**
   * Map Components
   */
  public static final int MAP_PREFERRED_WIDTH = 176;
  public static final int MAP_PREFERRED_HEIGHT = 144;
  public static final int MARKER_PREFERRED_WIDTH = 30;
  public static final int MARKER_PREFERRED_HEIGHT = 50;
  public static final int CIRCLE_PREFERRED_RADIUS = 30;
  public static final int LINESTRING_PREFERRED_LENGTH = 60;
  public static final int POLYGON_PREFERRED_WIDTH = 50;
  public static final int POLYGON_PREFERRED_HEIGHT = 30;
  public static final int RECTANGLE_PREFERRED_WIDTH = 50;
  public static final int RECTANGLE_PREFERRED_HEIGHT = 30;

}

