package com.mercury.platform.shared.messageparser;

import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;

import java.util.regex.Matcher;

class PoeTradeItemParserChina extends BaseRegexParser {

    // Tested for message 神圣怨恨初火: 你好，我想購買 Ghoul Hide, Advanced Marabout Garb 標價 1 exalted 在 Standard (倉庫頁 "asdac"; 位置: 左 5, 上 10)
    // coming from https://www.pathofexile.com/trade2/search/poe2/Standard 12.12.2024
    private static final String poeTradePattern = "^(.+): 你好，我想購買 (.+) 標價 (\\d+ exalted) 在 (.+) \\(倉庫頁 \"(.+)\"; 位置: 左 (\\d+), 上 (\\d+)\\)$";

    public PoeTradeItemParserChina() {
        super(poeTradePattern);
    }

    @Override
    protected NotificationDescriptor parse(Matcher matcher, String whisper) {
        ItemTradeNotificationDescriptor tradeNotification = new ItemTradeNotificationDescriptor();
        tradeNotification.setWhisperNickname(matcher.group(1));
        tradeNotification.setSourceString(matcher.group(0));
        tradeNotification.setItemName(matcher.group(2));
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
