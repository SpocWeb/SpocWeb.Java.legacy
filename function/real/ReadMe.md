---
digest:
  local-classes:
    Adder:
      mtime: '2026-09-05T16:43:31Z'
      digest: 98647c0e8206c5866d4100f72110ee720cd5292ef859722b3da842e3b0849096
    BiLinearSum:
      mtime: '2026-09-05T16:43:36Z'
      digest: 93e66d9670d4db071264cc9d5eebdb86c9f4082011c4a605b178c823ee4c4a70
    FourierCoefficient:
      mtime: '2026-09-05T16:43:23Z'
      digest: ec71e89091f505d1d17888ad90c675b18831d7a50b3675d95b7b500aba9d92fd
    LinearScale:
      mtime: '2026-09-05T16:43:41Z'
      digest: d30363cfc46960a78f8390cb55bec8c712158a4d72bf3f945802bef9125ec13d
    Maximum:
      mtime: '2026-09-05T16:43:46Z'
      digest: 56395d4af0466cdd9bc502b9c40ac1d1e9bbac22d3adf4aae0cbb64b0a3b3959
    Minimum:
      mtime: '2026-09-05T16:43:57Z'
      digest: 95a5d9cd8e516ffbe1ffbbbdbceb929abf05d525d3714c9659e559079c4113e8
    Multiplier:
      mtime: '2026-09-05T16:44:16Z'
      digest: 637f145847b8b2d5d7a6a6d6294a851ac0db242905c42052a93b6fffe05bff58
    Product:
      mtime: '2026-09-05T16:44:42Z'
      digest: 350e2b4852860aaf8ca83c78dc698282926d636ed6e2b44c8a0f7d5fcbd8dc69
    RunningMean:
      mtime: '2026-09-05T16:44:49Z'
      digest: f2083985eabb479c85dba28dab3b576f2a74eaccd146c195a39c4add6a1e7e25
    StatefulFloatFunction:
      mtime: '2026-09-05T16:44:56Z'
      digest: 9e3ae7dd07b0a37ae58f7b8d7c960b001a59d2f64b7c409631529971916db7d4
    Sum:
      mtime: '2026-09-05T16:45:02Z'
      digest: 3c40991ca51fad72aa35990262dde12bf9d39385f9c387132fd40b43a5a89de6
    SumSquares:
      mtime: '2026-09-05T16:45:08Z'
      digest: b465a82fa608c014ea00ef3eee899cb71d51dd004ef411a5884df92b1d334c74
  folders: {}
tags:
- code/running_aggregates
concepts:
- Streaming Numeric Aggregators
facets:
  layer: utility
  status: broken
  complexity: medium
description: 'Stateful streaming aggregators over a sequence of double values passed one at a time through `Map`/`process`: `Adder`/`Multiplier`/`Product`/`Sum`/`SumSquares` accumulate a running total, `Maximum`/`Minimum` track extrema, `RunningMean` and `Product.getHMV()` compute running statistics, and `LinearScale`/`BiLinearSum`/`FourierCoefficient` combine several inputs into a scaled or weighted result.'
---

# real

Stateful streaming aggregators over a sequence of double values passed one at a time through `Map`/`process`: `Adder`/`Multiplier`/`Product`/`Sum`/`SumSquares` accumulate a running total, `Maximum`/`Minimum` track extrema, `RunningMean` and `Product.getHMV()` compute running statistics, and `LinearScale`/`BiLinearSum`/`FourierCoefficient` combine several inputs into a scaled or weighted result.

## Classes

| Class | Responsibility |
|---|---|
| [Adder](Adder.java) | Title: Adder Description: AKA 'Offsetter' Offsets the Elements of the streamIO, but hands them on unchanged,<br/>so also other Operations can take place on them. |
| [BiLinearSum](BiLinearSum.java) | Title: enclosing_type Description: Purpose: This stateful Function combines the current Value in a weighted<br/>Manner with new incoming Values. |
| [FourierCoefficient](FourierCoefficient.java) | continuously calculates a single Fourier Component of the incoming Signal |
| [LinearScale](LinearScale.java) | Random Number Filter Generator with a white Noise Spectrum, i.e. the Power falls like f^-2 = 1/(f*f) A uniform<br/>Random Noise Generator also generates a uniform Power Spectrum (heuristic Reason: Spectrum is as Random as the<br/>Input!) Since P(0) = Infinity, the Signal Value could exceed any Bound. |
| [Maximum](Maximum.java) | Title: Maximum Description: Evaluates the Maximum of the Elements of the streamIO, but hands them on<br/>unchanged, so also other Operations can take place on them. |
| [Minimum](Minimum.java) | Title: Minimum Description: Evaluates the Minimum of the Elements of the streamIO, but hands them on<br/>unchanged, so also other Operations can take place on them. |
| [Multiplier](Multiplier.java) | Title: Multiplier Description: AKA 'Scaler' Filter for Number Streams. |
| [Product](Product.java) | Title: Product Description: Multiplies the Elements of the streamIO into a running Product, but hands them on<br/>unchanged, so also other Operations can take place on them. |
| [RunningMean](RunningMean.java) | Title: FilterGlideMean Description: Offsets the Elements of the streamIO, but hands them on unchanged, so also<br/>other Operations can take place on them. |
| [StatefulFloatFunction](StatefulFloatFunction.java) | Base class for the stateful streaming filters in this package (sum, product, mean, min/max, ...), each<br/>accumulating a running double value across successive #Map calls. |
| [Sum](Sum.java) | Title: Sum Description: Sums up the Elements of the streamIO, but hands them on unchanged, so also other<br/>Operations can take place on them. |
| [SumSquares](SumSquares.java) | Title: FilterSqlSum Description: Sums up the Elements of the streamIO, but hands them on unchanged, so also<br/>other Operations can take place on them. |
