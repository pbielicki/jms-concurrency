package test;

import java.io.Serializable;
import java.util.concurrent.Callable;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class TaskExecutor implements MessageListener {

  private Session session;

  public TaskExecutor(Session session) {
    this.session = session;
  }

  @Override
  public void onMessage(Message message) {
    if (message instanceof ObjectMessage) {
      ObjectMessage msg = (ObjectMessage) message;
      try {
        if (msg.getObject() instanceof Callable) {
          Object result = ((Callable<?>) msg.getObject()).call();
          session.createProducer(message.getJMSReplyTo())
              .send(session.createObjectMessage((Serializable) result));
          
          System.out.println("Thread " + Thread.currentThread().getId() + " OK for " + msg.getObject() + " reuslt " + result);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }
  
  public static void main(String[] args) throws Exception {
    String uri = "tcp://localhost:61616";
	if (args.length > 0) {
	  uri = args[0];
	}
    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(uri);
    Connection connection = factory.createConnection();
    for (int i = 0; i < 10; i++) {
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Queue destination = session.createQueue("jms/inqueue");
      session.createConsumer(destination).setMessageListener(new TaskExecutor(session));
      connection.start();
    }
  }

}
