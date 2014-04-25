package test;

import java.io.Serializable;

public class CalculatorInput implements Serializable {

  private static final long serialVersionUID = 1L;
  int left;
  int right;

  public CalculatorInput(int left, int right) {
    this.left = left;
    this.right = right;
  }

  
}
