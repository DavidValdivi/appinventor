// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.google.appinventor.components.runtime.util.YailList;

@DesignerComponent(version = YaVersion.SPINNER_VERSION, 
  description = "<p>A spinner component, that when clicked on, displays a pop-up"
    + " with a list of elements. These elements can be set in"
    + "the Designer or Blocks Editor by setting the "
    + "<code>ElementsFromString</code> property to their string-separated "
    + "concatenation (for example, <em>choice 1, choice 2, choice 3</em>) or "
    + "by setting the <code>Elements</code> property to a List in the Blocks " + "editor.</p>", 
    category = ComponentCategory.USERINTERFACE, 
    nonVisible = false, 
    iconName = "images/spinner.png")
@SimpleObject
public final class Spinner extends AndroidViewComponent implements OnItemSelectedListener {

  private final android.widget.Spinner view;
  private ArrayAdapter<String> adapter;
  private YailList items;
  private String selection;
  private int selectionIndex;

  /**
   * Creates a new Spinner Component
   * 
   * @param container
   *          container, component will be placed in
   */
  public Spinner(ComponentContainer container) {
    super(container);
    view = new android.widget.Spinner(container.$context());

     // Adds the component to its designated container
    container.$add(this);

    items = new YailList();
    selection = "";
    selectionIndex = 0;

    // set regular and dropdown layouts
    adapter = new ArrayAdapter<String>(container.$context(), android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    view.setAdapter(adapter);
    view.setOnItemSelectedListener(this);
  }

  @Override
  public View getView(){
    return view;
  }

  /**
   * Selection property getter method.
   */
  @SimpleProperty(description = "The selected item.  When directly changed by the "
      + "programmer, the SelectionIndex property is also changed to the first "
      + "item in the Spinner with the given value.  If the value does not "
      + "appear, SelectionIndex will be set to 0.", category = PropertyCategory.BEHAVIOR)
  public String Selection(){
    return selection;
  }

  /**
   * Selection property setter method.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
  @SimpleProperty
  public void Selection(String value){
    selection = value;
    // Now, we need to change SelectionIndex to correspond to Selection.
    // If multiple Selections have the same SelectionIndex, use the first.
    // If none do, arbitrarily set the SelectionIndex to its default value
    // of 0.
    for (int i = 0; i < items.size(); i++) {
      // The comparison is case-sensitive to be consistent with yail-equal?.
      if (items.getString(i).equals(value)) {
        selectionIndex = i + 1;
        return;
      }
    }
    selectionIndex = 0;
  }

  /**
   * Selection index property getter method.
   */
  @SimpleProperty(description = "The index of the currently selected item, starting at "
      + "1.  If no item is selected, the value will be 0.  If an attempt is "
      + "made to set this to a number less than 1 or greater than the number "
      + "of items in the Spinner, SelectionIndex will be set to 0, and "
      + "Selection will be set to the empty text.", category = PropertyCategory.BEHAVIOR)
  public int SelectionIndex(){
    return selectionIndex;
  }

  /**
   * Selection index property setter method.
   */
  // Not a designer property, since this could lead to unpredictable
  // results if Selection is set to an incompatible value.
  @SimpleProperty
  public void SelectionIndex(int index){
    if (index <= 0 || index > items.size()) {
      selectionIndex = 0;
      selection = "";
    } else {
      selectionIndex = index;
      // YailLists are 0-based, but we want to be 1-based.
      selection = items.getString(selectionIndex - 1);
    }
  }

  /**
   * Elements property getter method
   * 
   * @return a YailList representing the list of strings to be picked from
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public YailList Elements(){
    return items;
  }

  /**
   * Elements property setter method
   * 
   * @param itemList
   *          - a YailList containing the strings to be added to the Spinner
   */
  @SimpleProperty
  public void Elements(YailList itemList){
    adapter.clear();
    Object[] objects = itemList.toStringArray();
    for (int i = 0; i < objects.length; i++) {
      if (!(objects[i] instanceof String)) {
        throw new YailRuntimeError("Items passed to ListPicker must be Strings", "Error");
      }
    }
    items = itemList;
    adapter.addAll(itemList.toStringArray());
  }

  /**
   * ElementsFromString property setter method
   * 
   * @param itemstring
   *          - a string containing a comma-separated list of the strings to be
   *          picked from
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public void ElementsFromString(String itemstring){
    adapter.clear();
    if (itemstring.length() == 0) {
      items = new YailList();
    } else {
      items = YailList.makeList((Object[]) itemstring.split(" *, *"));
      adapter.addAll(items.toStringArray());

    }
  }

  /**
   * Prompt property getter method
   * 
   * @return a String with the current prompt for the Spinner
   */
  @SimpleProperty(category = PropertyCategory.APPEARANCE)
  public String Prompt(){
    return view.getPrompt().toString();
  }

  /**
   * Prompt property setter method
   * 
   * @param str
   *          - the string to set as the spinner prompt
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
  @SimpleProperty(category = PropertyCategory.APPEARANCE)
  public void Prompt(String str){
    view.setPrompt(str);
  }

  /**
   * To display the dropdown list without the user having to click it
   */
  @SimpleFunction(description = "displays dropdown list, same action as when user clicks")
  public void DisplayDropdown(){
    view.performClick();
  }

  /**
   * Indicates a user has selected an item
   */
  @SimpleEvent
  public void DidSelectItem(){
    EventDispatcher.dispatchEvent(this, "DidSelectItem");
  }

  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
    selection = (String) parent.getItemAtPosition(pos);
    selectionIndex = pos;
    DidSelectItem();
  }

  /**
   * When a user selects nothing, not sure when this actually gets called
   */
  @SimpleEvent
  public void DidSelectNothing(){
    EventDispatcher.dispatchEvent(this, "DidSelectNothing");
  }

  public void onNothingSelected(AdapterView<?> parent){
    selection = "";
    selectionIndex = 0;
    DidSelectNothing();
  }

}
