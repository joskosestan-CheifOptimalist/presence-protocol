# Presence Protocol — Receipt Format (v0.1)

This document defines the canonical "Presence Receipt" format used by the mobile app and backend.

**Design goals**
- Cardano-native encoding: **CBOR**
- Deterministic serialization for hashing and Merkle inclusion
- Simple, verifiable signatures: **Ed25519**
- Privacy-first: no personally identifying info; no precise location; optional coarse bucket

---

## 1. Terminology

- **Receipt**: A record that a device participated in an epoch/session and obtained one or more witness attestations.
- **Subject**: The device producing the receipt.
- **Witness**: Another nearby device that signs an attestation about the subject during a handshake.
- **Epoch**: A time window (e.g., 10 minutes) used to batch receipts.

---

## 2. Encoding

All receipts are encoded as **CBOR** using a canonical/deterministic representation:
- keys use fixed integer labels (not strings)
- maps must be deterministic (canonical CBOR) to ensure stable hashing

**Receipt binary = CBOR(map)**

### CBOR canonicalization requirements
Implementations MUST:
- use canonical CBOR encoding
- represent integers in the shortest form
- sort map keys by numeric order

---

## 3. Receipt schema (CBOR map)

The receipt is a CBOR map with the following integer keys:

| Key | Field | Type | Required | Description |
|---:|------|------|:--:|------------|
| 0 | `v` | uint | ✅ | Format version (v0.1 = 1) |
| 1 | `epoch_id` | uint | ✅ | Epoch identifier (monotonic; derived from time) |
| 2 | `ts_start` | uint | ✅ | Epoch start timestamp (unix seconds) |
| 3 | `ts_end` | uint | ✅ | Epoch end timestamp (unix seconds) |
| 4 | `subject_pk` | bstr(32) | ✅ | Subject Ed25519 public key |
| 5 | `subject_nonce` | bstr(16..32) | ✅ | Random nonce to prevent linking across epochs |
| 6 | `loc_bucket` | uint or null | ⛔️ | Optional coarse location bucket ID (e.g., ~5km grid) |
| 7 | `device_class` | uint | ⛔️ | Optional enum: 0=unknown,1=low,2=mid,3=high |
| 8 | `witnesses` | array | ✅ | Array of witness attestations (0..N, N capped by protocol rules) |
| 9 | `quality` | map | ✅ | Quality score breakdown (see section 5) |
| 10 | `subject_sig` | bstr(64) | ✅ | Subject signature over the receipt body (see section 4) |

### Witness attestation object
Each element of `witnesses` is a CBOR map:

| Key | Field | Type | Required | Description |
|---:|------|------|:--:|------------|
| 0 | `witness_pk` | bstr(32) | ✅ | Witness Ed25519 public key |
| 1 | `witness_nonce` | bstr(16..32) | ✅ | Witness nonce for unlinkability |
| 2 | `rssi_q` | int | ⛔️ | Optional quantized RSSI measure (privacy-preserving) |
| 3 | `witness_sig` | bstr(64) | ✅ | Witness signature over the witness message (see section 4) |

---

## 4. Signature rules (Ed25519)

To avoid circular signing, we define:
- a **receipt body** (everything except `subject_sig`)
- a **witness message** each witness signs

### 4.1 Receipt body (for hashing and subject signature)
Define `receipt_body` as the receipt map **excluding key 10 (`subject_sig`)**.

**Subject signs:**
- `subject_sig = Ed25519.sign(subject_sk, blake2b_256(CBOR(receipt_body)))`

Where:
- `CBOR(receipt_body)` is canonical CBOR encoding
- `blake2b_256` is a 32-byte BLAKE2b hash

### 4.2 Witness message (what a witness signs)
Each witness signs a message that binds them to the subject and epoch without revealing identity.

Define `witness_msg` as CBOR(map) with integer keys:

| Key | Field | Type | Required |
|---:|------|------|:--:|
| 0 | `v` | uint | ✅ |
| 1 | `epoch_id` | uint | ✅ |
| 2 | `subject_pk` | bstr(32) | ✅ |
| 3 | `subject_nonce` | bstr(16..32) | ✅ |
| 4 | `witness_pk` | bstr(32) | ✅ |
| 5 | `witness_nonce` | bstr(16..32) | ✅ |
| 6 | `loc_bucket` | uint or null | ⛔️ |
| 7 | `rssi_q` | int or null | ⛔️ |

**Witness signs:**
- `witness_sig = Ed25519.sign(witness_sk, blake2b_256(CBOR(witness_msg)))`

Verification requires:
- signature verifies under `witness_pk`
- witness fields match the attestation entry

---

## 5. Quality score map

`quality` is a CBOR map with integer keys:

| Key | Field | Type | Required | Description |
|---:|------|------|:--:|------------|
| 0 | `witness_count` | uint | ✅ | Number of valid witnesses included |
| 1 | `diversity_score` | int | ✅ | Optional scoring value (implementation-defined) |
| 2 | `graph_score` | int | ✅ | Optional score from backend heuristics (can be 0 in MVP) |
| 3 | `total_score` | int | ✅ | Total quality score for the epoch |

**MVP note:** In v0.1, `diversity_score` and `graph_score` may be 0. The key is consistent encoding.

---

## 6. Deterministic receipt hash

The canonical receipt identifier is:
- `receipt_hash = blake2b_256(CBOR(receipt_body))`

This is what is stored, batched into a Merkle tree, and anchored on-chain.

---

## 7. Versioning

- `v=1` corresponds to this v0.1 spec.
- Breaking changes increment `v`.
- Implementations MUST reject unknown `v` unless explicitly supported.

---

## 8. Implementation notes

Recommended libraries:
- CBOR: use a canonical CBOR encoder (deterministic map ordering)
- Ed25519: libsodium, tweetnacl, or platform-native equivalents
- Hash: BLAKE2b-256

Test vectors and example encodings should be added in `architecture/test-vectors/` in a future update.
