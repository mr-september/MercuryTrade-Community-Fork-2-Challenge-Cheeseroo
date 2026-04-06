package com.mercury.platform.ui.misc;

import com.mercury.platform.core.utils.error.ErrorNotifier;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public final class ExternalBrowser {
    private ExternalBrowser() {
    }

    public static boolean open(String targetUrl, String failureMessage) {
        return open(URI.create(targetUrl), failureMessage);
    }

    static boolean open(URI targetUri, String failureMessage) {
        if (!Desktop.isDesktopSupported()) {
            ErrorNotifier.notify(failureMessage, new IllegalStateException("Desktop browsing is not supported."));
            return false;
        }
        return open(targetUri, failureMessage, uri -> Desktop.getDesktop().browse(uri));
    }

    static boolean open(URI targetUri, String failureMessage, BrowserLauncher browserLauncher) {
        try {
            browserLauncher.browse(targetUri);
            return true;
        } catch (IOException | RuntimeException e) {
            ErrorNotifier.notify(failureMessage, e);
            return false;
        }
    }

    interface BrowserLauncher {
        void browse(URI uri) throws IOException;
    }
}
