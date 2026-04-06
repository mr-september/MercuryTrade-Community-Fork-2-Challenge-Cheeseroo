package com.mercury.platform.shared;

import com.mercury.platform.shared.entity.message.CurrencyTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.messageparser.MessageParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Created by Константин on 11.01.2017.
 */
public class NotificationDescriptorParserTest {
    private MessageParser parser;
    @BeforeEach
    public void before(){
        parser = new MessageParser();
    }

    @Test
    public void testPoeTrade()  throws Exception{
        ItemTradeNotificationDescriptor message1 = (ItemTradeNotificationDescriptor) parser.parse("@From <TEST> Pubesmannen: Hi, I would like to buy your Bronn's Lithe Cutthroat's Garb listed for 1 alteration in Hardcore Legacy (stash tab \"Maps\"; position: left 5, top 6)");
        assertEquals("Pubesmannen", message1.getWhisperNickname());
        assertEquals("Hi, I would like to buy your Bronn's Lithe Cutthroat's Garb listed for 1 alteration in Hardcore Legacy (stash tab \"Maps\"; position: left 5, top 6)", message1.getSourceString());
        assertEquals("Bronn's Lithe Cutthroat's Garb", message1.getItemName());
        assertEquals("alteration", message1.getCurrency());
        assertEquals(Double.valueOf(1), message1.getCurCount());
        assertEquals("", message1.getOffer());

        ItemTradeNotificationDescriptor message2 = (ItemTradeNotificationDescriptor) parser.parse("@From <TEST> Pubesmannen: Hi, I would like to buy your Bronn's Lithe Cutthroat's Garb listed for 15 exalt in Legacy (stash tab \"Maps\"; position: left 5, top 6) offer 32");
        assertEquals("Pubesmannen", message2.getWhisperNickname());
        assertEquals("Hi, I would like to buy your Bronn's Lithe Cutthroat's Garb listed for 15 exalt in Legacy (stash tab \"Maps\"; position: left 5, top 6) offer 32", message2.getSourceString());
        assertEquals("Bronn's Lithe Cutthroat's Garb", message2.getItemName());
        assertEquals("exalt", message2.getCurrency());
        assertEquals(Double.valueOf(15), message2.getCurCount());
        assertEquals("Legacy", message2.getLeague());

        assertEquals("offer 32", message2.getOffer());
    }
    @Test
    public void testPoeTradeNoBuyout()  throws Exception{
        ItemTradeNotificationDescriptor message1 = (ItemTradeNotificationDescriptor) parser.parse("@From Pubesmannen: Hi, I would like to buy your level 1 4% Reduced Mana Support in Hardcore Legacy (stash tab \"qgems\"; position: left 12, top 4)");
        assertEquals("Pubesmannen", message1.getWhisperNickname());
        assertEquals("Hi, I would like to buy your level 1 4% Reduced Mana Support in Hardcore Legacy (stash tab \"qgems\"; position: left 12, top 4)", message1.getSourceString());
        assertEquals("level 1 4% Reduced Mana Support", message1.getItemName());
        assertEquals(Double.valueOf(0d), message1.getCurCount());
        assertEquals("???", message1.getCurrency());
        assertEquals("", message1.getOffer());
        assertEquals("Hardcore Legacy", message1.getLeague());

        ItemTradeNotificationDescriptor message2 = (ItemTradeNotificationDescriptor) parser.parse("@From Pubesmannen: Hi, I would like to buy your level 1 4% Reduced Mana Support in Hardcore (stash tab \"qgems\"; position: left 12, top 4)");
        assertEquals("Pubesmannen", message2.getWhisperNickname());
        assertEquals("Hi, I would like to buy your level 1 4% Reduced Mana Support in Hardcore (stash tab \"qgems\"; position: left 12, top 4)", message2.getSourceString());
        assertEquals("level 1 4% Reduced Mana Support", message2.getItemName());
        assertEquals(Double.valueOf(0d), message2.getCurCount());
        assertEquals("???", message2.getCurrency());
        assertEquals("", message2.getOffer());
        assertEquals("Hardcore", message2.getLeague());
    }

    @Test
    public void testPoeCurrency()  throws Exception{
        CurrencyTradeNotificationDescriptor message1 = (CurrencyTradeNotificationDescriptor) parser.parse("@From tradeeer: Hi, I'd like to buy your 366 chaos for my 5 exalted in Legacy.");
        assertEquals("tradeeer", message1.getWhisperNickname());
        assertEquals("Hi, I'd like to buy your 366 chaos for my 5 exalted in Legacy.", message1.getSourceString());
        assertEquals(Double.valueOf(366), message1.getCurrForSaleCount());
        assertEquals("chaos", message1.getCurrForSaleTitle());
        assertEquals("exalted", message1.getCurrency());
        assertEquals(Double.valueOf(5), message1.getCurCount());
        assertEquals("Legacy", message1.getLeague());
        assertEquals("", message1.getOffer());

        CurrencyTradeNotificationDescriptor message2 = (CurrencyTradeNotificationDescriptor) parser.parse("@From <qwe> tradeeer: Hi, I'd like to buy your 366 chaos for my 5 exalted in Legacy. 123");
        assertEquals("tradeeer", message2.getWhisperNickname());
        assertEquals("Hi, I'd like to buy your 366 chaos for my 5 exalted in Legacy. 123", message2.getSourceString());
        assertEquals(Double.valueOf(366), message2.getCurrForSaleCount());
        assertEquals("chaos", message2.getCurrForSaleTitle());
        assertEquals("exalted", message2.getCurrency());
        assertEquals(Double.valueOf(5), message2.getCurCount());
        assertEquals("Legacy", message2.getLeague());
        assertEquals("123", message2.getOffer());
    }

    @Test
    public void testSourceStringNormalization() {
        NotificationDescriptor descriptor = new NotificationDescriptor();

        descriptor.setSourceString(" Pubesmannen: Hi, I would like to buy your level 1 4% Reduced Mana Support in Hardcore Legacy (stash tab \"qgems\"; position: left 12, top 4) ");
        assertEquals("Hi, I would like to buy your level 1 4% Reduced Mana Support in Hardcore Legacy (stash tab \"qgems\"; position: left 12, top 4)", descriptor.getSourceString());

        descriptor.setSourceString("Hi, I would like to buy your level 1 4% Reduced Mana Support in Hardcore Legacy (stash tab \"qgems\"; position: left 12, top 4)");
        assertEquals("Hi, I would like to buy your level 1 4% Reduced Mana Support in Hardcore Legacy (stash tab \"qgems\"; position: left 12, top 4)", descriptor.getSourceString());

        descriptor.setSourceString("안녕하세요, 결전(보관함 탭 \"젬 \", 위치: 왼쪽 12, 상단 12)에 1 chaos(으)로 올려놓은 레벨 1 0% 향상 보조(을)를 구매하고 싶습니다");
        assertEquals("안녕하세요, 결전(보관함 탭 \"젬 \", 위치: 왼쪽 12, 상단 12)에 1 chaos(으)로 올려놓은 레벨 1 0% 향상 보조(을)를 구매하고 싶습니다", descriptor.getSourceString());
    }

}
