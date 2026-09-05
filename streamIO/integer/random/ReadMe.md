---
digest:
  local-classes:
    ARandomInt:
      mtime: '2026-09-05T21:49:48Z'
      digest: ea5ffb5edfceb342e0114d2e745bfd25503b263c4db16269086361ce892d6845
    ARandomLong:
      mtime: '2026-09-05T21:50:07Z'
      digest: 8c5a7bce4c41ada8eff936cbc7eb3e912295014cea9098e4f3fe3809e0d39b22
    AStreamIn_BoundInt:
      mtime: '2026-09-05T21:50:42Z'
      digest: 5c4d0a54a60ac3d4e02ace9e86abf196e2115634b8a22bc2f6259c4fdc1d6ff8
    BitNoise:
      mtime: '2026-09-05T21:51:32Z'
      digest: 7717ef41157b610dcd072cafafc7273ff74abbf9e12d1779f086b0d874c7984c
    IStreamIn_Bound_Int:
      mtime: '2026-09-05T21:50:50Z'
      digest: 9d11b75c33e63a7844682da662efacc030fffad69b5fb34e7462354ab3f79b3a
    RandomAffine:
      mtime: '2026-09-05T21:51:54Z'
      digest: 0b085df0c025fb8482f40e9317fd9cf6dcaa4761c545353e893b2bdec8bcd4a6
    RandomBinomial:
      mtime: '2026-09-05T10:13:31Z'
      digest: 8d80c94f9fb0d7270e3ddc19269caa2240f7b4e7be3c2870873957a425b1db2e
    RandomBit:
      mtime: '2026-09-05T21:52:05Z'
      digest: 49d15691e8c2373ba87c770b00a54c403f9aaf131f084b64c1f3c00ef540f220
    RandomBit2:
      mtime: '2026-09-05T21:52:14Z'
      digest: 52de0ca4044fb425f4543c6f0744be4ce930e897d6abbf047ebfbeb33816a7c7
    RandomBySubt:
      mtime: '2026-09-05T21:55:51Z'
      digest: 6435e4f319e0b68861c00fd00cd450c1d5c119ca3afff848d6492e99ae1f0513
    RandomDiscrete:
      mtime: '2026-09-05T21:55:55Z'
      digest: 64800fc6fd973b38b4828e7006b772c9eadb06a85c2dbe7c4ae8cb056fe1d6d8
    RandomFast:
      mtime: '2026-09-05T21:55:56Z'
      digest: 76eb3ad7312a25c82e42e07c915cd757fd731fdcf9eafc43dbebffbdd7fbacef
    RandomJava:
      mtime: '2026-09-05T10:13:31Z'
      digest: 3e0a880bf148a0dfec3bea4bb1d8eca06d53fa1f3eef15af71791441d5691b9b
    RandomLinear:
      mtime: '2026-09-05T10:13:31Z'
      digest: e4fe388f34c132d5fdb591eebb31d680909f936f2f1804f6ef6f43612fb41d22
    RandomLong:
      mtime: '2026-09-05T10:13:31Z'
      digest: 005202de2a8c9c2ef3f3207c862f779d16358b49e29091fb6de57bd77b1b44ee
    RandomMix:
      mtime: '2026-09-05T21:56:00Z'
      digest: 1ac4fc978dc70dcafbbdc865b6a25d9c33528456f111f461d7881baa31969abd
    RandomPseudoBinary:
      mtime: '2026-09-05T21:56:16Z'
      digest: ba8ca2ac6d7ed571f14dcd72c474278234796a354d501a8550ba56bb5007264b
    RandomPseudoGAdic:
      mtime: '2026-09-05T21:56:36Z'
      digest: 23403b54a571279179142ac8ab3f6b303f4454385f92f1144f4ad1002be0ccc3
    RandomQuick:
      mtime: '2026-09-05T10:13:31Z'
      digest: 9ac47695ad6b382fa46b1b894f227060193c8b89108dcc7b085e5566544c4f62
    RandomShuffle:
      mtime: '2026-09-05T10:13:31Z'
      digest: 2c5de743ab23a5a227b6abf5e792a8cd69bc103428210946e1943c3d7e754c3d
  folders: {}
