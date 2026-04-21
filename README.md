# Presence Protocol

A decentralized proof-of-presence system that transforms real-world human encounters into cryptographically verifiable events.

## Status — Alpha 0.2 (Hardware Validated)

Presence Protocol is no longer conceptual. It has been validated through real-device interaction cycles using BLE and GATT transport.

### Implemented

- BLE peer discovery (Android)
- GATT-based HELLO → REPLY → RESULT handshake
- CBOR-encoded message transport
- Ephemeral key generation and signing
- EncounterTicket creation with dual signatures
- Ledger credit issuance on successful handshake completion
- Background mining service with wake lock support
- Peer deduplication resilient to MAC rotation

### Verified Milestones

- `v0.1-first-presence` — First real-world encounter recorded
- `v0.1-result-confirmed` — Full handshake validation achieved
- `v0.1-soak-pass` — Multi-cycle device stability confirmed
- `v0.2-service-pass` — Background mining operational
- `v0.2-peer-dedup-pass` — Stable identity across rotating MAC addresses

## Architecture

Mobile Device  
→ BLE Discovery  
→ GATT Handshake (HELLO / REPLY / RESULT)  
→ Encounter Validation  
→ Ticket Generation  
→ Ledger Credit  

Future:
→ Cardano anchoring  
→ Midnight privacy layer  

## Evidence

See:

`docs/qa_logs/RUN_0XX/`

Each run contains:
- device interaction logs (sanitized)
- handshake validation traces
- ticket outputs
- closure summaries

## Project Structure

- `mobile/` — Android implementation
- `mobile/core-common/` — protocol definitions
- `mobile/core-crypto/` — key generation and signing
- `mobile/data-ble/` — BLE + GATT transport
- `mobile/domain/` — encounter + ledger logic
- `docs/` — architecture, QA evidence, milestones

## Current Focus

- Protocol stabilization
- Relay layer integration
- Privacy layer (Midnight)
- Cardano settlement alignment

## Position

Presence Protocol explores a new primitive:

**Proof of Presence**

A system where real-world interaction becomes verifiable, portable, and trustless.
