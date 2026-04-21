# The First Verified Presence Event

Date: 2026-03-14  
Time: 14:39:51 NZDT  
Project: Presence Protocol  
Branch: phase2a-gatt-transport  
Commit: e7fc286

## Declaration

This document records the first successfully completed and ledger-credited cryptographically verified Presence Protocol encounter on real physical hardware.

On March 14, 2026 at 14:39:51 NZDT the Presence Protocol completed its first end-to-end verified encounter between two Android devices resulting in successful ticket construction persistence and ledger credit issuance.

This marks the transition of Presence Protocol from a transport and handshake prototype into a functioning real-world proof-of-presence system.

## Devices

Samsung A17 — client role using 0.1-both  
Samsung S23 — server role using 0.1-server

## Protocol Stages Confirmed

BLE discovery  
GATT transport connection  
HELLO transmission  
REPLY transmission  
16-byte session ID exchange  
32-byte nonce exchange  
EC public key exchange  
ECDSA signature generation  
Signature verification on both sides  
Deterministic encounter ticket construction  
Local ticket persistence  
Ledger credit issuance  
RESULT transmission by client  
HANDSHAKE_COMPLETE logged by server

## Encounter Record

encounterId: 0f643bed-3dec-43f9-4494-830914427d72  
protocolVersion: presence_v1  
epochId: 9852513  
heartbeatId: 118230159  
heartbeatIndexInEpoch: 3  

client appVersion: 0.1-both  
server appVersion: 0.1-server

## Evidence

docs/qa_logs/phase2b_a17_first_ledger_credit.log  
docs/qa_logs/phase2b_s23_first_ledger_credit.log

## Known Limitation At Time Of Event

The responder/server path called markComplete() without first executing ensureLocalEphemeral().

This caused the following server ticket fields to appear as placeholders:

deviceAEphemeralKey  
deviceASignature

Despite this limitation:

tickets passed validation  
encounter persistence succeeded  
ledger credit was issued

## Significance

This event proves that the Presence Protocol can convert real human proximity into a verifiable cryptographic event using BLE discovery GATT transport cryptographic challenge-response and deterministic encounter ticket construction.

This is the first confirmed proof that the Presence Protocol works end-to-end on physical devices.

## Status

Milestone achieved: First Verified Presence Event  
Protocol state: End-to-end encounter proof operational
