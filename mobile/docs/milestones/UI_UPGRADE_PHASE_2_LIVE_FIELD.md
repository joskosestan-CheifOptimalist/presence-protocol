# UI Upgrade Phase 2 — Live Field

## Summary
Replaced the old Discovery card with a dedicated PresenceFieldCard component.

## Verified
- App builds successfully
- FIELD ACTIVE banner remains visible
- Live Field card renders current peersNearby and peersSeenLast10Minutes state
- BLE/protocol logic unchanged

## Build
./gradlew :app:assembleBothDebug
