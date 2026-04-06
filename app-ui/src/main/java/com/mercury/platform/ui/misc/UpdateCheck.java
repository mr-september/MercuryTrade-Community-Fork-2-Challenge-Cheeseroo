package com.mercury.platform.ui.misc;

import com.google.gson.Gson;
import com.mercury.platform.TranslationKey;
import com.mercury.platform.core.MercuryConstants;
import com.mercury.platform.core.utils.error.ErrorNotifier;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.dialog.AlertDialog;
import com.mercury.platform.ui.dialog.OkDialog;
import com.mercury.platform.ui.frame.titled.GithubReleaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateCheck {
    private static final String LATEST_RELEASE_URL = "https://github.com/mr-september/MercuryChat/releases/latest";
    private static final String LATEST_RELEASE_API_URL = "https://api.github.com/repos/mr-september/MercuryChat/releases/latest";
    private static final String UPDATE_CHECK_FAILURE = "Failed to check MercuryChat updates from GitHub.";
    private static final String RELEASE_PAGE_FAILURE = "Failed to open the MercuryChat releases page.";

    static {
        MercuryStoreCore.checkForUpdates.subscribe(x -> {
           checkForUpdates(x);
        });
    }
    private final static Logger logger = LogManager.getLogger(UpdateCheck.class.getSimpleName());
    private final static Gson gson = new Gson();

    public static void checkForUpdates(boolean showOnlyIfNewestIsAvailable) {
        logger.info("Checking for updates...");
        GithubReleaseResponse response = getNewestVersion();

        if (response == null) {
            if (showOnlyIfNewestIsAvailable) {
                return;
            }
            SwingUiExecutor.run(() -> {
                AlertDialog dialog = new AlertDialog(callback -> {
                    if (callback) {
                        ExternalBrowser.open(LATEST_RELEASE_URL, RELEASE_PAGE_FAILURE);
                    }
                }, TranslationKey.there_was_a_problem_with_checking_newest_version.value(), null);
                dialog.setTitle(TranslationKey.check_for_updates.value());
                dialog.setVisible(true);
            });
        } else if (StringUtils.isNotEmpty(response.getTag_name()) && response.getTag_name().equals(MercuryConstants.APP_VERSION)) {
            if (showOnlyIfNewestIsAvailable) {
                return;
            }
            SwingUiExecutor.run(() -> {
                OkDialog dialog = new OkDialog(null, TranslationKey.you_have_the_newest_version.value(), null);
                dialog.setTitle(TranslationKey.check_for_updates.value());
                dialog.setVisible(true);
            });
        } else {
            SwingUiExecutor.run(() -> {
                AlertDialog dialog = new AlertDialog(callback -> {
                    if (callback) {
                        ExternalBrowser.open(LATEST_RELEASE_URL, RELEASE_PAGE_FAILURE);
                    }
                }, TranslationKey.there_is_a_newer_version.value(), null);
                dialog.setTitle(TranslationKey.check_for_updates.value());
                dialog.setVisible(true);
            });
        }
    }

    private static GithubReleaseResponse getNewestVersion() {
        HttpURLConnection con = null;
        try {
            URL url = new URL(LATEST_RELEASE_API_URL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "request");
            con.setRequestMethod("GET");
            int code = con.getResponseCode();
            if (code == 200) {
                StringBuilder content = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                }
                return gson.fromJson(content.toString(), GithubReleaseResponse.class);
            } else {
                return null;
            }
        } catch (IOException | RuntimeException e) {
            ErrorNotifier.notify(UPDATE_CHECK_FAILURE, e);
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
