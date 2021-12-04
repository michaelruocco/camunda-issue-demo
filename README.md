# Camunda Issue Demo

[![Build](https://github.com/michaelruocco/camunda-issue-demo/workflows/pipeline/badge.svg)](https://github.com/michaelruocco/camunda-issue-demo/actions)
[![codecov](https://codecov.io/gh/michaelruocco/camunda-issue-demo/branch/master/graph/badge.svg?token=FWDNP534O7)](https://codecov.io/gh/michaelruocco/camunda-issue-demo)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/272889cf707b4dcb90bf451392530794)](https://www.codacy.com/gh/michaelruocco/camunda-issue-demo/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=michaelruocco/camunda-issue-demo&amp;utm_campaign=Badge_Grade)
[![BCH compliance](https://bettercodehub.com/edge/badge/michaelruocco/camunda-issue-demo?branch=master)](https://bettercodehub.com/)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=michaelruocco_camunda-issue-demo&metric=alert_status)](https://sonarcloud.io/dashboard?id=michaelruocco_camunda-issue-demo)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=michaelruocco_camunda-issue-demo&metric=sqale_index)](https://sonarcloud.io/dashboard?id=michaelruocco_camunda-issue-demo)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=michaelruocco_camunda-issue-demo&metric=coverage)](https://sonarcloud.io/dashboard?id=michaelruocco_camunda-issue-demo)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=michaelruocco_camunda-issue-demo&metric=ncloc)](https://sonarcloud.io/dashboard?id=michaelruocco_camunda-issue-demo)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.michaelruocco/camunda-issue-demo.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.michaelruocco%22%20AND%20a:%22camunda-issue-demo%22)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Overview

This is a repo to be used for demonstrating issues or problems found when using Camunda.

## Useful Commands

```gradle
// cleans build directories
// prints currentVersion
// formats code
// builds code
// runs tests
// checks for gradle issues
// checks dependency versions
./gradlew clean currentVersion dependencyUpdates lintGradle spotlessApply build
```