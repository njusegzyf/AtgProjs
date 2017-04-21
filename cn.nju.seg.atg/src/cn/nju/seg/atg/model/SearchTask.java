package cn.nju.seg.atg.model;

/**
 * 搜索的种子点
 * @author zy
 *
 */
public class SearchTask implements Comparable<SearchTask>{
	
    /**
     * 搜索变量下标
     */
	private final int searchIndex;

	/**
	 * 初始的程序输入数据
	 */
	private final double[] initInputs;
	
	/**
	 * 任务优先级
	 */
	private final double priority;
	
	/**
	 * 搜索变量下标已经更迭的次数
	 */
	private final int changeTime;
	
	/**
	 * 带参构造函数
	 * @param u
	 * @param p
	 * @param si
	 * @param ct
	 */
	public SearchTask(double[] u, double p, int si, int ct){
		this.initInputs = u;
		this.priority = p;
		this.searchIndex = si;
		this.changeTime = ct;
	}

	public int getSearchIndex() {
		return searchIndex;
	}

	public double[] getInitInputs() {
		return initInputs;
	}

	public double getPriority() {
		return priority;
	}

	public int getChangeTime() {
		return changeTime;
	}

	@Override
	public int compareTo(SearchTask o) {
		return priority > o.priority ? 1 :
			(priority < o.priority ? -1 : 0);
	}
}

