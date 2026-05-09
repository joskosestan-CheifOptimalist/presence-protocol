# CPOP Naming Migration

## Summary
Updated Presence Protocol visible token identity from POP to CPOP.

## Positioning
CPOP = Cardano Proof of Presence.

## Scope
- UI-visible token symbol migrated to CPOP
- Ledger/default tokenSymbol values migrated to CPOP
- No BLE logic changed
- No crypto/protocol rules changed
- No reward math changed

## Build
./gradlew :app:assembleBothDebug
