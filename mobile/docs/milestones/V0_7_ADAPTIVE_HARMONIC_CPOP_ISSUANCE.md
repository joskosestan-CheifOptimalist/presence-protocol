# v0.7 Adaptive Harmonic CPOP Issuance

## Summary

Presence Protocol v0.7 introduces adaptive harmonic CPOP issuance as a local protocol accounting prototype.

## What Changed

- Added `CpopHarmonicIssuanceEngine`
- Moved tokenomics logic into the domain layer
- Added `MiningLedger.currentStats()`
- Wired harmonic reward calculation into the verified ledger credit path
- Replaced fixed default encounter credit with calculated adaptive reward
- Added `CPOP_HARMONIC_ISSUANCE` log evidence

## Formula

reward = baseReward × frequencyMultiplier × dampingMultiplier

Where:

baseReward = 1.0

frequencyMultiplier =
1.0 + min(encountersThisEpoch × 0.02, 0.25)

dampingMultiplier =
0.75 when verifiedToday > 50
otherwise 1.0

## Current Scope

This is local protocol accounting only.

It is NOT:
- on-chain minting
- financial issuance
- final tokenomics
- decentralized consensus
- monetary policy

## Purpose

The purpose of v0.7 is to prove that CPOP issuance can respond dynamically to verified presence frequency while remaining bounded by conservative damping rules.

## Verification

Build:

./gradlew :app:assembleBothDebug

Result:

BUILD SUCCESSFUL

## Evidence Log

Example telemetry:

CPOP_HARMONIC_ISSUANCE
peer=<peer>
reward=<reward>
base=<base>
frequencyMultiplier=<multiplier>
dampingMultiplier=<multiplier>

