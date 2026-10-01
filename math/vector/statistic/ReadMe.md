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
- Correlation
- Hypothesis Testing
facets:
  layer: domain
  status: legacy
  complexity: '4'
description: 'Statistical correlation and hypothesis-testing utilities built on top of the `vector` family''s float arrays: `Correlation` computes cross-vector association (Pearson/Spearman/Kendall), and `StatisticsFloat` runs classical hypothesis tests (t, F, chi-square, Kolmogorov-Smirnov) over float data sets, with `HyperCubeShare` as a 2D sampling test model for the latter.'
dv_has_:
  sub_:
    folders: 0
    files: 5
    units: 3
    facet_:
      layer_:
        domain: 2
        test: 1
      status_:
        legacy: 3
      complexity_:
        "2": 1
        "4": 2
    tag_:
      code_:
        hypothesis_testing: 3
        chi_squared: 1
        statistical_correlation: 1
    concept_:
      "2d_sampling_test_model": 1
      cross_vector_correlation_statistics: 1
      hypothesis_testing: 1
      "schema-org\\Class\\is_a_\\Data_Type\\Number\\Float.md": 1
has_sub_folders: 0
has_sub_files: 5
has_sub_units: 3
has_sub_facet_layer_domain: 2
has_sub_facet_layer_test: 1
has_sub_facet_status_legacy: 3
has_sub_facet_complexity_2: 1
has_sub_facet_complexity_4: 2
has_sub_tag_code_hypothesis_testing: 3
has_sub_tag_code_chi_squared: 1
has_sub_tag_code_statistical_correlation: 1
has_sub_concept_2d_sampling_test_model: 1
has_sub_concept_cross_vector_correlation_statistics: 1
has_sub_concept_hypothesis_testing: 1
has_sub_concept_schema_org_class_is_a_data_type_number_float_md: 1
---

# statistic

Statistical correlation and hypothesis-testing utilities built on top of the `vector` family's float arrays: `Correlation` computes cross-vector association (Pearson/Spearman/Kendall), and `StatisticsFloat` runs classical hypothesis tests (t, F, chi-square, Kolmogorov-Smirnov) over float data sets, with `HyperCubeShare` as a 2D sampling test model for the latter.

## Classes

| Class | Responsibility |
|---|---|
| [Correlation](Correlation.java) | Static utility collecting cross-vector correlation statistics: Pearson's linear correlation coefficient,<br/>Spearman's rank correlation, and Kendall's tau sign correlation, together with their significance tests. |
| [HyperCubeShare](StatisticsFloat.java) | Maps a 2D point to the four quadrant shares of a [-1,+1]&sup2; cube it selects, as a test model for<br/>StatisticsFloat#PROB_2D_SAMPLE_FROM_MODEL. |
| [StatisticsFloat](StatisticsFloat.java) | Static utility of hypothesis tests and contingency-table statistics over float data sets: Student's t (same<br/>mean), F-test (same variance), chi-square (goodness of fit, cross-tabulation), and 1D/2D Kolmogorov-Smirnov<br/>tests. |
