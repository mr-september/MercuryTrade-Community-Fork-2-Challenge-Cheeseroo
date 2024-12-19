package com.mercury.platform.shared.hotkey;

import com.mercury.platform.shared.config.descriptor.HotKeyDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.HashSet;
import java.util.Set;

public class MercuryNativeKeyListener implements NativeKeyListener {
    private boolean menuPressed;
    private boolean shiftPressed;
    private boolean ctrlpressed;

    private boolean block;

    private Set<Integer> lastPressed = new HashSet<>();

    public MercuryNativeKeyListener() {
        MercuryStoreCore.blockHotkeySubject.subscribe(state -> this.block = state);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        switch (nativeKeyEvent.getKeyCode()) {
            case 42: {
                shiftPressed = true;
                break;
            }
            case 29: {
                ctrlpressed = true;
                break;
            }
            case 56: {
                menuPressed = true;
                break;
            }
            default: {
                if (!this.block) {
                    if (!lastPressed.contains(nativeKeyEvent.getKeyCode())) {
                        System.out.println("pressed " + nativeKeyEvent.getKeyChar());
                        MercuryStoreCore.hotKeySubject.onNext(this.getDescriptor(nativeKeyEvent));
                        lastPressed.add(nativeKeyEvent.getKeyCode());
                    }
                }
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        if (!this.block) {
            System.out.println("released " + nativeKeyEvent.getKeyChar());
            MercuryStoreCore.hotKeyReleaseSubject.onNext(this.getDescriptor(nativeKeyEvent));
            lastPressed.remove(nativeKeyEvent.getKeyCode());
        }
        switch (nativeKeyEvent.getKeyCode()) {
            case 42: {
                shiftPressed = false;
                break;
            }
            case 29: {
                ctrlpressed = false;
                break;
            }
            case 56: {
                menuPressed = false;
                break;
            }
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    private String getButtonText(HotKeyDescriptor descriptor) {
        String text = descriptor.getTitle();
        if (descriptor.isShiftPressed())
            text = "Shift + " + text;
        if (descriptor.isMenuPressed())
            text = "Alt + " + text;
        if (descriptor.isControlPressed())
            text = "Ctrl + " + text;
        return text;
    }

    private HotKeyDescriptor getDescriptor(NativeKeyEvent nativeKeyEvent) {
        HotKeyDescriptor hotKeyDescriptor = new HotKeyDescriptor();
        hotKeyDescriptor.setTitle(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
        hotKeyDescriptor.setVirtualKeyCode(nativeKeyEvent.getKeyCode());
        hotKeyDescriptor.setControlPressed(ctrlpressed);
        hotKeyDescriptor.setShiftPressed(shiftPressed);
        hotKeyDescriptor.setMenuPressed(menuPressed);

        hotKeyDescriptor.setTitle(this.getButtonText(hotKeyDescriptor));
        return hotKeyDescriptor;
    }
}
