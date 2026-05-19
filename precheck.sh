#!/bin/bash
sbt clean scalafmt Test/scalafmt coverage test it/test it/scalafmt it/coverageReport coverageReport