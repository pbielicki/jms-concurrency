#!/bin/bash
mvn exec:java -Dexec.mainClass="test.TaskExecutor" -Dexec.args="tcp://ncel3361:61616"
