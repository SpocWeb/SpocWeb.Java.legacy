---
digest:
  local-classes:
    WaveReader:
      mtime: '2026-09-05T11:11:09Z'
      digest: 6356a8972f22a64c6b4e3863dcfbee58e39a9e2af31725b6b6234756587968d6
  folders: {}
tags:
- code/audio
concepts:
- Java Sound API Demos
facets:
  layer: infrastructure
  status: legacy
  complexity: low
description: 'A single demonstration of the Java Sound API: reading a WAV file''s format and stream, and writing back a synthesized sine-wave WAV file of raw PCM samples.'
---

# sound

A single demonstration of the Java Sound API: reading a WAV file's format and stream, and
writing back a synthesized sine-wave WAV file of raw PCM samples.

## Classes

| Class | Responsibility |
|---|---|
| [WaveReader](WaveReader.java) | Demonstrates reading a WAV file with the Java Sound API and re-synthesizing a sine-wave WAV file of raw PCM<br/>samples produced through IIntFunction#Map(int). |

## Entry Points

| Class.Method | Description |
|---|---|
| [WaveReader.main(String[])](WaveReader.java#L48) | Reads the sample WAV file and writes a re-synthesized sine-wave copy alongside it. |
