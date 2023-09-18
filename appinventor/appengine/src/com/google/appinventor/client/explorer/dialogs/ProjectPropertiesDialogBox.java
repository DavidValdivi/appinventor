package com.google.appinventor.client.explorer.dialogs;

import com.google.appinventor.client.editor.simple.components.MockForm;
import com.google.appinventor.client.editor.youngandroid.YaProjectEditor;
import com.google.appinventor.client.Ode;
import com.google.appinventor.client.widgets.properties.EditableProperties;
import com.google.appinventor.client.widgets.properties.EditableProperty;
import com.google.appinventor.client.widgets.properties.PropertyEditor;
import com.google.appinventor.client.wizards.Dialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent; 
import com.google.gwt.event.dom.client.ChangeHandler; 
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A dialog for updating project properties that can be open from any screen
 */
public class ProjectPropertiesDialogBox { 

    private static ProjectPropertiesDialogBoxUiBinder uiBinder =
        GWT.create(ProjectPropertiesDialogBoxUiBinder.class);

    interface ProjectPropertiesDialogBoxUiBinder extends UiBinder<Widget, ProjectPropertiesDialogBox> {
    }

    @UiField 
    Dialog projectProperties;

    @UiField
    ListBox projectPropertyCategoryTitlePanel;

    @UiField
    DeckPanel propertiesDeckPanel;

    @UiField
    Button closeDialogBox;

    /**
     * List Of project properties category, which will be used to group properties in the dialog 
     * properties category are : General, Theming, Publishing
     */
    private static final ArrayList<String> projectPropertyCategoryTitle = new ArrayList<String>() {{
        add(Ode.MESSAGES.projectPropertyGeneralCategoryTitle());
        add(Ode.MESSAGES.projectPropertyThemingCategoryTitle());
        add(Ode.MESSAGES.projectPropertyPublishingCategoryTitle());
    }};

    /**
     * Maps the project property category to List of EditableProperty which
     * belongs to that particular project property category
     */
    private HashMap<String, ArrayList<EditableProperty>> categoryToProperties = new HashMap<>();

    /* Object for stroing reference of project editor in which the dialog opned */
    private YaProjectEditor projectEditor;

    /* refers to the screen name in which dialog opend */
    private String currentScreen = "";

    /**
     * Show the project property dialog
     * 
     * @param screenName name of the screen in which dialog needs to open
     */
    public void showDialog(String screenName) {
        currentScreen = screenName;
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                projectProperties.center();
            }
        });   
    }

    public ProjectPropertiesDialogBox() {
        uiBinder.createAndBindUi(this);
        projectProperties.setAutoHideEnabled(false);
        projectProperties.setModal(true);
        projectProperties.setCaption(Ode.MESSAGES.projectPropertiesText());

        // Get current instance of YaProjectEditor
    	projectEditor = (YaProjectEditor)Ode.getInstance().getEditorManager().getOpenProjectEditor(
            Ode.getInstance().getCurrentYoungAndroidProjectId());

        MockForm form = projectEditor.getFormFileEditor("Screen1").getForm();

        // Get project properties from the screen1 MockForm
	    EditableProperties editableProperties = form.getProperties();
        Iterator<EditableProperty> properties = editableProperties.iterator();

        // Iterate and put the editable property to the corresponding category in categoryToProperties
        while (properties.hasNext()) {
            EditableProperty property = properties.next();

            if (!categoryToProperties.containsKey(property.getCategory())) {
                categoryToProperties.put(property.getCategory(), new ArrayList<EditableProperty>());
            } 

            categoryToProperties.get(property.getCategory()).add(property);
        }

        // Add the Categories to ListBox - projectPropertyCategoryTitlePanel
        for (String categoryTitle : projectPropertyCategoryTitle) {
            projectPropertyCategoryTitlePanel.addItem(categoryTitle);
            propertiesDeckPanel.add(getPanel(categoryTitle));
        }

        // When category is changed by the user, display related properties
        projectPropertyCategoryTitlePanel.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                propertiesDeckPanel.showWidget(projectPropertyCategoryTitlePanel.getSelectedIndex());
            }
        });

        projectPropertyCategoryTitlePanel.setVisibleItemCount(projectPropertyCategoryTitle.size());
        
        // When dialog is opened, properties related to the General category is shown
        propertiesDeckPanel.showWidget(0);
    }

    /**
     * Build vertical panel for each categories
     * 
     * @param category indicates the category for which we need to build the vertical panel
     * @return vertical panel which contains the all the Editable Property belongs to the particualt category passed as argument
     */
    private VerticalPanel getPanel(String category) {
        // Main 
        VerticalPanel propertiesContainer = new VerticalPanel();
        propertiesContainer.setStyleName("ode-propertyDialogVerticalPanel");

        ArrayList<EditableProperty> properties = categoryToProperties.get(category);

        for (EditableProperty property : properties) {
            // container for displaing one editable property
            VerticalPanel propertyContainer = new VerticalPanel();
            propertyContainer.setStyleName("ode-propertyDialogPropertyContainer");

            // name of the EditableProperty
            Label name = new Label(property.getName());
            name.setStyleName("ode-propertyDialogPropertyTitle");

            // Description of the property
            Label description = new Label(property.getDescription());
            description.setStyleName("ode-propertyDialogPropertyDescription");

            // editor of the editor
            PropertyEditor editor = property.getEditor();
            editor.setStyleName("ode-propertyDialogPropertyEditor");

            // add to the container
            propertyContainer.add(name);
            propertyContainer.add(description);
            propertyContainer.add(editor);

            // add to the main container
            propertiesContainer.add(propertyContainer);
        }  
        
        return propertiesContainer;
    }

    void applyPropertyChanges() {
        if (!"Screen1".equals(currentScreen)) {
            MockForm currentform = projectEditor.getFormFileEditor(currentScreen).getForm();
            if (currentform != null) {
                currentform.projectPropertyChanged();
            }
        }
    }

    @UiHandler("closeDialogBox")
    void handleClose(ClickEvent e) {
        projectProperties.hide();
        applyPropertyChanges();
    }
}