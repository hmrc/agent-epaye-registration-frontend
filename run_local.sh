#!/usr/bin/env bash

echo "-------------------------------------------------------------"
echo "-- RUNNING UPSTREAM SERVICES LOCALLY USING SERVICE MANAGER --"
echo "-------------------------------------------------------------"
sm --start AGENT_EPAYE_REG_ALL -r
sm --stop AGENT_EPAYE_REGISTRATION_FRONTEND
echo "-------------------------------------------------------------"
echo "--       RUNNING AGENT EPAYE REGISTRATION FRONTEND         --"
echo "-------------------------------------------------------------"
sbt -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes run