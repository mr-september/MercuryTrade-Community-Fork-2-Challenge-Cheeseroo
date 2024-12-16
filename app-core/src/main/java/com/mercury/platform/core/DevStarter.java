package com.mercury.platform.core;


import com.mercury.platform.LangTranslator;
import com.mercury.platform.core.misc.SoundNotifier;
import com.mercury.platform.shared.FrameVisibleState;
import com.mercury.platform.shared.HistoryManager;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.MercuryConfigManager;
import com.mercury.platform.shared.config.MercuryConfigurationSource;
import com.mercury.platform.shared.hotkey.ClipboardListener;
import com.mercury.platform.shared.hotkey.HotKeysInterceptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.shared.wh.WhisperHelperHandler;

import java.io.IOException;

public class DevStarter {

    public static FrameVisibleState APP_STATUS = FrameVisibleState.HIDE;
    private volatile int delay = 100;

    public void startApplication() throws IOException {
        MercuryConfigManager configuration = new MercuryConfigManager(new MercuryConfigurationSource());
        configuration.load();
        Configuration.set(configuration);
        LangTranslator.getInstance().changeLanguage(Configuration.get().applicationConfiguration().get().getLanguages());
        new SoundNotifier();
        new ChatHelper();
        new HotKeysInterceptor();
        new WhisperHelperHandler();
        ClipboardListener.createListener();

        HistoryManager.INSTANCE.load();
        MercuryStoreCore.uiLoadedSubject.subscribe((Boolean state) -> {
            APP_STATUS = FrameVisibleState.SHOW;
            MercuryStoreCore.frameVisibleSubject.onNext(FrameVisibleState.SHOW);
        });
        MercuryStoreCore.showingDelaySubject.subscribe(state -> this.delay = 100);
        MercuryStoreCore.shutdownAppSubject.subscribe(state -> System.exit(0));
    }

}
