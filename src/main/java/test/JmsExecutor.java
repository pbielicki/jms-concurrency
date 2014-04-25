package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsExecutor implements ExecutorService {
  
  ConnectionFactory factory;
  Connection connection;
  Destination destination;
  Destination replyTo;
  Session session;
  MessageProducer producer;
  
  public JmsExecutor(String uri) {
    factory = new ActiveMQConnectionFactory(uri);
    try {
      connection = factory.createConnection();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      destination = session.createQueue("jms/inqueue");
      replyTo = session.createTemporaryQueue();
      producer = session.createProducer(destination);
      connection.start();
    } catch (JMSException e) {
      throw new IllegalStateException("Unable to initialize JMS connection", e);
    }
  }

  @Override
  public void execute(Runnable command) {
    // TODO Auto-generated method stub
  }

  @Override
  public void shutdown() {
    try {
      connection.stop();
      connection.close();
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<Runnable> shutdownNow() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isShutdown() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isTerminated() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Future<?> submit(Runnable task) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    for (Callable<T> callable : tasks) {
      try {
        ObjectMessage msg = session.createObjectMessage((RemoteCallable<?, ?>) callable);
        msg.setJMSReplyTo(replyTo);
        producer.send(msg);
      } catch (JMSException e) {
        throw new IllegalArgumentException("Unable to send JMS message(s)", e);
      }
    }
    
    final BlockingQueue<T> queue = new ArrayBlockingQueue<T>(tasks.size());
    List<Future<T>> result = new ArrayList<Future<T>>(tasks.size());
    for (int i = 0; i < tasks.size(); i++) {
      result.add(new Future<T>() {
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
          return false;
        }
        
        @Override
        public T get() throws InterruptedException, ExecutionException {
          return queue.take();
        }
        
        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
          return queue.poll(timeout, unit);
        }
        
        @Override
        public boolean isCancelled() {
          return false;
        }
        
        @Override
        public boolean isDone() {
          return queue.isEmpty() == false;
        }
      });
    }
    
    try {
      session.createConsumer(replyTo).setMessageListener(new MessageListener() {
        @Override
        public void onMessage(Message message) {
          if (message instanceof ObjectMessage) {
            try {
              queue.offer((T) ((ObjectMessage) message).getObject());
            } catch (JMSException e) {
              e.printStackTrace();
            }
          }
        }
      });
    } catch (JMSException e) {
      e.printStackTrace();
    }
    return result;
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    // TODO Auto-generated method stub
    return null;
  }

}
