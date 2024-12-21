package com.mercury.platform.shared.messageparser;

import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;

import java.util.regex.Matcher;

class PoeTradeItemParserFrench extends BaseRegexParser {

    // Tested for message Sillmar: Bonjour, je souhaiterais t'acheter Call of the Brotherhood, Topaz Ring pour 15 exalted dans la ligue Standard (onglet de réserve "A vendre" ; 6e en partant de la gauche, 1e en partant du haut)
    // coming from https://www.pathofexile.com/trade2/search/poe2/Standard 18.12.2024
    private static final String poeTradePattern = "^(.+): Bonjour, je souhaiterais t'acheter (.+) pour (.+) dans la ligue (.+) \\(onglet de réserve \"(.*)\" ; (\\d+)e en partant de la gauche, (\\d+)e en partant du haut\\)$";

    public PoeTradeItemParserFrench() {
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
