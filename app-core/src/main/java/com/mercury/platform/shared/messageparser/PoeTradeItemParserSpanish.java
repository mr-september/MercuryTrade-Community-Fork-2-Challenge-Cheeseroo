package com.mercury.platform.shared.messageparser;

import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;

import java.util.regex.Matcher;

class PoeTradeItemParserSpanish extends BaseRegexParser {

    // Tested for message zuschnell: Morph_MT: Hola, quisiera comprar tu Djinn Barya listado por 1 exalted en Standard (pestaña de alijo "~price 1 exalted"; posición: izquierda 3, arriba 3)
    // coming from https://www.pathofexile.com/trade2/search/poe2/Standard 23.12.2024
    private static final String poeTradePattern = "^(.+): Hola, quisiera comprar tu (.+) listado por (.+) en (.+) \\(pestaña de alijo \"(.*)\"; posición: izquierda (.*), arriba (.*)\\)$";

    public PoeTradeItemParserSpanish() {
        super(poeTradePattern);
    }

    @Override
    protected NotificationDescriptor parse(Matcher matcher, String whisper) {
        ItemTradeNotificationDescriptor tradeNotification = new ItemTradeNotificationDescriptor();
        tradeNotification.setWhisperNickname(matcher.group(1));
        tradeNotification.setSourceString(matcher.group(0));
        tradeNotification.setItemName(matcher.group(2));
        tradeNotification.setTabName(matcher.group(5));
        tradeNotification.setLeft(Integer.parseInt(matcher.group(6)));
        tradeNotification.setTop(Integer.parseInt(matcher.group(7)));
        if (matcher.group(3) != null) {
            String[] split = matcher.group(3).split(" ");
            tradeNotification.setCurCount(Double.parseDouble(split[0]));
            String temp = "";
            for (int i = 1; i < split.length; i++) {
                temp += split[i];
                if (i < split.length - 1) {
                    temp += " ";
                }
            }
            tradeNotification.setCurrency(temp);
        } else {
            tradeNotification.setCurCount(0d);
            tradeNotification.setCurrency("???");
        }
        tradeNotification.setLeague(matcher.group(4));
        tradeNotification.setType(NotificationType.INC_ITEM_MESSAGE);
        return tradeNotification;
    }
}
