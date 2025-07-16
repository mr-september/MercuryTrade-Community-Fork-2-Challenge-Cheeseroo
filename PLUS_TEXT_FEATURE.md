# Plus Text Feature for Chat Scanner

## Overview
The Plus Text feature is a new enhancement to the MercuryTrade chat scanner that automatically detects and responds to the modern "+text" syntax used in Path of Exile global chat. This feature is designed to handle the new trend where users share activities using patterns like "free uber elder +elder" and interested players respond with "+elder" in global chat instead of private messages.

## How It Works

### Detection
- The chat scanner automatically detects messages containing "+text" patterns (e.g., "+elder", "+lab", "+trial", etc.)
- The pattern matching uses regex: `\+\w+` to find any "+" followed by word characters
- When such a pattern is detected, the notification panel shows an additional button

### User Interface
- **New Button**: When a "+text" pattern is detected, a "Global: +text" button appears in the notification panel
- **Tooltip**: The button shows helpful text: "Send '+text' to global chat instead of whispering"
- **Position**: The global chat button appears first (leftmost) in the button row for prominence

### Behavior
- **Global Chat**: Clicking the button sends the "+text" to global chat, not as a private message
- **Hotkey Support**: The feature is fully integrated with the hotkey system (HotKeyType.N_GLOBAL_CHAT_RESPONSE)
- **Configuration**: The feature can be enabled/disabled through the settings panel

## Configuration

### Settings Panel
Navigate to: **Settings > Notification > Chat Scanner Notification**

Available options:
- **Enable +text detection**: Toggle to enable/disable the feature
- **Default +text response**: Fallback response text (default: "I'll join")

### Example Usage
1. Chat message: "free uber elder +elder need 2 more"
2. MercuryTrade detects "+elder" pattern
3. Notification shows "Global: +elder" button
4. Clicking sends "+elder" to global chat
5. Other players see your interest in joining

## Technical Implementation

### Files Modified
1. **PlainMessageDescriptor.java**: Added `plusText` and `hasPlusText` fields
2. **ScannerDescriptor.java**: Added configuration options for the feature
3. **ChatScannerFrame.java**: Added regex pattern detection for "+text"
4. **ScannerNotificationPanel.java**: Added conditional button display
5. **NotificationScannerController.java**: Added global chat response method
6. **HotKeyType.java**: Added new hotkey type for the feature
7. **TranslationKey.java**: Added localization strings
8. **HotKeyConfigurationService.java**: Added hotkey configuration support

### Pattern Detection
```java
Pattern plusTextPattern = Pattern.compile("\\+\\w+");
Matcher plusTextMatcher = plusTextPattern.matcher(originalMessage);
if (plusTextMatcher.find()) {
    descriptor.setHasPlusText(true);
    descriptor.setPlusText(plusTextMatcher.group());
}
```

### Global Chat Response
```java
@Override
public void performGlobalChatResponse(String plusText) {
    // Send the "+text" to global chat (not as a whisper)
    // Need to prefix with "#" for global chat
    MercuryStoreCore.chatCommandSubject.onNext("#" + plusText);
}
```

## Benefits
- **Modern Syntax Support**: Keeps up with current Path of Exile community trends
- **Efficient Communication**: Reduces need for private messages for public activities
- **User-Friendly**: Intuitive button with clear labeling and tooltips
- **Configurable**: Can be enabled/disabled based on user preference
- **Hotkey Integration**: Fully supports keyboard shortcuts for power users

## Backward Compatibility
- The feature is fully backward compatible
- Existing whisper/private message functionality remains unchanged
- Traditional response buttons continue to work as before
- Feature can be disabled if not needed