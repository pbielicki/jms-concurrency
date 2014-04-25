package test;

import java.io.Serializable;

public class CalculatorOutput implements Serializable {

  private static final long serialVersionUID = 1L;
  private int result;

  public CalculatorOutput(int result) {
    this.result = result;
  }
  
  public int getResult() {
    return result;
  }
  
  @Override
  public String toString() {
    return "" + result;
  }
}
