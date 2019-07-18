package com.google.appinventor.client.editor.simple.components;

import com.google.appinventor.client.output.OdeLog;
import com.google.appinventor.components.common.ComponentConstants;

import java.util.Map;

public class MockChartLayout extends MockLayout {

    MockChartLayout() {
        layoutWidth = ComponentConstants.VIDEOPLAYER_PREFERRED_WIDTH;
        layoutHeight = ComponentConstants.VIDEOPLAYER_PREFERRED_HEIGHT;
    }

    @Override
    LayoutInfo createContainerLayoutInfo(Map<MockComponent, LayoutInfo> layoutInfoMap) {
        return new LayoutInfo(layoutInfoMap, container) {
            @Override
            int calculateAutomaticWidth() {
                return ComponentConstants.VIDEOPLAYER_PREFERRED_WIDTH;
            }

            @Override
            int calculateAutomaticHeight() {
                return ComponentConstants.VIDEOPLAYER_PREFERRED_HEIGHT;
            }
        };
    }

    @Override
    void layoutChildren(LayoutInfo containerLayoutInfo) {

    }

    @Override
    boolean onDrop(MockComponent source, int x, int y, int offsetX, int offsetY) {
        if (source instanceof MockChartData) {
            container.addComponent(source);
            ((MockChartData)source).addToChart((MockChart) container);
            return true;
        } else if (source instanceof MockNonVisibleComponent) {
            // Checking that it is indeed a CSV File is done in the addCSVFile method.
            ((MockChart)container).addCSVFile((MockNonVisibleComponent)source);
            return true;
        }

        return false;
    }
}
