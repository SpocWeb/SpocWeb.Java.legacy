---
digest:
  local-classes:
    IFormatter:
      mtime: '2026-09-05T11:45:17Z'
      digest: c33231a0bd6f28554f08af64fa75494ba836a7e9246a62c6b03c34b28c4a9e8c
    InterpolDouble:
      mtime: '2026-09-05T11:45:36Z'
      digest: 5a5dfc775c4f45cb26a56a0eb5939dc37de4d638c4f9af6ba5d85279a12be4e0
    Line2D:
      mtime: '2026-09-05T11:45:22Z'
      digest: 56b42cf08423e662ddce12828a1d0dbe889bf73a8332280c7ff52dda0248e0df
    LinearOptics2D:
      mtime: '2026-09-05T11:45:27Z'
      digest: 7d6bb42fb7823d6b155605e2e54ab0c855f507d754c97d6b6ddd9ed6dcc4e67e
    NumberFormatter:
      mtime: '2026-09-05T11:48:41Z'
      digest: dec88050ddd604b0075dbc511e7ca5ef89859ae6bbbd1df2eb0e2ef678b330ac
    Vector2D:
      mtime: '2026-09-05T11:48:10Z'
      digest: d85756be64cee3a4493516eaafb2ec977ede292a7d4c3c3621f257eb25e2313a
    Vector3D:
      mtime: '2026-09-05T11:47:40Z'
      digest: 0f1b0252c63f617e9d0e8e457c0b9fcac5f99043c91a0a638d76feb4ff5a30b0
  folders:
    algorithm/:
      mtime: '2026-09-05T10:13:18Z'
      digest: a6188a57af47e4aa5f01c54cb272affd9e370dac93122fe820fd749f156a379f
    fit/:
      mtime: '2026-09-05T11:51:11Z'
      digest: 12b30c81b8755e67dc21daee644acfac8ed9932e1526e1d221ae8c0d543d0174
    integration/:
      mtime: '2026-09-05T11:51:29Z'
      digest: 53d82bc05434ea60bf15724a3073c68ba88df7fb6e75eb0d253aa9e060a664f6
    minimizer/:
      mtime: '2026-09-05T11:48:12Z'
      digest: 158f5433ead9722821cbeada129d95115a039a8c32a1be47903d9b4c7e7db85c
    wavelet/:
      mtime: '2026-09-05T11:53:03Z'
      digest: 445608724f03edfdb6584679981616500867d6088e1f633a5edfbda24562ec10
tags:
- code/numerical_algorithm
- code/vector_math
- code/computational_geometry
concepts:
- Numerical Computing and Geometry
facets:
  layer: utility
  status: legacy
  complexity: high
description: Provides general-purpose numerical building blocks - low-dimensional Vector and Line Geometry, paraxial Optics Matrices, polynomial/spline Interpolation and Number Formatting - plus five specialized Subsystems for Combinatorial Algorithms, Curve Fitting, Numerical Integration, Function Minimization and the discrete Wavelet Transform. Most of the Subsystems are independent translations of Numerical Recipes Algorithms and depend only on the root-level Vector types (`Vector2D`, `Vector3D`) where a Geometry primitive is needed.
---

# math

Provides general-purpose numerical building blocks - low-dimensional Vector and Line
Geometry, paraxial Optics Matrices, polynomial/spline Interpolation and Number Formatting -
plus five specialized Subsystems for Combinatorial Algorithms, Curve Fitting, Numerical
Integration, Function Minimization and the discrete Wavelet Transform. Most of the
Subsystems are independent translations of Numerical Recipes Algorithms and depend only on
the root-level Vector types (`Vector2D`, `Vector3D`) where a Geometry primitive is needed.

## Classes

| Class | Responsibility |
|---|---|
| [IFormatter](IFormatter.java) | Formats an arbitrary Object into a String, optionally under a caller-supplied format. |
| [InterpolDouble](InterpolDouble.java) | Implements a polynomial Interpolator that interpolates a Function dependent on a one-dimensional Variable, and<br/>also provides static Methods for rational-function and bicubic-spline Interpolation in 2 Dimensions. |
| [Line2D](Line2D.java) | Defines a 2D Line by it's Vector2D Start and End Point |
| [LinearOptics2D](LinearOptics2D.java) | Represents a paraxial (Gaussian) optics ray-transfer (ABCD) matrix and its named factory constructions for<br/>propagation, refraction and lenses. |
| [NumberFormatter](NumberFormatter.java) | Formats numbers into fixed-width, fixed-point decimal Strings with a configurable Digit count before and after<br/>the decimal separator, and streams them directly to a Writer or OutputStream without allocating intermediate<br/>Strings. |
| [Vector2D](Vector2D.java) | Represents a 2D Vector holding its Coordinates in a shared Array, interpretable under two Coordinate Systems,<br/>plus static helper Methods for Intervals and 2x2 linear algebra. |
| [Vector3D](Vector3D.java) | Represents an immutable-shaped 3D Vector holding its Coordinates in a shared Array, interpretable under three<br/>Coordinate Systems and convertible between them. |

## Subsystems

| Folder | Domain Role | Entry Point |
|---|---|---|
| `algorithm/` | Holds two self-contained dynamic-programming Algorithms that are otherwise unrelated to the | `Bracketing` |
| `fit/` | Fits parameterized functions and scalar fields to measured data sets, covering both | `FitFields` |
| `integration/` | Implements Monte Carlo Integration over rectangular Regions of R^n, translated from | `AdaptiveMCIntegrator` |
| `minimizer/` | Numerical function-minimization algorithms translated from Numerical Recipes: bracketing | `AFloatMinimizer` |
| `wavelet/` | Implements the discrete Wavelet Transform (Numerical Recipes Chapter 13.10) as a Strategy | `Daubechies4` |

## Entry Points

| Class.Method | Description |
|---|---|
| [InterpolDouble.Map(double)](InterpolDouble.java#L337) | Evaluates the polynomial Interpolation Polynom at the given Position. |
| [Line2D.intersects(Line2D)](Line2D.java#L35) | Tests whether this Line segment intersects another one. |
| [LinearOptics2D.Propagation(double, double)](LinearOptics2D.java#L48) | Builds the ray-transfer Matrix for straight-line Propagation in a Medium. |
| [NumberFormatter.format(Object, String)](NumberFormatter.java#L128) | Formats a Number into a fixed-width, fixed-point decimal String. |
