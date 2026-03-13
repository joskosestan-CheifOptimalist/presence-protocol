# Current Milestone – MVP A / Phase 1

- **Date:** 2026-02-24
- **Scope:** BLE discovery (scan + advertise + UI counters)
- **Status:** Implementation complete, awaiting Josko-run validation on S23 + A17.
- **Artifacts:**
  - `data-ble/PresenceDiscoveryController.kt`
  - `domain/discovery/PeerDiscoveryModels.kt`
  - `app/ui` updates – runtime permission flow + live counters.
  - `docs/QA_STATUS.md` – tracking required human validation results.

## Stage 1 QA Prerequisites

These are preconditions for a valid test result, not troubleshooting steps.

**Samsung battery optimization** (both S23 and A17):
Settings → Apps → Presence Protocol → Battery → **Unrestricted**

Without this, One UI will restrict the foreground service and BLE scan loop,
producing false negatives indistinguishable from real regressions.

Full checklist: `docs/QA_STATUS.md` and `mobile/docs/QA_STATUS.md`.

Next milestone (pending approval): Phase 2 – BLE GATT transport + handshake framing.
