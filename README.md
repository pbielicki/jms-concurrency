jms-concurrency
===============

<code>java.util.concurrent.ExecutorService</code> implementation based on JMS

How to make it work?
-------------

1. Download [ActiveMQ distribution version 5.9.x](http://www.us.apache.org/dist/activemq/5.9.1/)
2. Unpack and run the ActiveMQ broker <code>ACTIVEMQ_HOME/bin/activemq(.bat)</code>
3. Build the project <code>mvn package</code>
4. Run task executor (this will register message consumers waiting for the client tasks to execute) <code>mvn exec:java -Dexec.mainClass="test.TaskExecutor" -Dexec.args="tcp://localhost:61616"</code>
5. Then run the client using multiple tasks by executing <code>mvn exec:java -Dexec.mainClass="test.ConcurrentTest" -Dexec.args="tcp://localhost:61616"</code>
6. Ideally you should ActiveMQ broker, task executor and client on three different machines to see that it's really distributed and works over TCP (in such case you need to replace <code>localhost</code> by the machine name on which ActiveMQ broker is running.