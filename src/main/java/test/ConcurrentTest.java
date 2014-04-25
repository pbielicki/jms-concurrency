package test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConcurrentTest {

  public static void main(String[] args) throws Exception {
    String uri = "tcp://localhost:61616";
    if (args.length > 0) {
      uri = args[0];
    }
    
    ExecutorService service = new JmsExecutor(uri);
    List<Callable<CalculatorOutput>> tasks = new ArrayList<Callable<CalculatorOutput>>();
    CalculatorInput[] in = new CalculatorInput[] { 
    		new CalculatorInput(10, 20), 
    		new CalculatorInput(30, 50),
    		new CalculatorInput(20, 55),
    		new CalculatorInput(35, 150),
    		new CalculatorInput(130, 500),
    		new CalculatorInput(10, 20),
    		new CalculatorInput(10, 200),
    		new CalculatorInput(11, 22),
    		new CalculatorInput(0, 10),
    		new CalculatorInput(40, 25),
        new CalculatorInput(60, 80),
    };
		
    for (final CalculatorInput ci : in) {
      tasks.add(new Sum(ci));
    }

    List<Future<CalculatorOutput>> responses = service.invokeAll(tasks);
    int sum = 0;
    for (Future<CalculatorOutput> resp : responses) {
      sum += resp.get().getResult();
    }

    System.out.println("" + sum);
    service.shutdown();
  }

  static class Sum extends RemoteCallable<CalculatorInput, CalculatorOutput> {
    private static final long serialVersionUID = 1L;

    public Sum(CalculatorInput input) {
      super(input);
    }

    @Override
    public CalculatorOutput call() throws Exception {
      return new CalculatorOutput(getInput().left + getInput().right);
    }
  }
}
