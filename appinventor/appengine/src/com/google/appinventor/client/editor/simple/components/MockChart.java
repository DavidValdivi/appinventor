package com.google.appinventor.client.editor.simple.components;

import com.google.appinventor.client.editor.simple.SimpleEditor;
import com.google.appinventor.client.editor.simple.palette.SimplePaletteItem;
import com.google.appinventor.client.widgets.dnd.DragSource;
import com.google.appinventor.components.common.ComponentConstants;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.resources.client.ImageResource;
import org.pepstock.charba.client.AbstractChart;
import org.pepstock.charba.client.Chart;
import org.pepstock.charba.client.ScatterChart;
import org.pepstock.charba.client.enums.Position;
import org.pepstock.charba.client.resources.EmbeddedResources;
import org.pepstock.charba.client.resources.ResourcesType;

public final class MockChart extends MockContainer {
    public static final String TYPE = "Chart";

    protected AbstractChart chartWidget;

    static {
        ResourcesType.setClientBundle(EmbeddedResources.INSTANCE);
    }

    /**
     * Creates a new instance of a visible component.
     *
     * @param editor editor of source file the component belongs to
     */
    public MockChart(SimpleEditor editor) {
        super(editor, TYPE, images.image(), new MockChartLayout());

        chartWidget = new ScatterChart();

        // Since the Mcok Chart component is not a container in a normal
        // sense (attached components should not be visible), the Chart Widget
        // is added to the root panel, and the root panel itself is initialized.
        // This is done to ensure that Mock Chart Data components can be dragged
        // onto the Chart itself, rather than outside the Chart component.
        rootPanel.setStylePrimaryName("ode-SimpleMockComponent");
        rootPanel.add(chartWidget);
        chartWidget.setWidth("100%"); // Fill root panel with Chart Widget's width

        initComponent(rootPanel);

        // Re-attach all children MockChartData components
        chartWidget.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent arg0) {
                if (arg0.isAttached()) {
                    for (MockComponent child : children) {
                        //((MockChartData) child).addToChart(MockChart.this);
                    }
                }
            }
        });
    }


    @Override
    public int getPreferredWidth() {
        return ComponentConstants.VIDEOPLAYER_PREFERRED_WIDTH;
    }

    @Override
    public int getPreferredHeight() {
        return ComponentConstants.VIDEOPLAYER_PREFERRED_HEIGHT;
    }

    @Override
    public void onPropertyChange(String propertyName, String newValue) {
        super.onPropertyChange(propertyName, newValue);
    }

    /**
     * Returns the Mock Component of the Drag Source.
     *
     * @param source  DragSource instance
     * @return  MockComponent instance
     */
    protected MockComponent getComponentFromDragSource(DragSource source) {
        MockComponent component = null;
        if (source instanceof MockComponent) {
            component = (MockComponent) source;
        } else if (source instanceof SimplePaletteItem) {
            component = (MockComponent) source.getDragWidget();
        }

        return component;
    }
}
