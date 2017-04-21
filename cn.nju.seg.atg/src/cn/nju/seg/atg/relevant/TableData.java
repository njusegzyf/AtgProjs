package cn.nju.seg.atg.relevant;

/**
 * 为每个测试函数手动设置约束相关表
 * @author zy
 *
 */
public class TableData {
    private  RelevantTable data;
    
    public TableData(){
    	this.data = new RelevantTable();
    }
    
    /**
	 * 根据输入变量下标与分支节点名称，返回该变量与分支节点的相关性
	 * @param nodeName
	 * @param paramIndex
	 * @return
	 */
	public boolean getRelevantInfo(String nodeName, int paramIndex){
		return this.data.getRelevantInfo(nodeName, paramIndex);
	}
    
    public void data_for_caldat(){
    	this.data.addRelevantInfo("node2", new boolean[]{true, false, false, false});
    	this.data.addRelevantInfo("node4", new boolean[]{true, false, false, false});
    	this.data.addRelevantInfo("node8", new boolean[]{true, false, false, false});
    	this.data.addRelevantInfo("node11", new boolean[]{true, false, false, false});
    	this.data.addRelevantInfo("node13", new boolean[]{true, false, false, false});
    	this.data.addRelevantInfo("node15", new boolean[]{true, false, false, false});
    }
    
    public void data_for_julday(){
    	this.data.addRelevantInfo("node2", new boolean[]{false, false, true});
    	this.data.addRelevantInfo("node4", new boolean[]{false, false, true});
    	this.data.addRelevantInfo("node6", new boolean[]{true, false, false});
    	this.data.addRelevantInfo("node10", new boolean[]{true, true, true});
    }
    
    public void data_for_ran2(){
    	this.data.addRelevantInfo("node2", new boolean[]{true});
    	this.data.addRelevantInfo("node4", new boolean[]{false});
    	this.data.addRelevantInfo("node6", new boolean[]{true});
    	this.data.addRelevantInfo("node8", new boolean[]{false});
    	this.data.addRelevantInfo("node12", new boolean[]{true});
    	this.data.addRelevantInfo("node15", new boolean[]{true});
    	this.data.addRelevantInfo("node18", new boolean[]{true});
    	this.data.addRelevantInfo("node20", new boolean[]{true});   	
    }
    
    public void data_for_ran3(){
//    	data.addRelevantInfo("node2", new boolean[]{true});
//    	data.addRelevantInfo("node4", new boolean[]{false});
//    	data.addRelevantInfo("node6", new boolean[]{true});
//    	data.addRelevantInfo("node9", new boolean[]{false});
//    	data.addRelevantInfo("node10", new boolean[]{false});
//    	data.addRelevantInfo("node12", new boolean[]{true});
//    	data.addRelevantInfo("node16", new boolean[]{false});
//    	data.addRelevantInfo("node19", new boolean[]{false});
//    	data.addRelevantInfo("node22", new boolean[]{true});
    	this.data.addRelevantInfo("node2", new boolean[]{true});
    	this.data.addRelevantInfo("node4", new boolean[]{true});
    	this.data.addRelevantInfo("node6", new boolean[]{true});
    	this.data.addRelevantInfo("node9", new boolean[]{true});
    	this.data.addRelevantInfo("node10", new boolean[]{true});
    	this.data.addRelevantInfo("node12", new boolean[]{true});
    	this.data.addRelevantInfo("node16", new boolean[]{true});
    	this.data.addRelevantInfo("node19", new boolean[]{true});
    	this.data.addRelevantInfo("node22", new boolean[]{true});
    }
    
    public void data_for_brent(){
    	this.data.addRelevantInfo("node2", new boolean[]{false, false, false, false});
    	this.data.addRelevantInfo("node4", new boolean[]{true, true, true, true});
    	this.data.addRelevantInfo("node6", new boolean[]{false, true, false, true});
    	this.data.addRelevantInfo("node8", new boolean[]{false, true, false, false});
    	this.data.addRelevantInfo("node11", new boolean[]{false, true, false, false});
    	this.data.addRelevantInfo("node14", new boolean[]{true, true, true, true});
    	this.data.addRelevantInfo("node18", new boolean[]{true, true, true, true});
    	this.data.addRelevantInfo("node19", new boolean[]{true, true, true, true});
    	this.data.addRelevantInfo("node23", new boolean[]{true, true, true, true});
    	this.data.addRelevantInfo("node26", new boolean[]{true, true, true, true});
    	this.data.addRelevantInfo("node28", new boolean[]{true, true, true, true});
    }
    
