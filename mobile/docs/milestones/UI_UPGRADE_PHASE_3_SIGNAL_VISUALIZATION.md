# UI Upgrade Phase 3 — Reactive Signal Visualization

## Summary
Enhanced the PresencePulseHero so the circular field visual reacts to real nearby peer state.

## Verified
- Build succeeds
- Calm field state when no peers nearby
- Stronger ring / outer ripple when peersNearby > 0
- Status text changes to “Presence detected” when peers are nearby
- No BLE, ledger, or protocol logic changed

## Build
./gradlew :app:assembleBothDebug
