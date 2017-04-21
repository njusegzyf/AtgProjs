package cn.nju.seg.atg.relevant;

import java.util.HashMap;
import java.util.Map;

/**
 * 变量相关表
 * @author zy
 *
 */
public class RelevantTable {
    /**
     * 存储路径上每个分支节点与输入变量的相关性信息
     */
	private Map<String, boolean[]> relevantTable;
	
	/**
	 * 带参构造函数
	 * @param branchNum
	 * @param paramNum
	 */
	public 	RelevantTable(){
		//初始化相关表
		this.relevantTable = new HashMap<String, boolean[]>();
	}
	
	/**
	 * 根据分支节点名称加入相关信息
	 * @param nodeName
	 * @param relevantInfo
	 */
	public void addRelevantInfo(String nodeName, boolean[] relevantInfo){
		this.relevantTable.put(nodeName, relevantInfo);
	}
	
	/**
	 * 根据输入变量下标与分支节点名称，返回该变量与分支节点的相关性
	 * @param nodeName
	 * @param paramIndex
	 * @return
	 */
	public boolean getRelevantInfo(String nodeName, int paramIndex){
		return this.relevantTable.get(nodeName)[paramIndex];
	}
}
