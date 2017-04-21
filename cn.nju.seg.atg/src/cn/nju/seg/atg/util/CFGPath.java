package cn.nju.seg.atg.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.nju.seg.atg.model.Condition;
import cn.nju.seg.atg.model.Coodinate;
import cn.nju.seg.atg.model.Interval;
import cn.nju.seg.atg.model.SimpleCFGNode;
import cn.nju.seg.atg.model.constraint.BinaryExpression;
import cn.nju.seg.atg.parse.CFGBuilder;
import cn.nju.seg.atg.parse.LineChart;
import cn.nju.seg.atg.parse.TestBuilder;

/**
 * 
 * @author zy
 * @author Zhang Yifan
 */
public class CFGPath {

  /**
   * 一条路径
   */
  private ArrayList<SimpleCFGNode> path;

  /**
   * 当前路径的最优输入参数
   * 即该输入参数所能覆盖的节点数最多
   */
  private double[] optimalParams;

  /**
   * 当前最优输入参数覆盖到的节点数
   */
  private double numOfCoveredNodes;

  /**
   * 最优输入参数覆盖到的最远节点名称
   */
  private String endCoveredNodeName;

  /**
   * 判断当前路径是否已被覆盖
   * true-是 false-否
   */
  private boolean isCovered;

  /**
   * 无参构造函数
   */
  public CFGPath() {
    this.path = new ArrayList<SimpleCFGNode>();
    this.numOfCoveredNodes = 0;
    this.isCovered = false;
  }

  /**
   * 获取当前最优输入参数
   * 
   * @return
   */
  public double[] getOptimalParams() {
    return this.optimalParams;
  }

  /**
   * 设置当前最优输入参数
   * 
   * @param params
   */
  public void setOptimalParams(double[] params) {
    this.optimalParams = new double[params.length];
    for (int i = 0; i < params.length; i++) {
      this.optimalParams[i] = params[i];
    }
  }

  /**
   * 设置最优参数覆盖到的节点数
   * 
   * @return
   */
  public double getNumOfCoveredNodes() {
    return this.numOfCoveredNodes;
  }

  /**
   * 获取最优参数覆盖到的节点数
   * 
   * @param numOfCoveredNodes
   */
  public void setNumOfCoveredNodes(double numOfCoveredNodes) {
    this.numOfCoveredNodes = numOfCoveredNodes;
  }

  /**
   * 获取最优参数覆盖到的最远节点的名称
   * 
   * @return
   */
  public String getEndCoveredNodeName() {
    return this.endCoveredNodeName;
  }

  /**
   * 设置最优参数覆盖到的最远节点的名称
   * 
   * @param endCoveredNode
   */
  public void setEndCoveredNodeName(String endCoveredNodeName) {
    this.endCoveredNodeName = endCoveredNodeName;
  }

  /**
   * 获取覆盖结果
   * 
   * @return
   */
  public boolean isCovered() {
    return this.isCovered;
  }

  /**
   * 设置覆盖结果
   * 
   * @param isCovered
   */
  public void setCovered(boolean isCovered) {
    this.isCovered = isCovered;
  }

  /**
   * 获取分歧节点
   * 
   * @return divergenceNode
   */
  public SimpleCFGNode getDivergenceNode() {
    SimpleCFGNode divergenceNode = null;

    for (SimpleCFGNode node : this.path) {
      if (node.isBranchNode()) {
        divergenceNode = node;
      }
    }
    return divergenceNode;
  }

  /**
   * 计算当前路径被覆盖的长度
   */
  public double coveredPathLength() {
    SimpleCFGNode divergenceNode = this.getDivergenceNode();
    if (divergenceNode != null && divergenceNode.isAndCompositeConstraint()) {
      return this.path.size() - 1 + divergenceNode.coveredNodeLength();
    } else {
      return this.path.size();
    }
  }

  /**
   * 获取路径的覆盖度评估值
   * PCE = DC + max{1/Nc * (CC + 1/1+abs(EP))}
   * 
   * @return
   */
  public double pathCoverageEvaluation() {
    double PCE = 0.0;
    SimpleCFGNode divergenceNode = null;

    for (SimpleCFGNode node : this.path) {
      if (node.isBranchNode()) {
        PCE += 1;
        divergenceNode = node;
      }
    }
    if (divergenceNode == null)
      return 0;
    return PCE - 1 + divergenceNode.getOptimalInput();
  }

