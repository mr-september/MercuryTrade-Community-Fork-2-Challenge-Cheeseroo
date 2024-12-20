package com.mercury.platform.ui.misc;

import com.mercury.platform.ui.components.panel.misc.ToggleCallback;
import lombok.Setter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToggleAdapter extends MouseAdapter {
    ToggleCallback firstState;
    ToggleCallback secondState;
    @Setter
    private boolean state;

    public ToggleAdapter(ToggleCallback firstState, ToggleCallback secondState, boolean initialState) {
        this.firstState = firstState;
        this.secondState = secondState;
        this.state = initialState;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (state) {
            firstState.onToggle();
            state = false;
        } else {
            secondState.onToggle();
            state = true;
        }
    }
}
