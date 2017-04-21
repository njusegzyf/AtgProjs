package cn.nju.seg.atg.model.constraint;

public class Expression {

	private Expression parent = null;
	
	private ExprProperty property = null;
	
	private int offset = -1;

	public Expression getParent() {
		return this.parent;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setParent(Expression parent) {
		this.parent = parent;
	}

	public ExprProperty getProperty() {
		return this.property;
	}

	public void setProperty(ExprProperty property) {
		this.property = property;
	}
}
