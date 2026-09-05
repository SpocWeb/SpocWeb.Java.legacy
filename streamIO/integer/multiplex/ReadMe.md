---
digest:
  local-classes:
    DeMultiplexerIn_Raid0:
      mtime: '2026-09-05T21:52:40Z'
      digest: 86ac2e15d87bf374976a45a9d41786b72de0f6d716dc21ba0dda683399df1692
    DeMultiplexerIn_Raid5:
      mtime: '2026-09-05T21:53:07Z'
      digest: fcebd3fe24ca9c204ae024a0a78a52c6f0e3e39acef8be65f07c5951ee57eb0b
    Markov1:
      mtime: '2026-09-05T21:53:33Z'
      digest: dc0f7f65af6c1fb0055b6dfb41871301c7bf9ea59eb3ccaa12d6e57fa13d2628
    Markov1Hidden:
      mtime: '2026-09-05T21:53:53Z'
      digest: cffdb0b0da18295c7b5afe2bf06c00fb8aabec2c54c4bbb5b6710733fde4ed35
    MultiplexerOutRaid0:
      mtime: '2026-09-05T21:54:06Z'
      digest: 24fe512277858fc98c2346aad515ac4302a795ca051314ced738ff0c1fd54893
    MultiplexerOutRaid5:
      mtime: '2026-09-05T21:54:24Z'
      digest: 413b3363a9996da4cbb0ce3ee08bad2e53a80fcef4f6aa8a2d1de33b9b9aef7b
    Viterbi:
      mtime: '2026-09-05T21:53:53Z'
      digest: 2533f74f69a586faebdaf4a25aa59f92c49d9bb672cac1132ad0cb090aa31df5
  folders: {}
tags:
- code/multiplexer
- code/multiplexing
- code/raid_encoding
concepts:
- RAID-Style Stream Multiplexing plus Markov/Viterbi Math
facets:
  layer: domain
  status: legacy
  complexity: high
description: This folder combines several independent Byte streams into one logical stream (and back), in two unrelated senses. `MultiplexerOutRaid0`/`DeMultiplexerIn_Raid0` interleave/de-interleave a Set of Output/Input streams Round-Robin (RAID 0 striping, for throughput), and `MultiplexerOutRaid5`/ `DeMultiplexerIn_Raid5` extend that with an XOR Parity Stream (RAID 5) so a single failed source/sink stream can be reconstructed. Separately, `Markov1`/`Markov1Hidden`/`Viterbi` implement Markov Chain and Hidden-Markov-Model math (stationary distribution, and the Viterbi algorithm for finding the most probable hidden state sequence) - included here as the statistical basis for modeling and validating noisy/redundant transmission across multiplexed streams, rather than as stream implementations themselves.
---

# multiplex

This folder combines several independent Byte streams into one logical stream (and back), in two
unrelated senses. `MultiplexerOutRaid0`/`DeMultiplexerIn_Raid0` interleave/de-interleave a Set of
Output/Input streams Round-Robin (RAID 0 striping, for throughput), and `MultiplexerOutRaid5`/
`DeMultiplexerIn_Raid5` extend that with an XOR Parity Stream (RAID 5) so a single failed
source/sink stream can be reconstructed. Separately, `Markov1`/`Markov1Hidden`/`Viterbi` implement
Markov Chain and Hidden-Markov-Model math (stationary distribution, and the Viterbi algorithm for
finding the most probable hidden state sequence) - included here as the statistical basis for
modeling and validating noisy/redundant transmission across multiplexed streams, rather than as
stream implementations themselves.

## Classes

| Class | Responsibility |
|---|---|
| [DeMultiplexerIn_Raid0](DeMultiplexerIn_Raid0.java) | The DeMultiplexerInRaid0 is derived from the abstract Base Class AStreamIn_Byte and de-multiplexes this Input<br/>stream from a List of Input Streams in a Round Robin Fashion. |
| [DeMultiplexerIn_Raid5](DeMultiplexerIn_Raid5.java) | The DeMultiplexerInRaid0 is derived from the abstract Base Class AStreamIn_Byte and de-multiplexes this Input<br/>stream from a List of Input Streams in a Round Robin Fashion. |
| [Markov1](Markov1.java) | Title: Description: Purpose: Validating the Transition Model of a Markov Series Order 1. The Values of Markov<br/>Time Series with Order N are random with a Probability that depends only on the last N Values. |
| [Markov1Hidden](Markov1Hidden.java) | Title: Description: Purpose: Defines the Hidden Markov Model (HMM) of Order 1. Hidden, because the Sequence of<br/>Outcomes cannot be determined directly. |
| [MultiplexerOutRaid0](MultiplexerOutRaid0.java) | The MultiplexerOutRaid0 is derived from the abstract Base Class AStreamOutByte and multiplexes this<br/>StreamOutByte to a List of Output StreamOutByte in a Round Robin Fashion (RAID0, Striping), which can be used<br/>to speed up Processing in parallel Processors. |
| [MultiplexerOutRaid5](MultiplexerOutRaid5.java) | Multiplexes this StreamOutByte to a List of Output Streams in RAID 5 Fashion, writing each Block and an XOR<br/>Parity Block to allow reconstructing a failed Stream. |
| [Viterbi](Markov1Hidden.java) | Title: Description: Purpose: Helper Value Class to hold intermediaries & return the Result from the Viterbi<br/>Calculation Design Decisions / Implementation Details: Known SubClasses: Known Uses: Copyright: Copyright (c)<br/>Matthias Heuer Company: personal Created on 10-26-2002, 12:47 PM |
