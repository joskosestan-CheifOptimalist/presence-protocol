# Presence Protocol Milestone — Dashboard UI + Nearby Validation
Date: 2026-03-14

## Summary
This milestone confirms that the Presence Protocol mobile MVP now renders the `Peers Nearby` dashboard metric correctly on-device and that the nearby metric behaves physically correctly in a two-device test.

## Devices / roles
- Samsung S23 — server build
- Samsung A17 — client-side test using both-role build

## Completed
- Patched `PresencePulseHero()` to explicitly render `uiState.peersNearby`
- Built and installed latest APKs successfully
- Visually confirmed the dashboard UI fix on physical devices
- Confirmed A17 reflects S23 presence correctly
- Confirmed A17 returns to `0` when the S23 app is fully closed
- Confirmed no crash, no frozen UI, and no obvious stale counter bug during this validation pass

## Interpretation
The `Peers Nearby` value behaves as a live nearby occupancy signal, not as an endlessly incrementing counter. In the tested two-device setup, a stable value of `1` is the correct physical interpretation when one peer is nearby.

## Follow-up issue
Disabling mining did not immediately remove presence while runtime continued in the background. Presence disappeared only after fully closing the S23 app. This suggests a lifecycle / stop-behavior hardening task.

## Related record
- `docs/run-logs/2026-03-14-mvp-validation.md`
