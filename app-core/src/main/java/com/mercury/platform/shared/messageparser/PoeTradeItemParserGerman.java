package com.mercury.platform.shared.messageparser;

import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;

import java.util.regex.Matcher;

class PoeTradeItemParserGerman extends BaseRegexParser {

    // Tested for message zuschnell: Biter: Hi, ich möchte ' Shimmering Sapphire' zum angebotenen Preis von 1 exalted in der Standard-Liga kaufen (Truhenfach "TRADE"; Position: 12 von links, 7 von oben)
    // coming from https://www.pathofexile.com/trade2/search/poe2/Standard 21.12.2024
    private static final String poeTradePattern = "^(.+): Hi, ich möchte ' (.+)' zum angebotenen Preis von (.+) in der (.+)-Liga kaufen \\(Truhenfach \"(.*)\"; Position: (\\d+) von links, (\\d+) von oben\\)$";

    public PoeTradeItemParserGerman() {
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
