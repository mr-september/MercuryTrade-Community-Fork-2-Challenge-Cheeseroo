package com.mercury.platform.shared.messageparser;

import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;

import java.util.regex.Matcher;

class PoeTradeItemParserJapanese extends BaseRegexParser {

    // Tested for message ナーフ神ジョナサン: こんにちは、Standard リーグで 3 exalted で売っている、あなたの Havoc Band, Emerald Ring を購入したいです (スタッシュタブ "~b/o 3 exalted"; 位置: 左から 6, 上から 7)
    // coming from https://www.pathofexile.com/trade2/search/poe2/Standard 12.12.2024
    private static final String poeTradePattern = "^(.+): こんにちは、(.+) リーグで (.+) (.+) で売っている、あなたの (.+) を購入したいです \\(スタッシュタブ \"(.+)\"; 位置: 左から (\\d+), 上から (\\d+)\\)$";

    public PoeTradeItemParserJapanese() {
        super(poeTradePattern);
    }

    @Override
    protected NotificationDescriptor parse(Matcher matcher, String whisper) {
        ItemTradeNotificationDescriptor tradeNotification = new ItemTradeNotificationDescriptor();
        tradeNotification.setWhisperNickname(matcher.group(1));
        tradeNotification.setSourceString(matcher.group(0));
        tradeNotification.setItemName(matcher.group(5));
        tradeNotification.setLeft(Integer.parseInt(matcher.group(7)));
        tradeNotification.setTop(Integer.parseInt(matcher.group(8)));
        tradeNotification.setTabName(matcher.group(6));

        if (matcher.group(3) != null) {
            tradeNotification.setCurCount(Double.parseDouble(matcher.group(3)));
            tradeNotification.setCurrency(matcher.group(4));
        } else {
            tradeNotification.setCurCount(0d);
            tradeNotification.setCurrency("???");
        }
        tradeNotification.setLeague(matcher.group(2));
        tradeNotification.setType(NotificationType.INC_ITEM_MESSAGE);
        return tradeNotification;
    }
}
