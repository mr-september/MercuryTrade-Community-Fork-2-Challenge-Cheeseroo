package com.mercury.platform.shared.messageparser;

import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;

import java.util.regex.Matcher;

class PoeTradeItemParserBrazilian extends BaseRegexParser {

    // Tested for message zuschnell: Olá, eu gostaria de comprar seu Polcirkeln, Sapphire Ring listado por 26 exalted na Standard (aba do baú: "grande"; posição: esquerda 22, topo 2)
    // coming from https://www.pathofexile.com/trade2/search/poe2/Standard 21.12.2024
    private static final String poeTradePattern = "^(.+): Olá, eu gostaria de comprar seu (.+) listado por (.+) na (.+) \\(aba do baú: \"(.*)\"; posição: esquerda (.*), topo (.*)\\)$";

    public PoeTradeItemParserBrazilian() {
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
