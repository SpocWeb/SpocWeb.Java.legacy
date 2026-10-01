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
- Error Correction
- Convolutional Encoding
facets:
  layer: utility
  status: legacy
  complexity: '3'
description: Forward-error-correction codecs that trade bandwidth for resilience against transmission errors. `Repeater`/`Depeater` are a simple pair that duplicates each group of bytes an odd number of times and recovers the original via majority vote, tolerant of bit errors but not of byte insertion/deletion. `ConvolutionBitEncode` is a self-contained rate-1/2 convolutional encoder with its own bit-error-rate simulation harness over a simulated AWGN channel at several constraint lengths and Es/No ratios; it has no corresponding decoder in this folder. `ConvolutionBitEncode`'s polynomial-table indexing was wrong, and `Depeater.flush()` never flushed downstream; both were fixed in the 2026-09-06 bug-fix run. Note that `Depeater`'s shortened final length is the protocol's tail signal, not a defect.
dv_has_:
  sub_:
    folders: 0
    files: 6
    units: 3
    facet_:
      layer_:
        utility: 3
      status_:
        legacy: 3
      complexity_:
        "3": 3
    tag_:
      code_:
        convolutional_encoding: 3
        error_correction: 3
    concept_:
      "Technology\\IT\\Data\\Data_Transmission\\Forward_Error_Correction.md": 3
      convolutional_encoding: 3
has_sub_folders: 0
has_sub_files: 6
has_sub_units: 3
has_sub_facet_layer_utility: 3
has_sub_facet_status_legacy: 3
has_sub_facet_complexity_3: 3
has_sub_tag_code_convolutional_encoding: 3
has_sub_tag_code_error_correction: 3
has_sub_concept_technology_it_data_data_transmission_forward_error_correction_md: 3
has_sub_concept_convolutional_encoding: 3
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
