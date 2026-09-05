---
digest:
  local-classes:
    AFloatVectorField:
      mtime: '2026-09-05T20:47:02Z'
      digest: bb0261e08baccfdda6b166ccf9af3e1c97449e0fb8756112d5bc7f4f7f9b1345
    AOdeFloat:
      mtime: '2026-09-05T20:44:55Z'
      digest: a6366f6b9fadb6cb050ff127748ca985b5ba5796d3d7077f9789e54c0ca416e6
    HeightOde:
      mtime: '2026-09-05T10:13:18Z'
      digest: 5995d3234fcecd9053f6231f71dd0743fd648f9038c8c43c728f1b356f0a54b7
    IBinaryOpFloat:
      mtime: '2026-09-05T10:13:18Z'
      digest: 6c0f5bc83fd076ae3c31e4ca2c0d0d791387f2803b8e5cecd519427a329a42f7
    IFloatScalarField:
      mtime: '2026-09-05T20:46:47Z'
      digest: 933de58d30b33b9e34921f10ac3a359fc3aaa70dfe6f05002bd272bb814b39a9
    IFloatVectorField:
      mtime: '2026-09-05T20:46:52Z'
      digest: 29ec3f0e7ad4853f1a570cd146bd1d28aa2cca08e616d84fd32f4ee143b40215
    IFloatVectorFunction:
      mtime: '2026-09-05T20:46:57Z'
      digest: aafebf33ea7975b1776b3ca43adb6be79b3fce8f31f1b4124fd470a07d28bf3a
    OdeHeight:
      mtime: '2026-09-05T10:13:18Z'
      digest: 81cc7650ebdd7c8cfb85f94540e5dd458e0929c0ab22961867d932be4e9963bd
    OdeLorentz:
      mtime: '2026-09-05T20:46:42Z'
      digest: 78e12c66f675563f27a9cf79c5289c1c0b22208c9b833fab701fec0d66ec57bb
    OpCount:
      mtime: '2026-09-05T20:45:08Z'
      digest: f6df7bd06bda38f96a4c6aac86e6e580cae24963413ae70d4aae1e4d54b42523
    OpFirst:
      mtime: '2026-09-05T20:45:20Z'
      digest: 235aff503f0ff465d7ed2900888ff79c087725d2d547bde928db7d1b57317bcf
    OpLast:
      mtime: '2026-09-05T20:45:30Z'
      digest: 262a821cb00f8a942fc08a90c58068866e3d7a8b06a28c84e2daffef47953ce2
    OpMax:
      mtime: '2026-09-05T20:45:41Z'
      digest: 4a53a237bdf7fb24d5a402431deb7833dce002e7e4aaa4a6b36e2cd908ddec00
    OpMin:
      mtime: '2026-09-05T20:45:53Z'
      digest: 36fb1c97615563355f27d726f47af919e2c4682246f0076fb009980ea3f692d9
    OpProd:
      mtime: '2026-09-05T20:46:05Z'
      digest: 7cc9d05083c861c4c3a0a94dc89bd4d83061bcc17abcee81bfe42a76cc7ca4fc
    OpSum:
      mtime: '2026-09-05T20:46:16Z'
      digest: 2bea29ffea4a92e634fd4c4ef250f701852750af7e5f1762d002f6513025f2a6
    StepConstant:
      mtime: '2026-09-05T10:13:18Z'
      digest: cffdcdce18a10fa7714999acb4c3d3bb0f9e7efde84136330b99b2ad72af5fd2
    fChargeField:
      mtime: '2026-09-05T10:13:18Z'
      digest: b43ede2018f0ca5e2423ce2919ecff1c2946c665525b86fc274e1940b33c10f3
    fLength:
      mtime: '2026-09-05T20:46:22Z'
      digest: b63d671cae87d632aaefa230a7925ee961a5f8365dff8ac234a8dc77bf17daf6
    fProduct:
      mtime: '2026-09-05T10:13:18Z'
      digest: 0a78d73725d9c6dff80a6ef6c90c4327484bb2e810434cce2aefb9027033c25d
    fSinProd:
      mtime: '2026-09-05T10:13:18Z'
      digest: 0a78d73725d9c6dff80a6ef6c90c4327484bb2e810434cce2aefb9027033c25d
    fSum:
      mtime: '2026-09-05T20:46:35Z'
      digest: 81ce4a0d18328efd3725695e173e11b5c505ddf19a051897d5732993d44a8daa
  folders: {}
tags:
- code/vector_math
- code/differential_integration
concepts:
- Vector Fields and ODE Integration
facets:
  layer: domain
  status: broken
  complexity: high
