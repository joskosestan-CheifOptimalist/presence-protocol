# Presence Protocol — Demo-Safe Build Checklist

## Target build
- Build variant: bothRelease
- Upload artifact: app/build/outputs/bundle/bothRelease/app-both-release.aab
- Distribution: Google Play Internal Testing

## Demo-safe requirements

### App launch
- App opens cleanly from Play install
- No crash on first launch
- Dashboard visible within 3 seconds

### Permissions
- Bluetooth permission request is understandable
- Nearby devices permission is requested where required
- Location permission is requested only if required by Android BLE behavior
- Denying permission does not crash the app

### UI
- App name visible: Presence Protocol
- Main action visible: Start Mining / Stop Mining
- Peers Nearby visible
- Mining / ledger counters visible
- Developer/debug text not overwhelming the main demo

### Demo flow
- Phone A and Phone B both launch from Play-installed build
- Start Mining works on both
- Peers Nearby updates when phones are close
- App remains stable for 5 minutes
- Stop Mining works without crash

### Do not demo if
- First launch crashes
- Permission denial crashes
- Peers Nearby never updates
- UI shows confusing placeholder/internal-only labels