    public void data_for_bessi(){
    	this.data.addRelevantInfo("node2", new boolean[]{true, false});
    	this.data.addRelevantInfo("node4", new boolean[]{false, true});
    	this.data.addRelevantInfo("node7", new boolean[]{true, false});
    	this.data.addRelevantInfo("node9", new boolean[]{false, true});
    	this.data.addRelevantInfo("node11", new boolean[]{true, false});
    }
    
    public void data_for_bessj(){
    	this.data.addRelevantInfo("node2", new boolean[]{true, false});
    	this.data.addRelevantInfo("node5", new boolean[]{false, true});
    	this.data.addRelevantInfo("node7", new boolean[]{true, true});
    	this.data.addRelevantInfo("node9", new boolean[]{true, false});
    	this.data.addRelevantInfo("node13", new boolean[]{true, false});
    	this.data.addRelevantInfo("node15", new boolean[]{true, true});
    	this.data.addRelevantInfo("node17", new boolean[]{false, false});
    	this.data.addRelevantInfo("node20", new boolean[]{true, false});
    }
    
    public void data_for_expint(){
    	this.data.addRelevantInfo("node2", new boolean[]{true, true});
    	this.data.addRelevantInfo("node4", new boolean[]{true, false});
    	this.data.addRelevantInfo("node6", new boolean[]{false, true});
    	this.data.addRelevantInfo("node8", new boolean[]{false, true});
    	this.data.addRelevantInfo("node10", new boolean[]{false, false});
    	this.data.addRelevantInfo("node12", new boolean[]{true, true});
    	this.data.addRelevantInfo("node16", new boolean[]{false, false});
    	this.data.addRelevantInfo("node18", new boolean[]{true, false});
    	this.data.addRelevantInfo("node21", new boolean[]{true, false});
    	this.data.addRelevantInfo("node25", new boolean[]{true, true});
    }
    
    public void data_for_betacf(){
    	this.data.addRelevantInfo("node2", new boolean[]{true, true, true});	
    	this.data.addRelevantInfo("node5", new boolean[]{false, false, false});	
    	this.data.addRelevantInfo("node7", new boolean[]{true, true, true});	
    	this.data.addRelevantInfo("node10", new boolean[]{true, true, true});	
    	this.data.addRelevantInfo("node13", new boolean[]{true, true, true});	
    	this.data.addRelevantInfo("node16", new boolean[]{true, true, true});	
    	this.data.addRelevantInfo("node19", new boolean[]{true, true, true});	
    	this.data.addRelevantInfo("node21", new boolean[]{true, true, true});	
    }
    
    public void data_for_bessik(){
    	this.data.addRelevantInfo("node2", new boolean[]{true, true, false, false, false, false});	
    	this.data.addRelevantInfo("node5", new boolean[]{true, true, false, false, false, false});	
    	this.data.addRelevantInfo("node8", new boolean[]{false, false, false, false, false, false});	
    	this.data.addRelevantInfo("node10", new boolean[]{true, true, false, false, false, false});	
    	this.data.addRelevantInfo("node12", new boolean[]{true, true, false, false, false, false});	
    	this.data.addRelevantInfo("node15", new boolean[]{false, true, false, false, false, false});	
    	this.data.addRelevantInfo("node18", new boolean[]{true, false, false, false, false, false});	
    	this.data.addRelevantInfo("node20", new boolean[]{false, false, false, false, false, false});	
    	this.data.addRelevantInfo("node22", new boolean[]{true, true, false, false, false, false});	
    	this.data.addRelevantInfo("node24", new boolean[]{true, true, false, false, false, false});	
    	this.data.addRelevantInfo("node28", new boolean[]{false, false, false, false, false, false});	
    	this.data.addRelevantInfo("node30", new boolean[]{true, true, false, false, false, false});	
    	this.data.addRelevantInfo("node32", new boolean[]{true, true, false, false, false, false});	
    	this.data.addRelevantInfo("node36", new boolean[]{false, true, false, false, false, false});	
    }
}
