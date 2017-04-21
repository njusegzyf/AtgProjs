package cn.nju.seg.atg.model.constraint;

/**
 * 条件表达式
 * 
 * @version 0.1 Fix `hashCode` and `equals`, and make this class final.
 * @author zhouyan
 * @author Zhang Yifan
 */
public final class BinaryExpression extends Expression {
  private Operator op;
  private Expression operand1;
  private Expression operand2;
  private String id;

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }

  /**
   * @since 0.1
   */
  public BinaryExpression() {
    this(null, null, null);
  }
  
  public BinaryExpression(Operator op, Expression operand1, Expression operand2) {
    this.op = op;
    this.operand1 = operand1;
    this.operand2 = operand2;
  }

  public Operator getOp() {
    return this.op;
  }

  public void setOp(Operator op) {
    this.op = op;
  }

  public Expression getOperand1() {
    return this.operand1;
  }

  public void setOperand1(Expression operand1) {
    this.operand1 = operand1;
  }

  public Expression getOperand2() {
    return this.operand2;
  }

  public void setOperand2(Expression operand2) {
    this.operand2 = operand2;
  }

  @Override
  public String toString() {
    if (this.op == Operator.AND || this.op == Operator.OR)
      return "(" + this.operand1.toString() + ")" + this.op.toString() + "(" + this.operand2.toString() + ")";
    else
      return this.operand1.toString() + this.op.toString() + this.operand2.toString();
  }

  /**
   * @since 0.1
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof BinaryExpression) {
      return (this.getId() == ((BinaryExpression) obj).getId());
    } else {
      return false;
    }
  }

  /**
   * @since 0.1
   */
  @Override
  public int hashCode() {
    return this.id.hashCode();
  }
}
