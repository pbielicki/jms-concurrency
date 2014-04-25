#!/bin/bash
mvn exec:java -Dexec.mainClass="test.ConcurrentTest" -Dexec.args="tcp://ncel3361:61616"
