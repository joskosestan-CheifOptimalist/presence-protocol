# QA Status – Presence Protocol Android MVP

---

## Environment Validation — Samsung Test Devices

**This section is part of the test harness, not optional troubleshooting.**

Samsung One UI aggressively restricts background services even when a foreground service is correctly declared. Without the setting below, One UI may kill BLE scan loops and foreground services, producing false negatives that look like code regressions.

### Samsung Battery Optimization Prerequisite

Apply to **both** test devices before any foreground-service or background BLE test:

1. Open **Settings**
2. Go to **Apps**
3. Select **Presence Protocol**
4. Open **Battery**
5. Set to **Unrestricted**

> **Classification: Environment validation.**
> A failed background test where this setting was not confirmed is an invalid test result. Do not draw conclusions about service stability, BLE reliability, or patch regressions until this prerequisite is verified.

---

## Standard QA Run Sheet

Required before any 30-minute or background-presence test:

- [ ] Samsung battery mode set to **Unrestricted** on both devices (see above)
- [ ] Foreground service notification is visible in the status bar
- [ ] App build is current — confirm APK matches the target commit
- [ ] Bluetooth is enabled on both devices
- [ ] All required BLE permissions are granted (verify in Settings → Apps → Permissions)
- [ ] Screen-off / background test **start time is logged**
- [ ] Results checked at fixed intervals: **5 min**, **15 min**, **30 min**

---

## Phase Results

| Phase | Date | Device | Android Version | Outcome | Notes |
| --- | --- | --- | --- | --- | --- |
| Phase 1 – BLE Discovery | 2026-02-24 | Samsung S23 | 14 (One UI 6) | PENDING | Awaiting Josko physical validation per script below. |
| Phase 1 – BLE Discovery | 2026-02-24 | Samsung A17 | 14 (One UI 6) | PENDING | Awaiting Josko physical validation per script below. |

## Pending Test Script Reference
See "READY FOR JOSKO TEST" section in the latest handoff message for detailed steps, expected UI state, and log tags.
