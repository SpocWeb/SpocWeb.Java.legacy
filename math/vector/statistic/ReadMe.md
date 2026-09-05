---
digest:
  local-classes:
    Correlation:
      mtime: '2026-09-05T12:51:41Z'
      digest: 91ae175b90447f995760fd819ca039fa5eca28c832920bf4d295d1227f92fe76
    HyperCubeShare:
      mtime: '2026-09-05T12:52:49Z'
      digest: 59f810c9af4480b89d0c0d1bc28ab2e57bd44753f0081dbf463698af816da14d
    StatisticsFloat:
      mtime: '2026-09-05T12:52:49Z'
      digest: e3ef5980c41dfcea2b3a6ae68b5b14c1f9fe7c9f64bfc3bb621613438a34fbd1
  folders: {}
tags:
- code/statistical_correlation
- code/hypothesis_testing
concepts:
- Statistical Correlation and Hypothesis Testing
facets:
  layer: domain
  status: legacy
  complexity: high
description: 'Statistical correlation and hypothesis-testing utilities built on top of the `vector` family''s float arrays: `Correlation` computes cross-vector association (Pearson/Spearman/Kendall), and `StatisticsFloat` runs classical hypothesis tests (t, F, chi-square, Kolmogorov-Smirnov) over float data sets, with `HyperCubeShare` as a 2D sampling test model for the latter.'
---

# statistic

Statistical correlation and hypothesis-testing utilities built on top of the `vector` family's float arrays: `Correlation` computes cross-vector association (Pearson/Spearman/Kendall), and `StatisticsFloat` runs classical hypothesis tests (t, F, chi-square, Kolmogorov-Smirnov) over float data sets, with `HyperCubeShare` as a 2D sampling test model for the latter.

## Classes

| Class | Responsibility |
|---|---|
| [Correlation](Correlation.java) | Static utility collecting cross-vector correlation statistics: Pearson's linear correlation coefficient,<br/>Spearman's rank correlation, and Kendall's tau sign correlation, together with their significance tests. |
| [HyperCubeShare](StatisticsFloat.java) | Maps a 2D point to the four quadrant shares of a [-1,+1]&sup2; cube it selects, as a test model for<br/>StatisticsFloat#PROB_2D_SAMPLE_FROM_MODEL. |
| [StatisticsFloat](StatisticsFloat.java) | Static utility of hypothesis tests and contingency-table statistics over float data sets: Student's t (same<br/>mean), F-test (same variance), chi-square (goodness of fit, cross-tabulation), and 1D/2D Kolmogorov-Smirnov<br/>tests. |
