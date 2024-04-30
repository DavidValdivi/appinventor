// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2023-2024 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.style.neo;

import com.google.appinventor.client.StatusPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;

public class StatusPanelNeo extends StatusPanel {
  interface StatusPanelUiBinderneo extends UiBinder<FlowPanel, StatusPanelNeo> {}

  @UiField FlowPanel footer;
  @UiField Anchor tosLink;

  @Override
  public void bindUI() {
    StatusPanelUiBinderneo uibinder = GWT.create(StatusPanelUiBinderneo.class);
    initWidget(uibinder.createAndBindUi(this));
    super.footer = footer;
    super.tosLink = tosLink;
  }
}