tags:
- code/random_number_generation
- code/quasi_random_sequence
concepts:
- Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay
facets:
  layer: utility
  status: legacy
  complexity: medium
description: 'A family of pseudo-random integer generators sharing the `IStreamIn_Bound_Int` contract (bounded `nextInt`/`nextLong`/`nextFloat`/`nextDouble`, with `getPosition()`/`reSet()` for mark-and-restore replay). `AStreamIn_BoundInt`/`ARandomInt`/`ARandomLong` supply the common scaffolding; concrete generators implement classic algorithms - linear/affine congruential (`RandomLinear`, `RandomAffine`, `RandomFast`, `RandomQuick`), Knuth''s subtractive method (`RandomBySubt`), Java''s built-in generator wrapped for this interface (`RandomJava`), a sum of two independent linear generators for a very long period (`RandomMix`), a further shuffling wrapper (`RandomShuffle`), linear-feedback shift-register bit generators (`RandomBit`, `RandomBit2`), quasi-random low-discrepancy sequences (`RandomPseudoBinary`, `RandomPseudoGAdic`, Halton-style), and derived non-uniform distributions (`RandomDiscrete` for arbitrary discrete distributions, `RandomBinomial` for the binomial distribution). `BitNoise` is a companion `IIntFunction` that flips a configurable density of bits using one of these generators, for injecting noise into a signal under test. Several generators have known state-handling bugs flagged inline with `TODO: LOGIC` - notably `RandomBit2.getPosition()` returning the wrong field (breaking mark/restore, unlike sibling `RandomBit`), `AStreamIn_BoundInt.nextLong(long)` truncating large bounds to `int`, `BitNoise`''s `Map(long)` decrementing its countdown twice as fast as `Map(int)`, and `RandomMix.reset(long)` unconditionally throwing instead of setting the seed.'
---

# random

A family of pseudo-random integer generators sharing the `IStreamIn_Bound_Int` contract (bounded
`nextInt`/`nextLong`/`nextFloat`/`nextDouble`, with `getPosition()`/`reSet()` for
mark-and-restore replay). `AStreamIn_BoundInt`/`ARandomInt`/`ARandomLong` supply the common
scaffolding; concrete generators implement classic algorithms - linear/affine congruential
(`RandomLinear`, `RandomAffine`, `RandomFast`, `RandomQuick`), Knuth's subtractive method
(`RandomBySubt`), Java's built-in generator wrapped for this interface (`RandomJava`), a sum of
two independent linear generators for a very long period (`RandomMix`), a further shuffling
wrapper (`RandomShuffle`), linear-feedback shift-register bit generators (`RandomBit`,
`RandomBit2`), quasi-random low-discrepancy sequences (`RandomPseudoBinary`,
`RandomPseudoGAdic`, Halton-style), and derived non-uniform distributions
(`RandomDiscrete` for arbitrary discrete distributions, `RandomBinomial` for the binomial
distribution). `BitNoise` is a companion `IIntFunction` that flips a configurable density of
bits using one of these generators, for injecting noise into a signal under test. Several
generators have known state-handling bugs flagged inline with `TODO: LOGIC` - notably
`RandomBit2.getPosition()` returning the wrong field (breaking mark/restore, unlike sibling
`RandomBit`), `AStreamIn_BoundInt.nextLong(long)` truncating large bounds to `int`, `BitNoise`'s
`Map(long)` decrementing its countdown twice as fast as `Map(int)`, and `RandomMix.reset(long)`
unconditionally throwing instead of setting the seed.

## Classes

