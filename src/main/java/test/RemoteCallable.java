package test;

import java.io.Serializable;
import java.util.concurrent.Callable;

public abstract class RemoteCallable<In extends Serializable, Out extends Serializable> implements Callable<Out>, Serializable {

  private static final long serialVersionUID = 1L;
  
  private final In input;
  
  public RemoteCallable(In input) {
    this.input = input;
  }
  
  public In getInput() {
    return input;
  }
}
