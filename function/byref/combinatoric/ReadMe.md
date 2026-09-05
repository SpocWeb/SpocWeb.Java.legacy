---
digest:
  local-classes:
    Bernoulli:
      mtime: '2026-09-05T20:59:44Z'
      digest: 6118a8824e06960135a34f83cf02180857b98cebafe2d67af0605dc22fac81ff
    BesselFuncs:
      mtime: '2026-09-05T20:59:55Z'
      digest: 6e3a7727d322ad2a6fc9d6d838058bdf3cca1c487b6aabc3342e2abb6b621887
    CombiFuncs:
      mtime: '2026-09-05T10:13:18Z'
      digest: 6bc34ff54e0266c7f4baa454fd115f765c933b761e2562ecefde9385d53ea2ca
    DblFactorial:
      mtime: '2026-09-05T10:13:18Z'
      digest: be3b47561a8f89eb26fc2d3f72be9106090d1b951972687d8093641fb80e11a2
    Factorial:
      mtime: '2026-09-05T20:59:46Z'
      digest: 961dc71d6f19f4205b37911e74d71033dc4cffd7a080d277a2f9459faf5deeba
    Prime:
      mtime: '2026-09-05T20:59:51Z'
      digest: 9effac5d7fad9c3a7b2619bb4d5552f7cd3e1bbd43c70e46afc68d589e768b10
    ProbFuncs:
      mtime: '2026-09-05T20:59:53Z'
      digest: 7d2ef15b98e1ca414fe6852c90f0934ef24781ad8781fbbc9f27dc9d11e4c31c
    TestCombinatoric:
      mtime: '2026-09-05T10:13:18Z'
      digest: a32f6471d3d5bb15d4d766c6c05b666248b80f41d1ac54a3286cc6661af5543a
  folders: {}
tags:
- code/combinatorics
- code/special_function
concepts:
- Combinatorics and Special Functions
facets:
  layer: utility
  status: legacy
  complexity: medium
description: 'Combinatorics and special functions built on the `byref` wrappers: `Factorial`/`DblFactorial` (with recursive caching), `Bernoulli` numbers, `Prime` (sieve-based prime cache), `CombiFuncs` (binomial coefficients etc.), `ProbFuncs` (probability distributions), and `BesselFuncs` (Bessel functions).'
---

# combinatoric

Combinatorics and special functions built on the `byref` wrappers: `Factorial`/`DblFactorial` (with recursive caching), `Bernoulli` numbers, `Prime` (sieve-based prime cache), `CombiFuncs` (binomial coefficients etc.), `ProbFuncs` (probability distributions), and `BesselFuncs` (Bessel functions).

## Classes

| Class | Responsibility |
|---|---|
| [Bernoulli](Bernoulli.java) | Calculates and stores the BERNOULLI Values for each number. |
| [BesselFuncs](BesselFuncs.java) | Definition of ALL Bessel Functions: I, J, K |
| [CombiFuncs](CombiFuncs.java) | Performant Implementation of the combinatoric Functions. |
| [DblFactorial](DblFactorial.java) | Calculates and stores the double Factorial for each number: n! The Caching ensures a fast access to previously<br/>used Faculties. |
| [Factorial](Factorial.java) | Calculates and stores the Factorial for each number. |
| [Prime](Prime.java) | This Class defines the Prime Numbers necessary for breaking up Integers into their Prime Numbers some Methods<br/>to test for Primality and the PI Function as well as it's Inverse. |
| [ProbFuncs](ProbFuncs.java) | Defines many Functions describing (accumulated) Probabilities Most of these Functions are (unfortunately) also<br/>defined in BodyFuncs That was the Reason, why most Constants were defined in IMeasurAble! |
| [TestCombinatoric](TestCombinatoric.java) | This class can take a variable number of parameters on the command line. |
