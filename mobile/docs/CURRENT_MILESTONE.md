# Current Milestone – MVP A / Phase 1

- **Date:** 2026-02-24
- **Scope:** BLE discovery (scan + advertise + UI counters)
- **Status:** Implementation complete, awaiting Josko-run validation on Samsung S23 + A17.
- **Artifacts:**
  - `data-ble/PresenceDiscoveryController.kt` – unified advertiser/scanner emitting Flow metrics.
  - `domain/discovery/PeerDiscoveryModels.kt` – canonical peer event + metrics models.
  - `app/ui` updates – runtime permission flow + live counters.
  - `docs/QA_STATUS.md` – tracking required human validation results.

## Stage 1 QA Prerequisites

Before running any background or 30-minute presence test, the following must be confirmed as part of environment validation. These are not troubleshooting steps — they are preconditions for a valid test result.

**Samsung battery optimization** (both test devices — S23 and A17):
Settings → Apps → Presence Protocol → Battery → **Unrestricted**

Without this, One UI will aggressively restrict the foreground service and BLE scan loop, producing false negatives that are indistinguishable from real regressions.

Full checklist is in `docs/QA_STATUS.md` and `mobile/docs/QA_STATUS.md`.

Next milestone (pending approval): Phase 2 – BLE GATT transport + handshake framing.
