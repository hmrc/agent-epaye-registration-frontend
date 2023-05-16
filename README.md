# agent-epaye-registration-frontend

[![Build Status](https://travis-ci.org/hmrc/agent-epaye-registration-frontend.svg)](https://travis-ci.org/hmrc/agent-epaye-registration-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/agent-epaye-registration-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/agent-epaye-registration-frontend/_latestVersion)

This is a frontend microservice for Agent EPAYE Registration, part of interim replacement of OPRA (On-Line Pre-Registration of Agents).

The OPRA system provides a way for PAYE agents (not otherwise known to PAYE systems and therefore without known facts) to obtain a reference number that can be used as a known fact to enable them to register and enrol as a PAYE agent.

## Features

- Agent who needs an Agent PAYE reference code is able to enter its details and can be issued a code

## Running the tests

    sbt test it:test

## Test Coverage

    sbt clean coverage test coverageReport

## Running the app locally

    ./run_local.sh

or

    sm --start AGENT_EPAYE_REG_ALL -r
    sm --stop AGENT_EPAYE_REGISTRATION_FRONTEND
    sbt -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes run

It should then be listening on port 9446

    browse http://localhost:9446/agent-epaye-registration

## Stop local upstream services

    sm --stop AGENT_EPAYE_REG

### License


This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")