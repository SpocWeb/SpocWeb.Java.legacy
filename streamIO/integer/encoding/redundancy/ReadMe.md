---
digest:
  local-classes:
    ConvolutionBitEncode:
      mtime: '2026-09-05T21:40:10Z'
      digest: d5f03a6f69ffaa06b9f40867aa52e118c764369372e4a9cd6dd90c80f3dd6989
    Depeater:
      mtime: '2026-09-05T21:41:27Z'
      digest: 86123cdd23dbadcd9ef9917231a7d1deb2e27591675cb6246ac493988a3393b6
    Repeater:
      mtime: '2026-09-05T21:40:37Z'
      digest: f49209d5d63e91347d58ba8738b3604921d40b36e2fa1b9bc32cd7a48538d130
  folders: {}
tags:
- code/error_correction
- code/convolutional_encoding
concepts:
- Forward Error Correction Codecs - Repetition and Convolutional Encoding
facets:
  layer: utility
  status: legacy
  complexity: medium
description: 'Forward-error-correction codecs that trade bandwidth for resilience against transmission errors. `Repeater`/`Depeater` are a simple pair that duplicates each group of bytes an odd number of times and recovers the original via majority vote, tolerant of bit errors but not of byte insertion/deletion. `ConvolutionBitEncode` is a self-contained rate-1/2 convolutional encoder with its own bit-error-rate simulation harness over a simulated AWGN channel at several constraint lengths and Es/No ratios; it has no corresponding decoder in this folder. `ConvolutionBitEncode`''s polynomial-table indexing was wrong, and `Depeater.flush()` never flushed downstream; both were fixed in the 2026-09-06 bug-fix run. Note that `Depeater`''s shortened final length is the protocol''s tail signal, not a defect.'
---

# redundancy

Forward-error-correction codecs that trade bandwidth for resilience against transmission
errors. `Repeater`/`Depeater` are a simple pair that duplicates each group of bytes an odd
number of times and recovers the original via majority vote, tolerant of bit errors but not of
byte insertion/deletion. `ConvolutionBitEncode` is a self-contained rate-1/2 convolutional
encoder with its own bit-error-rate simulation harness over a simulated AWGN channel at
several constraint lengths and Es/No ratios; it has no corresponding decoder in this folder.
`ConvolutionBitEncode`'s polynomial-table indexing was wrong, and `Depeater.flush()` never
flushed downstream; both were fixed in the 2026-09-06 bug-fix run. Note that `Depeater`'s shortened final
length is the protocol's tail signal, not a defect.

## Classes

| Class | Responsibility |
|---|---|
| [ConvolutionBitEncode](ConvolutionBitEncode.java) | A rate-1/2 convolutional encoder and its simulation harness, testing bit-error rates over a simulated<br/>additive-white-Gaussian-noise (AWGN) channel at several constraint lengths and Es/No ratios. |
| [Depeater](Depeater.java) | Undoes the Operations of the Repeater Class and uses the Redundancy in the Stream to eliminate Transmission Errors. |
| [Repeater](Repeater.java) | Adds Redundancy to a Stream of Bytes by repeating a Group an uneven Time (typ. |