description: Vector-valued and vector-field function contracts (`IFloatVectorFunction`/`IFloatVectorField`/`IFloatScalarField`/`IBinaryOpFloat`) with concrete implementations for ODE integration (`AOdeFloat`, `OdeLorentz` - the Lorenz attractor, `OdeHeight`, `StepConstant`) and per-element vector aggregation (`OpCount`/`OpFirst`/`OpLast`/`OpMax`/`OpMin`/`OpProd`/`OpSum`, `fLength`, `fSum`, `fProduct`, `fSinProd`, `fChargeField`).
---

# vector

Vector-valued and vector-field function contracts (`IFloatVectorFunction`/`IFloatVectorField`/`IFloatScalarField`/`IBinaryOpFloat`) with concrete implementations for ODE integration (`AOdeFloat`, `OdeLorentz` - the Lorenz attractor, `OdeHeight`, `StepConstant`) and per-element vector aggregation (`OpCount`/`OpFirst`/`OpLast`/`OpMax`/`OpMin`/`OpProd`/`OpSum`, `fLength`, `fSum`, `fProduct`, `fSinProd`, `fChargeField`).

## Classes

| Class | Responsibility |
|---|---|
| [AFloatVectorField](AFloatVectorField.java) | Base IFloatVectorField implementation supplying the batch (array-of-vectors) overloads of map in terms of the<br/>single-vector abstract methods. |
| [AOdeFloat](AOdeFloat.java) | Base class for a binary real-valued operation (IBinaryOpFloat), adding a float overload of Funktion that<br/>delegates to the double implementation. |
| [HeightOde](StepConstant.java) | Helper ODE that converts a time independent Force Field into an ODE that can be integrated by a 1-dim. |
| [IBinaryOpFloat](IBinaryOpFloat.java) | Interface for an ordinary real valued binary Operation. |
| [IFloatScalarField](IFloatScalarField.java) | Defines a scalar field: a function mapping a position vector to a single double or float value. |
| [IFloatVectorField](IFloatVectorField.java) | Defines a vector field: a function mapping one or more position vectors to a vector value of the same<br/>dimension, in either double or float precision. |
| [IFloatVectorFunction](IFloatVectorFunction.java) | Defines a function that writes a vector-valued result for a scalar input into a caller-owned output array, in<br/>either double or float precision. |
| [OdeHeight](OdeHeight.java) | Helper Class to integrate ODEs that describe a Force Field, derived from a Potential, i.e. (X',Y') ==<br/>(dX/dt,dY/dt) == (dS/dx, dS/dy). |
| [OdeLorentz](OdeLorentz.java) | ODE (Differentialgleichung) for the chaotic Lorentz curve, welche die Konvektionsrollen zwischen Schichten beschreibt. |
| [OpCount](OpCount.java) | Stateless binary operator counting the elements aggregated so far, for use e.g. building pivot matrices from<br/>streams of individual values/events. |
| [OpFirst](OpFirst.java) | Stateless binary operator that keeps the first (non-zero, non-NaN) value seen, for use e.g. building pivot<br/>matrices from streams of individual values/events. |
| [OpLast](OpLast.java) | Stateless binary operator that keeps the last value seen, discarding the running Value, for use e.g. building<br/>pivot matrices from streams of individual values/events. |
| [OpMax](OpMax.java) | Stateless binary operator computing a running maximum, for aggregating e.g. pivot matrices from streams of<br/>individual values/events. |
| [OpMin](OpMin.java) | Stateless binary operator computing a running minimum, for aggregating e.g. pivot matrices from streams of<br/>individual values/events. |
| [OpProd](OpProd.java) | Stateless binary operator computing a running product, for aggregating e.g. pivot matrices. |
| [OpSum](OpSum.java) | Stateless binary operator computing a running sum, for aggregating e.g. values in a matrix. |
| [StepConstant](StepConstant.java) | Stepper Routine, that integrates the Field from the given Starting Points on along the given Dimensions by<br/>keeping the Potential constant (by moving orthogonal to the Force Vector). |
| [fChargeField](fChargeField.java) | This Class encapsulates the the Charge Field Function, resulting from the Poisson Equation, in arbitrary Dimensions. |
| [fLength](fLength.java) | This Class implements a Function that assumes arg to be a Tensor and returns the Square of the Euklidean<br/>Length of all Coordinates. |
| [fProduct](fProduct.java) | This Class implements a Scalar Field Function that assumes arg to be a Vector (Tensor) and returns the Product<br/>of all Coordinates. |
| [fSinProd](fSinProd.java) | Returns the Product of the Sinusses of all Coordinates times Pi, i.e. the full Period fits into the unit Circle. |
| [fSum](fSum.java) | This Class implements a Function that assumes arg to be a Tensor and returns the Sum of all Coordinates. |
