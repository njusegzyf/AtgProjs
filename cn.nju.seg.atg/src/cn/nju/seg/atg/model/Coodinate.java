package cn.nju.seg.atg.model;

/**
 * 坐标类(x, y)
 * @author zy
 *
 */
public class Coodinate {
	private double x;
	private double y;
	
	/**
	 * 无参构造函数
	 */
	public Coodinate() 
	{
		this.x = 0.0;
		this.y = 0.0;
	}
	
	@Override
	public String toString(){
		return "("+this.x+","+this.y+")";
	}
	
	/**
	 * 带参构造函数
	 * @param x
	 * @param y
	 */
	public Coodinate(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
}
