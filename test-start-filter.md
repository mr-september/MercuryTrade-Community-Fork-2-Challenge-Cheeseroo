# Chat Scanner ^ Operator Test

## Feature Description
The `^` operator filters out messages that START with specific characters/strings.

## Test Cases

### Example 1: Filter messages starting with "+"
**Filter Configuration:** `wtb,^+`
- ✅ **PASS:** "wtb chaos orb" - contains "wtb", doesn't start with "+"
- ❌ **FAIL:** "+wtb chaos orb" - starts with "+", should be filtered out
- ✅ **PASS:** "three + five wtb" - contains "wtb", "+" is not at start

### Example 2: Filter messages starting with "!"
**Filter Configuration:** `uber,^!`
- ✅ **PASS:** "looking for uber elder" - contains "uber", doesn't start with "!"
- ❌ **FAIL:** "!price check uber" - starts with "!", should be filtered out
- ✅ **PASS:** "uber service! contact me" - contains "uber", "!" is not at start

### Example 3: Multiple start filters
**Filter Configuration:** `boss,^+,^-`
- ✅ **PASS:** "selling boss carries" - contains "boss", doesn't start with "+" or "-"
- ❌ **FAIL:** "+boss service" - starts with "+", should be filtered out
- ❌ **FAIL:** "-boss not available" - starts with "-", should be filtered out

## Implementation Details
- The `^` operator is processed before the `!` operator
- Multiple start filters can be combined
- All filtering is case-insensitive
- The operator works on the actual message content (after username)

## UI Documentation
The memo section now shows:
- `! - NOT (!wtb,!wts)`
- `^ - START (^+,^-)`
- `, - separator`
