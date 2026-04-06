package com.mercury.platform.ui.components.panel.notification;

import com.mercury.platform.shared.entity.message.FlowDirections;
import com.mercury.platform.ui.components.ComponentsFactory;
import org.apache.commons.lang3.SystemUtils;

import java.awt.*;

public class NotificationLayoutMetrics {
    private static final int WINDOWS_BUFFER_WIDTH = 10;
    private static final int WINDOWS_UPWARDS_BUFFER_HEIGHT = 1500;
    private static final int EXPAND_STUB_HEIGHT = 5;
    private static final Dimension PIN_PREVIEW_SIZE = new Dimension(400, 92);
    private static final Dimension SCANNER_CONTENT_SIZE = new Dimension(10, 60);
    private static final Dimension TIME_PANEL_SIZE = new Dimension(50, 26);
    private static final Dimension NICKNAME_COLLAPSED_SIZE = new Dimension(80, 20);

    private final ComponentsFactory componentsFactory;

    public NotificationLayoutMetrics(ComponentsFactory componentsFactory) {
        this.componentsFactory = componentsFactory;
    }

    public int scaleValue(int logicalValue) {
        return Math.round(this.componentsFactory.getScale() * logicalValue);
    }

    public Dimension getBufferSize(FlowDirections flowDirections) {
        int width = SystemUtils.IS_OS_WINDOWS ? scaleValue(WINDOWS_BUFFER_WIDTH) : 0;
        int height = SystemUtils.IS_OS_WINDOWS && FlowDirections.UPWARDS.equals(flowDirections)
                ? WINDOWS_UPWARDS_BUFFER_HEIGHT
                : 0;
        return new Dimension(width, height);
    }

    public Dimension getExpandStubSize(int expandPanelWidth) {
        return new Dimension(expandPanelWidth, scaleValue(EXPAND_STUB_HEIGHT));
    }

    public Dimension getPinPreviewSize() {
        return scaleDimension(PIN_PREVIEW_SIZE);
    }

    public Dimension getScannerContentSize() {
        return scaleDimension(SCANNER_CONTENT_SIZE);
    }

    public Dimension getTimePanelSize() {
        return scaleDimension(TIME_PANEL_SIZE);
    }

    public Dimension getNicknameCollapsedSize() {
        return scaleDimension(NICKNAME_COLLAPSED_SIZE);
    }

    public int getResponseButtonsHorizontalGap() {
        return scaleValue(5);
    }

    public int getResponseButtonsVerticalGap() {
        return scaleValue(2);
    }

    public int getNotificationMiscPanelGap() {
        return scaleValue(4);
    }

    public int getIncomingInteractionGap() {
        return scaleValue(4);
    }

    public int getOutgoingInteractionGap() {
        return scaleValue(3);
    }

    public int getScannerInteractionGap() {
        return scaleValue(3);
    }

    public int getCurrencyRateGap() {
        return scaleValue(4);
    }

    public int getAdditionalItemRowHeight() {
        return scaleValue(15);
    }

    private Dimension scaleDimension(Dimension initialSize) {
        return this.componentsFactory.convertSize(initialSize);
    }
}
