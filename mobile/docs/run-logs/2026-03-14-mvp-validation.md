# Presence Protocol MVP Validation — 2026-03-14

## Build under test
- device A: Samsung S23
- role A: server
- device B: Samsung A17
- role B: client
- operator: Josko

## Test results

### 1. Fresh launch
- [x] Device A launched
- [x] Device B launched
- Notes: Both apps launched successfully from the baseline install/launch pack. Dashboard visible on both devices.

### 2. Permissions
- [x] Device A permissions granted
- [x] Device B permissions granted
- Notes: No permission issue blocked testing. Both apps remained operational during the test pass.

### 3. Initial dashboard state
- [x] Device A shows Peers Nearby
- [x] Device B shows Peers Nearby
- [x] Device A shows sensible initial value
- [x] Device B shows sensible initial value
- Notes: Hero metric rendered correctly on both devices. UI fix for Peers Nearby visually confirmed on-device.

### 4. Discovery and live metrics
- [x] Device B discovers Device A
- [x] Peers Nearby updates on Device B
- [ ] Peers Seen (10m) updates on Device B
- [x] Device A remains stable as server
- Notes: Physical test confirmed A17 reflected S23 presence correctly. With only one nearby peer, nearby should stabilize at 1 rather than keep incrementing. When the S23 app was fully closed, A17 returned to 0, confirming correct live nearby behavior.

### 5. Encounter protocol
- [ ] Discovery confirmed
- [ ] GATT connected
- [ ] HELLO/REPLY completed
- [ ] Ticket generated
- [ ] Ledger credit observed
- Notes:

### 6. Toggle test
- [ ] Toggle OFF works on Device B
- [ ] Toggle ON works on Device B
- [ ] Rediscovery works after toggle
- Notes: Disabling mining did not have the expected immediate effect during backgrounded runtime. Presence disappeared only after fully closing the S23 app. This suggests a lifecycle / stop-behavior follow-up item rather than a nearby-metric bug.

### 7. Background / foreground test
- [ ] Device A survives background/foreground
- [ ] Device B survives background/foreground
- Notes:

### 8. Stability result
- [x] No crash observed
- [x] No frozen UI observed
- [x] No obvious stale counter bug observed
- Notes: Current pass indicates stable UI behavior and physically correct nearby detection for the tested two-device scenario.

## Final result
- PASS / FAIL: PASS (partial, based on physical validation completed so far)
- Summary: Core two-device physical validation succeeded for dashboard rendering and nearby peer detection. A17 correctly reflected S23 presence and returned to 0 when the S23 app was fully closed.
- Follow-up issues: Confirm handshake/log sequence again in this exact role setup and investigate why disabling mining does not immediately remove presence when app runtime continues in background.
