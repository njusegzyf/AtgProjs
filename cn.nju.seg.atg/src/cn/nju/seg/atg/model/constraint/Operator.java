package cn.nju.seg.atg.model.constraint;

/**
 * 关系/逻辑操作符
 * @author zhouyan
 *
 */
public enum Operator {
	/**
	 * ==
	 */
  	EQ("=="),
  	
  	/**
  	 * !=
  	 */
	NE("!="),
	
	/**
	 * <
	 */
	LT("<"),
	
	/**
	 * <=
	 */
	LE("<="),
	
	/**
	 * >
	 */
	GT(">"),
	
	/**
	 * >=
	 */
	GE(">="),
  	
  	AND("&&"),
	OR("||");

    private final String str;
    
    Operator(String str){
 	   this.str= str;
    }
    
	/**
//	 * ">="/">"/"<="/"<"/"=="/"!="/"&&"/"||"
//	 * @param operator
//	 * @return reverse op
//	 */
	public static Operator getReverseOp(Operator op) {
		switch(op){
		case AND:
			return Operator.OR;
		case OR:
			return Operator.AND;
		case NE:
			return Operator.EQ;
		case EQ:
			return Operator.NE;
		case LT:
			return Operator.GE;
		case GE:
			return Operator.LT;
		case LE:
			return Operator.GT;
		default:
			return Operator.LE;
		}
	}
    
    @Override
	public String toString() {
		return str;
	}
}
