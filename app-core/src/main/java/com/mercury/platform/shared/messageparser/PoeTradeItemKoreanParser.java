package com.mercury.platform.shared.messageparser;

import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;

import java.util.regex.Matcher;

class PoeTradeItemKoreanParser extends BaseRegexParser {

    /**
     * Any text between {@code \Q} and {@code \E} will be matched as raw text (escaped).
     */
    private final static String poeTradeKoreanRegex = "^\\s*(?<name>[^:]+): (?<message>\\Q안녕하세요, \\E(?<league>.+?)\\Q(보관함 탭 \"\\E(?<stashtab>[^\"]+)\\Q\", 위치: 왼쪽 \\E(?<left>\\d+),\\s*\\Q상단 \\E(?<top>\\d+)\\Q)에 \\E(?<price>[\\d.]*)\\s+(?<currency>.+?)\\Q(으)로 올려놓은 \\E(?<item>.+?)\\Q(을)를 구매하고 싶습니다\\E)$";

    public PoeTradeItemKoreanParser() {
        super(poeTradeKoreanRegex);
    }

    @Override
    protected NotificationDescriptor parse(Matcher matcher, String whisper) {
        ItemTradeNotificationDescriptor tradeNotification = new ItemTradeNotificationDescriptor();
        tradeNotification.setWhisperNickname(matcher.group("name").trim());
        tradeNotification.setSourceString(whisper);
        
        tradeNotification.setLeague(matcher.group("league").trim());
        tradeNotification.setTabName(matcher.group("stashtab").trim());
        tradeNotification.setLeft(Integer.parseInt(matcher.group("left").trim()));
        tradeNotification.setTop(Integer.parseInt(matcher.group("top").trim()));
        
        if (matcher.group("price") != null && !matcher.group("price").trim().isEmpty()) {
            tradeNotification.setCurCount(Double.parseDouble(matcher.group("price").trim()));
            tradeNotification.setCurrency(matcher.group("currency").trim());
        } else {
            tradeNotification.setCurCount(0d);
            tradeNotification.setCurrency("???");
        }
        
        tradeNotification.setItemName(matcher.group("item").trim());
        tradeNotification.setType(NotificationType.INC_ITEM_MESSAGE);

        return tradeNotification;
    }
}
