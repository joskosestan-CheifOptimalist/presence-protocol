# QA Status

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

## Phase 1 — BLE Discovery (Scan + Advertise + UI peer counter)
- Date: 2026-02-24 (Auckland)
- Devices:
  - Samsung S23 (R5CR700RAQF) — PASS
  - Samsung A17 (R5GYC0FZ6RY) — PASS
- Human test:
  - Walked ~10m away: peers dropped to 0.
  - Returned to range: peers returned to 1.
- Log evidence:
  - docs/qa_logs/phase1_s23.log
  - docs/qa_logs/phase1_a17.log
