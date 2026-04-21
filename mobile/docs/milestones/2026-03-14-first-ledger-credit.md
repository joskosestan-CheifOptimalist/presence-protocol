# Milestone: First Valid Ledger Credit
**Date:** 2026-03-14
**Branch:** phase2a-gatt-transport
**Commit:** e7fc286

## What happened
The first cryptographically verified encounter was produced and credited by the protocol.

At 14:39:51 NZDT, A17 (client, BOTH build) completed a full handshake with S23 (server build):
- HELLO sent with real 16-byte session ID, 32-byte nonce, and EC public key
- REPLY received with real EC public key and ECDSA signature
- Both deviceASignatureValid=true and deviceBSignatureValid=true
- Encounter ticket generated, persisted, and ledger credit issued
- RESULT sent by client, HANDSHAKE_COMPLETE logged by server

## Encounter record
- encounterId: 0f643bed-3dec-43f9-4494-830914427d72
- protocolVersion: presence_v1
- epochId: 9852513
- heartbeatId: 118230159
- heartbeatIndexInEpoch: 3
- appVersion (client): 0.1-both
- appVersion (server): 0.1-server

## Evidence
- docs/qa_logs/phase2b_a17_first_ledger_credit.log
- docs/qa_logs/phase2b_s23_first_ledger_credit.log

## Known issue recorded at this milestone
Server responder path calls markComplete directly without ensureLocalEphemeral().
Result: deviceAEphemeralKey and deviceASignature show as placeholder strings in
server-side tickets. Tickets still pass isValid() and are persisted correctly.
Fix required before tickets are submitted to any backend or relay.

## What this means
The protocol crossed from transport prototype into verified encounter proof.
BLE detection, GATT transport, CBOR framing, ECDSA signing, transcript
alignment, ticket construction, persistence, and ledger credit are all
functioning end-to-end on real hardware.

## Next milestone targets
- Fix server ephemeral key missing on responder path
- Build foreground service for background operation
- Wire encounter state machine to handshake completion
