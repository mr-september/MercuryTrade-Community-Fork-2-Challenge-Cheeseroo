package com.mercury.platform.ui.misc;

import com.google.gson.Gson;
import com.mercury.platform.TranslationKey;
import com.mercury.platform.core.MercuryConstants;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.dialog.AlertDialog;
import com.mercury.platform.ui.dialog.OkDialog;
import com.mercury.platform.ui.frame.titled.GithubReleaseResponse;
import com.mercury.platform.ui.frame.titled.SettingsFrame;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class UpdateCheck {
    static {
        MercuryStoreCore.checkForUpdates.subscribe(x -> {
           checkForUpdates(x);
        });
    }
    private final static Logger logger = LogManager.getLogger(UpdateCheck.class.getSimpleName());
    private final static Gson gson = new Gson();

    public static void checkForUpdates(boolean showOnlyIfNewestIsAvailable) {
        System.out.println("Checking for updates...");
        GithubReleaseResponse response = getNewestVersion();

        if (response == null) {
            if (showOnlyIfNewestIsAvailable) {
                return;
            }
            AlertDialog dialog = new AlertDialog(callback -> {
                if (callback) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/Morph21/MercuryTrade-Community-Fork/releases/latest"));
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }, TranslationKey.there_was_a_problem_with_checking_newest_version.value(), null);
            dialog.setTitle(TranslationKey.check_for_updates.value());
            dialog.setVisible(true);
        } else if (StringUtils.isNotEmpty(response.getTag_name()) && response.getTag_name().equals(MercuryConstants.APP_VERSION)) {
            if (showOnlyIfNewestIsAvailable) {
                return;
            }
            OkDialog dialog = new OkDialog(null, TranslationKey.you_have_the_newest_version.value(), null);
            dialog.setTitle(TranslationKey.check_for_updates.value());
            dialog.setVisible(true);
        } else {
            AlertDialog dialog = new AlertDialog(callback -> {
                if (callback) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/Morph21/MercuryTrade-Community-Fork/releases/latest"));
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }, TranslationKey.there_is_a_newer_version.value(), null);
            dialog.setTitle(TranslationKey.check_for_updates.value());
            dialog.setVisible(true);
        }
    }

    private static GithubReleaseResponse getNewestVersion() {
        try {
            URL url = new URL("https://api.github.com/repos/Morph21/MercuryTrade-Community-Fork/releases/latest");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "request");
            con.setRequestMethod("GET");
            int code = con.getResponseCode();
            if (code == 200) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                return gson.fromJson(content.toString(), GithubReleaseResponse.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }
}
