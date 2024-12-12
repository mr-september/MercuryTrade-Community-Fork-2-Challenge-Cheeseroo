package com.mercury.platform.shared.config.descriptor;


import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.IconConst;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum HotKeyType implements Serializable {
    N_TRADE_PLAYER {
        @Override
        public String getIconPath() {
            return IconConst.TRADE;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.offer_trade.value();
        }
    },
    N_OPEN_CHAT {
        @Override
        public String getIconPath() {
            return IconConst.CHAT_OPEN;
        }

        @Override
        public String getTooltip() {

            return TranslationKey.open_chat.value();
        }
    },
    N_CLOSE_NOTIFICATION {
        @Override
        public String getIconPath() {
            return IconConst.CLOSE;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.close_notification.value();
        }
    },
    //Incoming notification
    N_INVITE_PLAYER {
        @Override
        public String getIconPath() {
            return IconConst.INVITE;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.invite.value();
        }
    },
    N_KICK_PLAYER {
        @Override
        public String getIconPath() {
            return IconConst.KICK;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.kick.value();
        }
    },
    N_STILL_INTERESTING {
        @Override
        public String getIconPath() {
            return IconConst.STILL_INTERESTING;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.still_interested_button.value();
        }
    },
    N_REPEAT_MESSAGE {
        @Override
        public String getIconPath() {
            return IconConst.RELOAD_HISTORY;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.repeat_message.value();
        }
    },
    N_SWITCH_CHAT {
        @Override
        public String getIconPath() {
            return IconConst.CHAT_HISTORY;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.chat_history.value();
        }
    },
    //Outgoing/scanner notification
    N_VISITE_HIDEOUT {
        @Override
        public String getIconPath() {
            return IconConst.VISIT_HIDEOUT;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.visit_ho.value();
        }
    },
    N_LEAVE {
        @Override
        public String getIconPath() {
            return IconConst.LEAVE;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.leave.value();
        }
    },
    N_BACK_TO_HIDEOUT {
        @Override
        public String getIconPath() {
            return IconConst.BACK_TO_HIDEOUT;
        }

        @Override
        public String getTooltip() {
            return null;
        }
    },
    N_WHO_IS {
        @Override
        public String getIconPath() {
            return IconConst.WHO_IS;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.who_is.value();
        }
    },
    //scanner
    N_QUICK_RESPONSE {
        @Override
        public String getIconPath() {
            return IconConst.CHAT_SCANNER_RESPONSE;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.quick_response.value();
        }
    },
    T_TO_HIDEOUT {
        @Override
        public String getIconPath() {
            return IconConst.HIDEOUT;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.to_hideout.value();
        }
    },
    T_DND {
        @Override
        public String getIconPath() {
            return IconConst.VISIBLE_DND_MODE;
        }

        @Override
        public String getTooltip() {
            return TranslationKey.dnd.value();
        }
    };

    public static boolean contains(HotKeyType entry) {
        return Arrays.stream(HotKeyType.values())
                       .filter(it -> it.equals(entry))
                       .collect(Collectors.toList())
                       .size() != 0;
    }

    public abstract String getIconPath();

    public abstract String getTooltip();
}
