# Identity Design Standard — Presence Protocol

## Intent

The Presence Protocol is designed with the eventual adoption of
decentralized identity (DID) by humanity as a core architectural goal.
All identity-handling code must be written with this migration path in mind.

## Current State

Peer identity is currently represented by an ephemeral `appInstanceId`
(a UUID generated at install time). This is a **placeholder identity layer**,
not a permanent solution.

The MAC address → appInstanceId mapping exists solely to survive BLE
MAC rotation. It is not an identity system.

## Target State

Each participant's identity will resolve to a **Cardano Veridian DID**
(`did:prism:...`) — a W3C-compliant decentralized identifier anchored
on Cardano via the Veridian (formerly PRISM) infrastructure managed by
the Cardano Foundation.

Reference: https://veridian.identity.foundation

## Coding Standards

Any code that touches identity MUST answer the following question:

> "How does this relate to the Cardano Veridian DID layer?"

### Rules

1. `appInstanceId` is an **ephemeral local identifier** — never treat it
   as a permanent identity.

2. Encounter tickets, ledger credits, and reward attribution records MUST
   include a field that can accept a DID URI when the identity layer matures.
   Example field: `did: String? = null`

3. The identity resolution chain is:
```
   BLE MAC address
       ↓
   ephemeral appInstanceId  (current ceiling)
       ↓
   Veridian DID (did:prism:...)  (target)
```

4. No identity should be considered final or permanent until it is
   DID-anchored on Cardano.

5. The `rewardPeerId` field in the encounter ticket is the primary
   candidate for DID migration.

## Migration Path

When the wallet/identity layer matures:

- `rewardPeerId` → replaced by `did:prism:...`
- Encounter tickets become DID-linked attestations
- Mining rewards become attributable to a sovereign identity
- Compatible with Midnight ZK privacy layer — DID revealed only
  with user consent

## Related Components

| Component | Identity Field | Migration Target |
|---|---|---|
| EncounterTicket | rewardPeerId | did:prism:... |
| MiningLedger | peer identity key | did:prism:... |
| PresenceHandshakeCoordinator | macToAppId | appId → DID resolver |
| Relay Node (future) | witness identity | did:prism:... |

---
*This is a living standard. Update as the Veridian integration matures.*
*Presence Protocol — Identity Standard v1.0 — 2026*
