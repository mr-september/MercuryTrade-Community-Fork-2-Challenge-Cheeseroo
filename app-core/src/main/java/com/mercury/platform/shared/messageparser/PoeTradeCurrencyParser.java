package com.mercury.platform.shared.messageparser;

import com.mercury.platform.shared.entity.message.CurrencyTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

class PoeTradeCurrencyParser extends BaseRegexParser {

    private static final String poeCurrencyPattern = "^(.*\\s)?(.+): (.+ to buy your (\\d+(\\.\\d+)?)? (.+) for my (\\d+(\\.\\d+)?)? (.+) in\\s+(.+?))$";

    public PoeTradeCurrencyParser() {
        super(poeCurrencyPattern);
    }

    @Override
    protected NotificationDescriptor parse(Matcher matcher, String whisper) {
        CurrencyTradeNotificationDescriptor tradeNotification = new CurrencyTradeNotificationDescriptor();
        if (matcher.group(6).contains("&") || matcher.group(6)
                                                      .contains(",")) {  //todo this shit for bulk map
            String bulkItems = matcher.group(4).trim() + " " + matcher.group(6).trim();
            tradeNotification.setItems(Arrays.stream(StringUtils.split(bulkItems, ",&"))
                                             .map(String::trim)
                                             .collect(Collectors.toList()));
        } else {
            tradeNotification.setCurrForSaleCount(Double.parseDouble(matcher.group(4).trim()));
            tradeNotification.setCurrForSaleTitle(matcher.group(6).trim());
        }

        tradeNotification.setWhisperNickname(matcher.group(2).trim());
        tradeNotification.setSourceString(matcher.group(3).trim());
        tradeNotification.setCurCount(Double.parseDouble(matcher.group(7).trim()));
        tradeNotification.setCurrency(matcher.group(9).trim());
        String leagueAndOffer = matcher.group(10).trim();
        if (leagueAndOffer.endsWith(".")) {
            leagueAndOffer = leagueAndOffer.substring(0, leagueAndOffer.length() - 1).trim();
        }
        String[] split = leagueAndOffer.split("\\s+", 2);
        String league = split[0].trim();
        if (league.endsWith(".")) {
            league = league.substring(0, league.length() - 1).trim();
        }
        tradeNotification.setLeague(league);
        if (split.length > 1) {
            tradeNotification.setOffer(split[1].trim());
        }
        tradeNotification.setType(NotificationType.INC_CURRENCY_MESSAGE);
        return tradeNotification;
    }
}
