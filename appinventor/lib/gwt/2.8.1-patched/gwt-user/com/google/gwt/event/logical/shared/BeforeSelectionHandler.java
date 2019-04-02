/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.event.logical.shared;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for {@link BeforeSelectionEvent} events.
 * 
 * @param <T> the type about to be selected
 */
public interface BeforeSelectionHandler<T> extends EventHandler {

  /**
   * Called when {@link BeforeSelectionEvent} is fired.
   * 
   * @param event the {@link BeforeSelectionEvent} that was fired
   */
  void onBeforeSelection(BeforeSelectionEvent<T> event);
}