  /**
   * 获取节点在路径中的编号
   * 
   * @param name
   * @return
   */
  public int getNodeIndex(String name) {
    for (int i = 0; i < this.path.size(); i++) {
      if (this.path.get(i).getName().equals(name)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * 复制当前路径
   * 
   * @return 一条路径
   */
  public CFGPath clonePath() {
    CFGPath path = new CFGPath();

    int pathSize = this.path.size();
    for (int i = 0; i < pathSize; i++) {
      SimpleCFGNode snode = this.path.get(i).cloneNode();
      path.addNode(snode);
    }

    if (this.optimalParams != null)
      path.setOptimalParams(this.optimalParams);
    path.setCovered(this.isCovered);
    path.setEndCoveredNodeName(this.endCoveredNodeName);
    path.setNumOfCoveredNodes(this.numOfCoveredNodes);

    return path;
  }

  /**
   * 添加一个SimpleCFGNode节点
   * 
   * @param node
   */
  public void addNode(SimpleCFGNode node) {
    this.path.add(node);
  }

  public void addNodeFromBegin(SimpleCFGNode node) {
    this.path.add(0, node);
  }

  /**
   * 按参数添加普通节点
   * 
   * @param nodeName
   * @param nodeType
   */
  public void addNormalNode(String nodeName, int nodeType) {
    int pathSize = this.path.size();
    int index = -1;
    for (int i = 0; i < pathSize; i++) {
      if (this.path.get(i).getName().equals(nodeName)) {
        index = i;
        break;
      }
    }
    if (index == -1 || index == pathSize) {
      SimpleCFGNode tempNode = new SimpleCFGNode();
      tempNode.setName(nodeName);
      tempNode.setType(nodeType);
      this.path.add(tempNode);
    }
  }

  /**
   * 按参数添加分支节点
   * 
   * @param nodeName
   * @param nodeType
   * @param constraintExpression
   * @param c
   */
  public void addBranchNode(String nodeName, int nodeType, String atomicConstraintExpression, Coodinate c) {
    int pathSize = this.path.size();
    // 判断nodeName是否已在path中
    int index = -1;
    for (int i = 0; i < pathSize; i++) {
      if (this.path.get(i).getName().equals(nodeName)) {
        index = i;
        break;
      }
    }
    // 根据index的情况，在path中插入node
    if (index == -1) {
      SimpleCFGNode tempNode = new SimpleCFGNode();
      tempNode.setName(nodeName);
      tempNode.setType(nodeType);
      tempNode.addValue(atomicConstraintExpression, c);
      this.path.add(tempNode);
    } else if (index >= 0 && index < pathSize) {
      for (int i = 0; i < pathSize; i++) {
        if (this.path.get(i).getName().equals(nodeName)) {
          this.path.get(i).addValue(atomicConstraintExpression, c);
        }
      }
    }
  }

  /**
   * 找出当前路径覆盖到目标路径的部分
   * 
   * @param targetPath
   * @return 一条路径片段
   */
  public CFGPath getCoveredPath(CFGPath targetPath, double[] parameters) {
    CFGPath coveredPath = new CFGPath();

    SimpleCFGNode tempNode = new SimpleCFGNode();

    int pathSize = this.path.size();
    int targetPathSize = targetPath.getPath().size();
    for (int i = 0; i < pathSize; i++) {
      if (i < targetPathSize) {
        if (this.path.get(i).getName().equals(targetPath.getPath().get(i).getName())) {
          tempNode = this.path.get(i);
          tempNode.setConstraint(targetPath.getPath().get(i).getConstraint());
          tempNode.setTrue(targetPath.getPath().get(i).isTrue());
          tempNode.setType(targetPath.getPath().get(i).getType());
          coveredPath.addNode(tempNode);
        } else {
          break;
        }
      }
    }

    coveredPath.setEndCoveredNodeName(coveredPath.getEndNodeName());
    coveredPath.setNumOfCoveredNodes(coveredPath.coveredPathLength());
    coveredPath.setOptimalParams(parameters);

    return coveredPath;
  }

  /**
   * 找出当前路径覆盖到目标路径片段的部分
   * 
   * @param targetPath
   * @return 一条路径片段
   */
  public CFGPath getCoveredPathFragment(CFGPath targetPath) {
    CFGPath coveredPath = new CFGPath();
    SimpleCFGNode tempNode;
    boolean isInFunctionCall = false;

    int pathSize = this.path.size();
    int j = 0;
    for (int i = 0; i < pathSize; i++) {
      if (this.path.get(i).getName().equals(targetPath.getPath().get(j).getName())) {
        tempNode = this.path.get(i);
        tempNode.setConstraint(targetPath.getPath().get(j).getConstraint());
        tempNode.setTrue(targetPath.getPath().get(j).isTrue());
        tempNode.setType(targetPath.getPath().get(j).getType());
        coveredPath.addNode(tempNode);
        j++;
        if (j == targetPath.getPath().size())
          break;
      } else if (isInFunctionCall || this.path.get(i).getName().startsWith("entry")) {
        isInFunctionCall = true;
      } else if (this.path.get(i).getName().startsWith("exit")) {
        isInFunctionCall = false;
      } else {
        break;
      }
    }

    coveredPath.setEndCoveredNodeName(coveredPath.getEndNodeName());
    coveredPath.setNumOfCoveredNodes(coveredPath.coveredPathLength());

    return coveredPath;

  }

  /**
   * 更新覆盖到目标路径的最长路径片段(更新值对)
   * 
   * @param newPath
   */
  public void update(CFGPath newPath, double[] parameters) {
    int pathSize = this.path.size();
    int newPathSize = newPath.getPath().size();
    SimpleCFGNode tempNode;
    List<List<BinaryExpression>> atomicConstraintGroups;
    String atomicConstraintStr;
    List<Coodinate> tempCoodinateList;
    if (this.pathCoverageEvaluation() < newPath.pathCoverageEvaluation()) {
      // FIXME @since 0.1, In some cases, `pathSize` > `newPathSize`, and `newPath.getPath().get(i)` out of bound
      // is it right to simply change `i < pathSize` to `i < pathSize && i < newPathSize` ?
      for (int i = 0; i < pathSize && i < newPathSize ; i++) {
        tempNode = newPath.getPath().get(i);
        if (tempNode.isBranchNode()) {
          atomicConstraintGroups = newPath.getPath().get(i).getConstraint().getAtomicConstraintGroups();
          for (int j = 0; j < atomicConstraintGroups.size(); j++) {
            for (int k = 0; k < atomicConstraintGroups.get(j).size(); k++) {
              atomicConstraintStr = atomicConstraintGroups.get(j).get(k).getId();
              if (tempNode.getValues().containsKey(atomicConstraintStr)) {
                tempCoodinateList = tempNode.getValues().get(atomicConstraintStr);
                for (int m = 0; m < tempCoodinateList.size(); m++) {
                  this.addBranchNode(tempNode.getName(), tempNode.getType(), atomicConstraintStr, tempCoodinateList.get(m));
                }
              }
            }
          }
        }
      }
      for (int i = pathSize; i < newPathSize; i++) {
        this.addNode(newPath.getPath().get(i));
      }

      this.setOptimalParams(parameters);
      this.setEndCoveredNodeName(newPath.getEndNodeName());
      this.setNumOfCoveredNodes(this.coveredPathLength());
    } else {
      for (int i = 0; i < newPathSize; i++) {
        tempNode = newPath.getPath().get(i);
        if (tempNode.isBranchNode()) {
          atomicConstraintGroups = newPath.getPath().get(i).getConstraint().getAtomicConstraintGroups();
          for (int j = 0; j < atomicConstraintGroups.size(); j++) {
            for (int k = 0; k < atomicConstraintGroups.get(j).size(); k++) {
              atomicConstraintStr = atomicConstraintGroups.get(j).get(k).getId();
              if (tempNode.getValues().containsKey(atomicConstraintStr)) {
                tempCoodinateList = tempNode.getValues().get(atomicConstraintStr);
                for (int m = 0; m < tempCoodinateList.size(); m++) {
                  this.addBranchNode(tempNode.getName(), tempNode.getType(), atomicConstraintStr, tempCoodinateList.get(m));
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * 返回末尾节点的名称
   * 
   * @return 节点名称
   */
  public String getEndNodeName() {
    int pathSize = this.path.size();
    return this.path.get(pathSize - 1).getName();
  }

  /**
   * 判断当前路径是否是目标路径
   * 
   * @param targetPath
   * @return true|false
   */
  public boolean isTargetPath(CFGPath targetPath) {
    return this.isEqual(targetPath.getPath());
  }

  /**
   * 判断path是否于myPath相同
   * 
   * @param path
   * @return
   */
  public boolean isEqual(List<SimpleCFGNode> myPath) {
    if (this.path.size() != myPath.size()) {
      return false;
    } else {
      int size = this.path.size();
      for (int i = 0; i < size; i++) {
        if (!this.path.get(i).isEqual(myPath.get(i))) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * 获取可用取值区间,路径上的各个分支可并行执行
   * 
   * @return 可用取值区间
   */
  // public List<Interval> getEffectiveIntevalList()
  // {
  // List<Interval> effectiveIntervalList = new ArrayList<Interval>();
  // //初始化effectiveIntervalList为全集
  // Interval initialInterval = Builder.maxInterval();
  // effectiveIntervalList.add(initialInterval);
  //
  // CompletionService<List<Interval>> service = new ExecutorCompletionService<List<Interval>>(Builder.exec);
  // int taskNum = 0;
  //
  // int pathSize = this.path.size();
  // for (int i=0; i<pathSize; i++)
  // {
  // if (this.path.get(i).isBranchNode())
  // {
  // BranchNodeRunnable task = new BranchNodeRunnable(this.path.get(i));
  // service.submit(task);
  // taskNum ++;
  // }
  // }
  // for (int i = 0; i < taskNum; i++)
  // {
  // try
  // {
  // effectiveIntervalList = Interval.getIntersection(effectiveIntervalList, service.take().get());
  // }
  // catch (InterruptedException e)
  // {
  // e.printStackTrace();
  // }
  // catch (ExecutionException e)
  // {
  // e.printStackTrace();
  // }
  // }
  //
  // return effectiveIntervalList;
  // }

  public List<Interval> getEffectiveIntevalList() {
    List<Interval> effectiveIntervalList = new ArrayList<Interval>();
    // 初始化effectiveIntervalList为全集
    Interval initialInterval = ATG.maxInterval();
    effectiveIntervalList.add(initialInterval);

    List<Interval> tempIntervalList;
    int pathSize = this.path.size();

    for (int i = 0; i < pathSize; i++) {
      if (this.path.get(i).isBranchNode()) {
        tempIntervalList = this.path.get(i).getEffectiveIntervalList();
        List<Interval> temp = Interval.getIntersection(effectiveIntervalList, tempIntervalList);
        effectiveIntervalList = temp;
      }
    }

    return effectiveIntervalList;
  }

  /**
   * 获取新的可用输入数据集合
   * 
   * @return 输入数据集合
   */
  public List<Double> getNewParameterList(int paramIndex) {
    List<Double> newParameterList = new ArrayList<Double>();
    TestBuilder.paramsInExtensionCord = new ArrayList<Double>();
    List<Interval> effectiveIntervalList = this.getEffectiveIntevalList();

    if (effectiveIntervalList != null && effectiveIntervalList.size() != 0) {
      int effectiveIntervalListSize = effectiveIntervalList.size();
      for (int i = 0; i < effectiveIntervalListSize; i++) {
        double newParameter = effectiveIntervalList.get(i).getNewCoodinate();
        newParameterList.add(newParameter);
      }
    }

    double leftBoundary = 0, rightBoundary = 0;
    boolean left = false, right = false;
    for (int i = this.path.size() - 1; i >= 0; i--) {
      if (this.path.get(i).isBranchNode()) {
        SimpleCFGNode node = this.path.get(i);
        node.predictInterval(node.isEqual(this.getDivergenceNode()));
        if (!left) {
          if (node.isLeftTransboundary()) {
            left = true;
            leftBoundary = node.getLeftBoundary();
          }
        }
        if (!right) {
          if (node.isRightTransboundary()) {
            right = true;
            rightBoundary = node.getRightBoundary();
          }
        }
        node.restore();
        if (left && right) {
          break;
        }
      }
    }
    // 将拟合线段延长线与坐标轴交点集合加入新预测变量集合中
    if (TestBuilder.paramsInExtensionCord.size() != 0) {
      for (double param : TestBuilder.paramsInExtensionCord) {
        newParameterList.add(param);
      }
    }

    // if(left && leftBoundary >= ATG.LEFT_BOUNDARY[paramIndex]){
    if (left) {
      // 将零点附近一个小邻域内的随机值作为预测边界
      // double temp = leftBoundary + ICLFF_ATG.PREDICT_BOUNDARY;
      // double tempBoundary = (temp < Builder.leftSearchBoundary) ? temp : Builder.leftSearchBoundary;
      // double random = StaticMethod.getGoldenSectionPoint(leftBoundary - ICLFF_ATG.PREDICT_BOUNDARY, tempBoundary);
      // newParameterList.add(random);
      // Builder.autoIncreasedParameterList.add(random);
      newParameterList.add(leftBoundary);
      double predictBoundary = TestBuilder.leftSearchBoundary - leftBoundary;
      // 预测区间跨度过大
      if (predictBoundary > ATG.MAX_PREDICT_BOUNDARY) {
        double anotherRandom = MathFunc.getGoldenSectionPoint(leftBoundary - ATG.PREDICT_BOUNDARY, leftBoundary + ATG.PREDICT_BOUNDARY);
        newParameterList.add(anotherRandom);
        TestBuilder.autoIncreasedParameterList.add(anotherRandom);
      } else if (predictBoundary < ATG.MIN_PREDICT_BOUNDARY) {
        double autoIncreased = TestBuilder.leftSearchBoundary - Math.random() * ATG.MAX_STEP;
        newParameterList.add(autoIncreased);
        TestBuilder.autoIncreasedParameterList.add(autoIncreased);
      }
    } else {
      double autoIncreased = TestBuilder.leftSearchBoundary - Math.random() * ATG.MAX_STEP;
      // if(autoIncreased>= ATG.LEFT_BOUNDARY[paramIndex]){
      newParameterList.add(autoIncreased);
      TestBuilder.autoIncreasedParameterList.add(autoIncreased);
      // }
    }

    // if(right && rightBoundary<=ATG.RIGHT_BOUNDARY[paramIndex]){
    if (right) {
      // 将零点附近一个小邻域内的随机值作为预测边界
      // double temp = rightBoundary - ICLFF_ATG.PREDICT_BOUNDARY;
      // double tempBoundary = (temp > Builder.rightSearchBoundary) ? temp : Builder.rightSearchBoundary;
      // double random = StaticMethod.getGoldenSectionPoint(tempBoundary, rightBoundary + ICLFF_ATG.PREDICT_BOUNDARY);
      // newParameterList.add(random);
      // Builder.autoIncreasedParameterList.add(random);
      newParameterList.add(rightBoundary);
      double predictBoundary = rightBoundary - TestBuilder.rightSearchBoundary;
      // 预测区间跨度过大
      if (predictBoundary > ATG.MAX_PREDICT_BOUNDARY) {
        double anotherRandom = MathFunc.getGoldenSectionPoint(rightBoundary - ATG.PREDICT_BOUNDARY, rightBoundary + ATG.PREDICT_BOUNDARY);
        newParameterList.add(anotherRandom);
        TestBuilder.autoIncreasedParameterList.add(anotherRandom);
      } else if (predictBoundary < ATG.MIN_PREDICT_BOUNDARY) {
        double autoIncreased = TestBuilder.rightSearchBoundary + Math.random() * ATG.MAX_STEP;
        newParameterList.add(autoIncreased);
        TestBuilder.autoIncreasedParameterList.add(autoIncreased);
      }
    } else {
      double autoIncreased = TestBuilder.rightSearchBoundary + Math.random() * ATG.MAX_STEP;
      // if(autoIncreased<=ATG.RIGHT_BOUNDARY[paramIndex]){
      newParameterList.add(autoIncreased);
      TestBuilder.autoIncreasedParameterList.add(autoIncreased);
      // }
    }
    if (CFGBuilder.parameterTypes[paramIndex].equals("int")) {
      List<Double> tempList = new ArrayList<Double>();
      for (double newParameter : newParameterList) {
        double temp = (double) Math.round(newParameter);
        if (!tempList.contains(temp)) {
          tempList.add(temp);
        }
      }
      newParameterList = tempList;
    }

    return newParameterList;
  }

  public ArrayList<SimpleCFGNode> getPath() {
    return this.path;
  }

  public void setPath(ArrayList<SimpleCFGNode> path) {
    this.path = path;
  }

  /**
   * 显示该路径上所有分支节点的线性拟合函数
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void showCLF(int pathIndex, double effectiveValue) {
    LineChart chart = new LineChart();
    int pathSize = this.path.size();
    Iterator iterator = null;
    for (int i = 0; i < pathSize; i++) {
      if (this.path.get(i).getType() == ConstantValue.BRANCH_NODE) {
        iterator = this.path.get(i).getValues().entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry entry = (Map.Entry) iterator.next();
          chart.showLineChart((pathIndex + 1) + "." + i + " " + (String) entry.getKey(), (List<Coodinate>) entry.getValue(), TestBuilder.autoIncreasedParameterList, effectiveValue);
        }
      }
    }
  }

  /**
   * 显示该路径上表达式为constraint的分支节点的线性拟合函数
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void showCLF(int pathIndex, double effectiveValue, String constraint) {
    LineChart chart = new LineChart();
    int pathSize = this.path.size();
    Iterator iterator = null;
    for (int i = 0; i < pathSize; i++) {
      if (this.path.get(i).getType() == ConstantValue.BRANCH_NODE) {
        iterator = this.path.get(i).getValues().entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry entry = (Map.Entry) iterator.next();
          if (constraint.equals((String) entry.getKey())) {
            chart.showLineChart((pathIndex + 1) + "." + i + " " + (String) entry.getKey(), (List<Coodinate>) entry.getValue(), TestBuilder.autoIncreasedParameterList, effectiveValue);
          }
        }
      }
    }
  }

  public List<Double> getNewParameterListForCondition(int paramIndex, Condition targetCondition, boolean truthValue) {
    List<Double> newParameterList = new ArrayList<Double>();
    TestBuilder.paramsInExtensionCord = new ArrayList<Double>();
    List<Interval> effectiveIntervalList = this.getEffectiveIntevalList(targetCondition, truthValue);

    if (effectiveIntervalList != null && effectiveIntervalList.size() != 0) {
      int effectiveIntervalListSize = effectiveIntervalList.size();
      for (int i = 0; i < effectiveIntervalListSize; i++) {
        double newParameter = effectiveIntervalList.get(i).getNewCoodinate();
        newParameterList.add(newParameter);
      }
    }

    double leftBoundary = 0, rightBoundary = 0;
    boolean left = false, right = false;
    for (int i = this.path.size() - 1; i >= 0; i--) {
      if (this.path.get(i).isBranchNode()) {
        SimpleCFGNode node = this.path.get(i);
        if (node.isEqual(this.getDivergenceNode())) {
          node.predictInterval(false);
        } else {
          node.predictIntervalForCondition(targetCondition.getConstraint(), truthValue);
        }
        if (!left) {
          if (node.isLeftTransboundary()) {
            left = true;
            leftBoundary = node.getLeftBoundary();
          }
        }
        if (!right) {
          if (node.isRightTransboundary()) {
            right = true;
            rightBoundary = node.getRightBoundary();
          }
        }
        node.restore();
        if (left && right) {
          break;
        }
      }
    }
    // 将拟合线段延长线与坐标轴交点集合加入新预测变量集合中
    if (TestBuilder.paramsInExtensionCord.size() != 0) {
      for (double param : TestBuilder.paramsInExtensionCord) {
        newParameterList.add(param);
      }
    }

    // if(left && leftBoundary >= ATG.LEFT_BOUNDARY[paramIndex]){
    if (left) {
      newParameterList.add(leftBoundary);
      double predictBoundary = TestBuilder.leftSearchBoundary - leftBoundary;
      // 预测区间跨度过大
      if (predictBoundary > ATG.MAX_PREDICT_BOUNDARY) {
        double anotherRandom = MathFunc.getGoldenSectionPoint(leftBoundary - ATG.PREDICT_BOUNDARY, leftBoundary + ATG.PREDICT_BOUNDARY);
        newParameterList.add(anotherRandom);
        TestBuilder.autoIncreasedParameterList.add(anotherRandom);
      } else if (predictBoundary < ATG.MIN_PREDICT_BOUNDARY) {
        double autoIncreased = TestBuilder.leftSearchBoundary - Math.random() * ATG.MAX_STEP;
        newParameterList.add(autoIncreased);
        TestBuilder.autoIncreasedParameterList.add(autoIncreased);
      }
    } else {
      double autoIncreased = TestBuilder.leftSearchBoundary - Math.random() * ATG.MAX_STEP;
      // if(autoIncreased>= ATG.LEFT_BOUNDARY[paramIndex]){
      newParameterList.add(autoIncreased);
      TestBuilder.autoIncreasedParameterList.add(autoIncreased);
      // }
    }

    // if(right && rightBoundary<=ATG.RIGHT_BOUNDARY[paramIndex]){
    if (right) {
      newParameterList.add(rightBoundary);
      double predictBoundary = rightBoundary - TestBuilder.rightSearchBoundary;
      // 预测区间跨度过大
      if (predictBoundary > ATG.MAX_PREDICT_BOUNDARY) {
        double anotherRandom = MathFunc.getGoldenSectionPoint(rightBoundary - ATG.PREDICT_BOUNDARY, rightBoundary + ATG.PREDICT_BOUNDARY);
        newParameterList.add(anotherRandom);
        TestBuilder.autoIncreasedParameterList.add(anotherRandom);
      } else if (predictBoundary < ATG.MIN_PREDICT_BOUNDARY) {
        double autoIncreased = TestBuilder.rightSearchBoundary + Math.random() * ATG.MAX_STEP;
        newParameterList.add(autoIncreased);
        TestBuilder.autoIncreasedParameterList.add(autoIncreased);
      }
    } else {
      double autoIncreased = TestBuilder.rightSearchBoundary + Math.random() * ATG.MAX_STEP;
      // if(autoIncreased<=ATG.RIGHT_BOUNDARY[paramIndex]){
      newParameterList.add(autoIncreased);
      TestBuilder.autoIncreasedParameterList.add(autoIncreased);
      // }
    }
    if (CFGBuilder.parameterTypes[paramIndex].equals("int")) {
      List<Double> tempList = new ArrayList<Double>();
      for (double newParameter : newParameterList) {
        double temp = (double) Math.round(newParameter);
        if (!tempList.contains(temp)) {
          tempList.add(temp);
        }
      }
      newParameterList = tempList;
    }

    return newParameterList;

  }

  private List<Interval> getEffectiveIntevalList(Condition targetCondition, boolean truthValue) {
    List<Interval> effectiveIntervalList = new ArrayList<Interval>();
    // 初始化effectiveIntervalList为全集
    Interval initialInterval = ATG.maxInterval();
    effectiveIntervalList.add(initialInterval);

    List<Interval> tempIntervalList;
    int pathSize = this.path.size();

    for (int i = 0; i < pathSize; i++) {
      if (this.path.get(i).isBranchNode()) {
        if (this.path.get(i).getName() == targetCondition.getNodeId()) {
          tempIntervalList = this.path.get(i).getEffectiveIntervalListForCondition(targetCondition.getConstraint(), truthValue);
        } else {
          tempIntervalList = this.path.get(i).getEffectiveIntervalList();
        }
        List<Interval> temp = Interval.getIntersection(effectiveIntervalList, tempIntervalList);
        effectiveIntervalList = temp;
      }
    }

    return effectiveIntervalList;
  }
}
