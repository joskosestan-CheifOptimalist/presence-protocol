# Presence Protocol — MVP Protocol (v0.1)

This document defines the MVP protocol flow: mobile witnessing, receipt generation, backend batching, and Cardano anchoring.

**MVP goal:** Two devices can witness each other, produce verifiable CBOR receipts, and have those receipts included in a batch commitment anchored on Cardano.

---

## 1. Components

### 1.1 Mobile app
- Generates Ed25519 keypair (subject/witness identity)
- Runs BLE scan/advertise loop
- Performs handshake with nearby devices
- Collects witness attestations
- Emits a CBOR receipt once per epoch/session

### 1.2 Backend service (MVP)
- Receipt ingestion API
- Validation (schema, signatures, dedupe)
- Batching (e.g., hourly/daily)
- Merkle tree builder + inclusion proof service
- Anchoring job that posts batch root to Cardano

### 1.3 On-chain anchor (Cardano)
- Minimal transaction that commits:
  - `batch_root` (Merkle root)
  - optional metadata: batch time window, count
- No on-chain rewards or slashing in MVP

---

## 2. Witnessing handshake (BLE)

### 2.1 Design constraints
- Must not reveal human identity
- Must avoid stable identifiers across epochs
- Must be robust to intermittent connectivity

### 2.2 Session primitives
Each device maintains:
- Long-term Ed25519 keypair: `(pk, sk)`
- Per-epoch nonce: `subject_nonce` / `witness_nonce`

### 2.3 Handshake steps (simple MVP)
1. **Discovery**
   - Devices advertise a service UUID for Presence Protocol.
2. **Hello exchange**
   - Each device sends: `pk`, `nonce`, `epoch_id`, optional `loc_bucket`
3. **Mutual signing**
   - Each device constructs `witness_msg` for the other device (per Receipt Format spec) and signs it.
4. **Ack**
   - Devices exchange signatures and confirm receipt.

After completion, each device has:
- the peer public key
- the peer nonce
- the peer signature over the witness message

The device then stores this as a witness attestation entry.

---

## 3. Receipt creation (per epoch/session)

At the end of an epoch/session window:
1. Device builds `receipt_body` (see Receipt Format spec)
2. Device computes `receipt_hash = blake2b_256(CBOR(receipt_body))`
3. Device signs: `subject_sig = Ed25519.sign(subject_sk, receipt_hash)`
4. Device outputs final receipt CBOR bytes

---

## 4. Backend ingestion and validation (MVP)

### 4.1 Ingestion endpoint
- `POST /v1/receipts`
- Payload: raw CBOR bytes (content-type: application/cbor)

### 4.2 Validation rules
Backend MUST:
- decode CBOR
- check schema keys and types
- validate `subject_sig` over `receipt_hash`
- validate each `witness_sig` over the derived `witness_msg`
- enforce a max witness count (e.g., N=3 in MVP)
- dedupe by `receipt_hash` (idempotency)

Backend SHOULD:
- rate limit by IP and/or device public key
- detect repeated identical witness pairs (basic heuristic)

### 4.3 Storage
Store:
- `receipt_hash` (primary key)
- receipt CBOR bytes (or receipt_body only)
- timestamps and batch assignment

---

## 5. Batching + Merkle commitment

### 5.1 Batch definition
A batch is a set of receipts over a time window:
- e.g., hourly batches in pilot
- daily batches in production

### 5.2 Merkle tree
- Leaves: `receipt_hash` (32 bytes)
- Hash function: `blake2b_256(left || right)`
- Root: `batch_root`

Backend produces:
- `batch_root`
- inclusion proof path for any `receipt_hash`

### 5.3 Verification
Given:
- receipt CBOR
- inclusion proof
- batch_root
- on-chain anchor reference

A verifier can confirm:
- receipt signatures are valid
- receipt_hash is included in batch_root
- batch_root exists on Cardano

---

## 6. On-chain anchoring (MVP)

### 6.1 Anchor payload
At minimum, commit:
- `batch_root` (32 bytes)

Optionally include:
- `batch_start_ts`, `batch_end_ts`
- `receipt_count`
- `batch_version`

### 6.2 Transaction format
MVP options (choose one):
- **Tx metadata** (fast to implement)
- **Simple script address datum** (more structured)
- **Minting policy** (later; not needed for MVP)

**Recommendation for MVP:** Tx metadata for speed.

---

## 7. Privacy posture (MVP)

In v0.1:
- No names
- No precise GPS coordinates stored
- Optional `loc_bucket` is coarse
- Nonces rotate per epoch to reduce linkability

Telemetry and logs must be designed to avoid linkability (avoid storing stable identifiers in analytics).

---

## 8. Attack considerations (MVP)

MVP mitigations:
- witness count capped
- dedupe receipts
- rate limiting
- detect repeated pairings (low-effort farm detection)

Later versions can add:
- stronger graph analysis
- ZK proofs
- stake-based bonding / slashing

---

## 9. Deliverables for v0.1
- Receipt Format spec + test vectors
- Mobile witnessing demo (2 devices)
- Backend ingestion + validation + batching
- Cardano batch root anchoring
- Verifier SDK (TypeScript) that checks signatures and inclusion proofs