| Class | Responsibility |
|---|---|
| [ARandomInt](ARandomInt.java) | Abstract base for a pseudo-random integer generator, adding float/double derivation and time-based seeding on<br/>top of AStreamIn_BoundInt's bounded-integer contract. |
| [ARandomLong](ARandomLong.java) | Abstract Random Number Generator using an Integer (long) Generator and emulating various Generators of other<br/>simple Types. |
| [AStreamIn_BoundInt](AStreamIn_BoundInt.java) | Abstract Random Number Generator using an Integer (int) Generator and emulating various Generators of other<br/>primitive Types. |
| [BitNoise](BitNoise.java) | Adds a configurable amount of Noise to the Bits handed over. |
| [IStreamIn_Bound_Int](IStreamIn_Bound_Int.java) | Interface for a for a streamIO of bounded Numbers e.g. for Random Number Generators. |
| [RandomAffine](RandomAffine.java) | Generic Random Number Generator using the Affine Congruential Algorithm with Variables of Type Integer<br/>Generates a uniform integer Distribution between 0 and Modulus. |
| [RandomBinomial](RandomBinomial.java) | Returns integer random Numbers distributed in a Binomial fashion i.e. p(j) = (n!/j!(n-j)!)*p^j*(1-p)^(n-j)<br/>This is the Probability of i Poisson Events happening within a Time Interval T. This is related to the Gamma<br/>Distribution, that returns the waiting Time for the i-th Poisson Event. |
| [RandomBit](RandomBit.java) | Returns random Bits distributed in a Uniform fashion i.e. p(1) = p(0) = 0.5 |
| [RandomBit2](RandomBit2.java) | Returns random Bits distributed in a Uniform fashion i.e. p(1) = p(0) = 0.5 This is a faster Implementation<br/>than RandomBit, but less reliable and with a Period of ... |
| [RandomBySubt](RandomBySubt.java) | Integer Random deviate by Donald Knuth's subtractive method. |
| [RandomDiscrete](RandomDiscrete.java) | Returns random Numbers with the Probability Distribution given in the Constructor. |
| [RandomFast](RandomFast.java) | Fast Random Number Generator using an Affine Congruential Algorithm with good choices of the Parameters, so<br/>the first Multiplication can be replaced by a shifting Operation. |
| [RandomJava](RandomJava.java) | Random Number Generator encapsulating the standard Java Algorithm Unfortunately I don't know the<br/>Implementation, so I cannot be sure of the Algorithm or it's Parameters, like the Modulus Speed: fast(2) |
| [RandomLinear](RandomLinear.java) | Random Number Generator using a linear (not the affine) Algorithm: x[n+1] = (x[n]*a)% m A Consequence is that<br/>x must never become 0! To avoid integer Overflow, Schrage's Result can be applied: m = q*a + r with the<br/>Factorization of m by a it shows that when q > r and 0 < x < m-1 (a*x)%m = a(x%q) - r(x/q) if this is > 0 and<br/>+=m otherwise. |
| [RandomLong](RandomLong.java) | Generic Random Number Generator using the Linear Congruential Algorithm with Variables of Type Long as compared to |
| [RandomMix](RandomMix.java) | Generate a Random Number as the Sum of two independent Linear (not affine) Generators with different<br/>(relatively prime) Periods. |
| [RandomPseudoBinary](RandomPseudoBinary.java) | Generates a Sub-Random Binary Sequence that equally fills up any given Space. |
| [RandomPseudoGAdic](RandomPseudoGAdic.java) | Pseudo-Random Number Generator for arbitrary base Numbers (preferably Primes). |
| [RandomQuick](RandomQuick.java) | 'Quick and Dirty' Random Number Generator using affine Algorithm with automatic Modulus of 2^32 by Truncation<br/>of higher Bits using an unsigned 32 Bit Integer. |
| [RandomShuffle](RandomShuffle.java) | Random Number Generator; using a primary Generator for the actual Values. |
