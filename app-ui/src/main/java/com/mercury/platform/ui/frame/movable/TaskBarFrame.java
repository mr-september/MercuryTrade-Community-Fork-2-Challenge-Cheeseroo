package com.mercury.platform.ui.frame.movable;

import com.mercury.platform.shared.FrameVisibleState;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.components.panel.taskbar.MercuryTaskBarController;
import com.mercury.platform.ui.components.panel.taskbar.TaskBarController;
import com.mercury.platform.ui.components.panel.taskbar.TaskBarPanel;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class TaskBarFrame extends AbstractMovableComponentFrame {
    private static final int EXIT_STATE_DELAY_MS = 120;
    private static final int HOVER_MARGIN = 8;

    @Getter
    private int MIN_WIDTH;
    private int MAX_WIDTH;
    private boolean collapseEnabled;
    private MouseListener interactionListener;
    private Timer exitStateTimer;
    private TaskBarPanel taskBarPanel;

    public TaskBarFrame() {
        super();
        this.componentsFactory.setScale(this.scaleConfig.get("taskbar"));
        this.stubComponentsFactory.setScale(this.scaleConfig.get("taskbar"));
        this.processEResize = false;
        this.processSEResize = false;
        this.prevState = FrameVisibleState.SHOW;

    }

    private void enableCollapsedStripMode() {
        this.collapseEnabled = true;
        this.showTaskBar();
        this.collapseToMinimumWidth();
    }

    private void disableCollapsedStripMode() {
        this.collapseEnabled = false;
        this.stopExitStateTimer();
        this.showTaskBar();
        this.expandToMaximumWidth();
    }

    @Override
    protected LayoutManager getFrameLayout() {
        return new BorderLayout();
    }

    @Override
    public void subscribe() {
    }

    /**
     * For 'trident' property animations
     *
     * @param width next width
     */
    public void setWidth(int width) {
        int height = this.getHeight();
        if (height <= 0) {
            height = Math.max(this.getMinimumSize().height, this.getPreferredSize().height);
        }
        this.setSize(new Dimension(width, height));
    }

    @Override
    protected void onLock() {
        super.onLock();
        this.enableCollapsedStripMode();
    }

    @Override
    protected void onUnlock() {
        this.disableCollapsedStripMode();
        super.onUnlock();
    }


    @Override
    protected JPanel getPanelForPINSettings() {
        this.disableCollapsedStripMode();
        JPanel panel = this.componentsFactory.getJPanel(new BorderLayout(), AppThemeColor.FRAME);
        JLabel textLabel = this.componentsFactory.getTextLabel(FontStyle.BOLD, AppThemeColor.TEXT_DEFAULT, TextAlignment.CENTER, 22f, "Task Bar");
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(textLabel);
        panel.setPreferredSize(this.getPreferredSize());
        return panel;
    }

    @Override
    protected void registerDirectScaleHandler() {
        MercuryStoreUI.taskBarScaleSubject.subscribe(this::changeScale);
    }

    @Override
    protected void performScaling(Map<String, Float> scaleData) {
        this.componentsFactory.setScale(scaleData.get("taskbar"));
        onViewInit();
    }

    public void collapseToMinimumWidth() {
        this.setWidth(this.MIN_WIDTH);
        this.showTaskBar();
        this.revalidate();
        this.repaint();
    }

    private void expandToMaximumWidth() {
        this.setWidth(this.MAX_WIDTH);
        this.revalidate();
        this.repaint();
    }

    private MouseListener createInteractionListener() {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                TaskBarFrame.this.stopExitStateTimer();
                TaskBarFrame.this.showTaskBar();
                if (TaskBarFrame.this.collapseEnabled) {
                    TaskBarFrame.this.expandToMaximumWidth();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                TaskBarFrame.this.scheduleExitStateEvaluation();
            }
        };
    }

    @Override
    public void onViewInit() {
        JPanel panel = componentsFactory.getTransparentPanel(new BorderLayout());

        if (this.interactionListener != null) {
            this.removeMouseListener(this.interactionListener);
            if (this.taskBarPanel != null) {
                this.taskBarPanel.removeMouseListener(this.interactionListener);
            }
        }
        this.initializeExitStateTimer();

        this.interactionListener = this.createInteractionListener();
        taskBarPanel = new TaskBarPanel(new MercuryTaskBarController(), componentsFactory, this.interactionListener);
        panel.add(taskBarPanel, BorderLayout.CENTER);
        panel.setBackground(AppThemeColor.FRAME);
        mainContainer = panel;
        this.setContentPane(panel);
        this.pack();
        this.repaint();
        this.updateTaskBarBounds(taskBarPanel, true);
        this.enableCollapsedStripMode();
        this.addMouseListener(this.interactionListener);
        this.taskBarPanel.addMouseListener(this.interactionListener);
        this.applyExitStateIfNeeded();
    }

    @Override
    protected JPanel defaultView(ComponentsFactory factory) {
        TaskBarController controller = new TaskBarController() {
            @Override
            public void enableDND() {
            }

            @Override
            public void disableDND() {
            }

            @Override
            public void enablePushbullet() {

            }

            @Override
            public void disablePushbullet() {

            }

            @Override
            public void showITH() {
            }

            @Override
            public void performHideout() {
            }

            @Override
            public void showHelpIG() {
            }

            @Override
            public void showChatFiler() {
            }

            @Override
            public void showHistory() {
            }

            @Override
            public void openPINSettings() {
            }

            @Override
            public void openScaleSettings() {
            }

            @Override
            public void showSettings() {
            }

            @Override
            public void exit() {
            }

            @Override
            public void hideMessageNotifications() {

            }

            @Override
            public void showMessageNotifications() {

            }

            @Override
            public void performJoinChannel() {

            }
        };
        JPanel panel = factory.getTransparentPanel(new BorderLayout());
        TaskBarPanel taskBarPanel = new TaskBarPanel(controller, factory, this.createInteractionListener());
        panel.add(taskBarPanel, BorderLayout.CENTER);
        panel.setBackground(AppThemeColor.FRAME);
        updateTaskBarBounds(taskBarPanel, true);
        return panel;
    }

    private void updateTaskBarBounds(TaskBarPanel taskBarPanel, boolean collapseToMinimumWidth) {
        int horizontalInsets = getHorizontalRootInsets();
        Dimension stripSize = taskBarPanel.getStripSize();
        int frameHeight = Math.max(this.getHeight(), taskBarPanel.getPreferredSize().height);
        this.MIN_WIDTH = taskBarPanel.getCollapsedWidth() + horizontalInsets;
        this.MAX_WIDTH = stripSize.width + horizontalInsets;
        this.setMinimumSize(new Dimension(this.MIN_WIDTH, frameHeight));
        this.setMaximumSize(new Dimension(this.MAX_WIDTH, frameHeight));
        this.setSize(new Dimension(collapseToMinimumWidth ? this.MIN_WIDTH : this.MAX_WIDTH, frameHeight));
    }

    private int getHorizontalRootInsets() {
        if (this.getRootPane() == null || this.getRootPane().getBorder() == null) {
            return 0;
        }
        Insets borderInsets = this.getRootPane().getBorder().getBorderInsets(this.getRootPane());
        return borderInsets.left + borderInsets.right;
    }

    private void initializeExitStateTimer() {
        if (this.exitStateTimer != null) {
            this.exitStateTimer.stop();
        }
        this.exitStateTimer = new Timer(EXIT_STATE_DELAY_MS, action -> this.applyExitStateIfNeeded());
        this.exitStateTimer.setRepeats(false);
    }

    private void showTaskBar() {
        this.setOpacity(1f);
    }

    private void scheduleExitStateEvaluation() {
        if (this.exitStateTimer != null) {
            this.exitStateTimer.restart();
        }
    }

    private void stopExitStateTimer() {
        if (this.exitStateTimer != null) {
            this.exitStateTimer.stop();
        }
    }

    private void applyExitStateIfNeeded() {
        if (this.EResizeSpace || this.isPointerWithinTaskBarBounds()) {
            return;
        }
        if (this.collapseEnabled) {
            this.collapseToMinimumWidth();
            return;
        }
        if (Configuration.get().applicationConfiguration().get().isHideTaskbarUntilHover()) {
            this.setOpacity(0.01f);
        }
    }

    private boolean isPointerWithinTaskBarBounds() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo == null) {
            return false;
        }
        Rectangle taskBarBounds = this.getTaskBarBoundsOnScreen();
        if (taskBarBounds == null) {
            return false;
        }
        taskBarBounds.grow(HOVER_MARGIN, HOVER_MARGIN);
        return taskBarBounds.contains(pointerInfo.getLocation());
    }

    private Rectangle getTaskBarBoundsOnScreen() {
        Rectangle bounds = null;
        if (this.isShowing()) {
            bounds = new Rectangle(this.getLocationOnScreen(), this.getSize());
        }
        if (this.taskBarPanel != null && this.taskBarPanel.isShowing()) {
            Rectangle panelBounds = new Rectangle(this.taskBarPanel.getLocationOnScreen(), this.taskBarPanel.getSize());
            bounds = bounds == null ? panelBounds : bounds.union(panelBounds);
        }
        return bounds;
    }
}
