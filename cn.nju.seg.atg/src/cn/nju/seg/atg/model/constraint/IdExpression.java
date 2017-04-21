package cn.nju.seg.atg.model.constraint;

/**
 * @version 0.1 Remove default constructor and make this class final.
 * 
 * @author zy
 * @author Zhang Yifan
 */
public final class IdExpression extends Expression {

  private String name;

  public IdExpression(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
