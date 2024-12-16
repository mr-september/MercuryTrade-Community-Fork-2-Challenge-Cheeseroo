package com.mercury.platform.shared.messageparser;

import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;

import java.util.regex.Matcher;

class PoeTradeItemParserKorean2 extends BaseRegexParser {

    // Tested for message 술쟁이: 안녕하세요, 5 exalted(으)로 올려놓은 Standard 리그의 Morbid Grasp, Emerald Ring(을)를 구매하고 싶습니다 (보관함 탭 "~price 5 exalted", 위치: 왼쪽 11, 상단 3)
    // coming from https://www.pathofexile.com/trade2/search/poe2/Standard 12.12.2024
    private static final String poeTradePattern = "^(.+): 안녕하세요, (.+) (.+)\\(으\\)로 올려놓은 (.+) 리그의 (.+)\\(을\\)를 구매하고 싶습니다 \\(보관함 탭 \"(.*)\", 위치: 왼쪽 (\\d+), 상단 (\\d+)\\)$";

    public PoeTradeItemParserKorean2() {
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
        tradeNotification.setTabName(matcher.group(2));
        if (matcher.group(5) != null) {
            tradeNotification.setCurCount(Double.parseDouble(matcher.group(2)));
            tradeNotification.setCurrency(matcher.group(3));
        } else {
            tradeNotification.setCurCount(0d);
            tradeNotification.setCurrency("???");
        }
        tradeNotification.setLeague(matcher.group(4));
        tradeNotification.setType(NotificationType.INC_ITEM_MESSAGE);
        return tradeNotification;
    }
}
