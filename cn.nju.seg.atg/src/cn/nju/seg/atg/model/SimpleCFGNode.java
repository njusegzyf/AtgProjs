package cn.nju.seg.atg.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CancellationException;

import cn.nju.seg.atg.model.constraint.BinaryExpression;
import cn.nju.seg.atg.model.constraint.Operator;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.ATG;
import cn.nju.seg.atg.util.ConstantValue;

/**
 * 简单CFG节点
 * 
 * @author zy
 */
public class SimpleCFGNode {
  /**
   * 节点名称
   */
  private String name;

  /**
   * 节点类型
   */
  private int type;

  /**
   * 当前约束条件的子约束条件分组集合
   */
  private Constraint constraint;

  /**
   * 节点运行时CLF函数值
   * <p>
   * 每个复合约束条件的子约束条件都对应一组CLF函数值
   * <p>
   * key表示子约束的id
   */
  private HashMap<String, List<Coodinate>> values;

  /**
   * 分支节点在路径中的约束谓词是否为真
   */
  private boolean isTrue;

  /**
   * 左边界
   * <p>
   * 最左区间零点在当前搜索域左边时，将零点作为左预测边界
   */
  private double leftBoundary;

  /**
   * 右边界
   * <p>
   * 最右区间零点在当前搜索域右边时，将零点作为右预测边界
   */
  private double rightBoundary;

  /**
   * 判断是否越界
   */
  private boolean isLeftTransboundary, isRightTransboundary;

  /**
   * 最优解
   */
  private double optimal;

  private int id = -1;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /**
   * 无参初始化
   */
  public SimpleCFGNode() {
    this(null, 0, null, true);
  }

  /**
   * 带参构造函数
   * 
   * @param nodeName
   * @param nodeType
   * @param constraint
   * @param isTrue
   */
  public SimpleCFGNode(String nodeName, int nodeType, Constraint constraint, boolean isTrue) {
    this.name = nodeName;
    this.type = nodeType;
    this.constraint = constraint;
    this.values = new HashMap<String, List<Coodinate>>();
    this.isTrue = isTrue;
    this.isLeftTransboundary = false;
    this.isRightTransboundary = false;
  }

  public SimpleCFGNode(String nodeName, int nodeType, Constraint constraint, boolean isTrue, int id) {
    this.name = nodeName;
    this.type = nodeType;
    this.constraint = constraint;
    this.values = new HashMap<String, List<Coodinate>>();
    this.isTrue = isTrue;
    this.id = id;
    this.isLeftTransboundary = false;
    this.isRightTransboundary = false;
  }

  /**
   * 复制当前节点
   * 
   * @return 一个节点
   */
  public SimpleCFGNode cloneNode() {
    SimpleCFGNode node = new SimpleCFGNode(this.name, this.type, this.constraint, this.isTrue, this.id);
    return node;
  }

  /**
   * 为子约束条件添加运行时CLF函数值
   * 
   * @param constraintExpression
   * @param c
   */
  public void addValue(String atomicConstraintExpression, Coodinate c) {
    if (this.values.containsKey(atomicConstraintExpression)) {
      insertCoodinate2SortedList(c, this.values.get(atomicConstraintExpression));
    } else {
      List<Coodinate> list = new ArrayList<Coodinate>();
      list.add(c);
      this.values.put(atomicConstraintExpression, list);
    }
  }

  /**
   * 以有序方式将c插入到list中
   * list中元素以c.x从小到大排序
   * 
   * @param c
   * @param list
   */
  private static void insertCoodinate2SortedList(Coodinate c, List<Coodinate> list) {
    if (list != null) {
      int listSize = list.size();
      // 判断当前c在list中的位置
      int i = 0;
      for (i = 0; i < listSize; i++) {
        if (c.getX() <= list.get(i).getX()) {
          break;
        }
      }
      // 根据c在list中的位置添加c到list
      if (i == listSize) {
        list.add(c);
      }
      // 具有相同X坐标值的坐标点只保留第一个(2014-6-3)
      else if (i >= 0 && i < listSize && (list.get(i).getX() != c.getX())) {
        Coodinate tempCoodinate = list.get(listSize - 1);
        list.add(tempCoodinate);
        for (int j = (listSize - 1); j > i; j--) {
          tempCoodinate = list.get(j - 1);
          list.set(j, tempCoodinate);
        }
        list.set(i, c);
      }
    } else {
      list = new ArrayList<Coodinate>();
      list.add(c);
    }
  }

  /**
   * 判断该节点是否为分支节点
   * 
   * @return true|false
   */
  public boolean isBranchNode() {
    if (this.type == ConstantValue.BRANCH_DO || this.type == ConstantValue.BRANCH_FOR
        || this.type == ConstantValue.BRANCH_IF || this.type == ConstantValue.BRANCH_WHILE) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 若当前分支为分歧节点，求取分歧子约束的预测区间
   * 
   * @param isDivergenceNode
   */
  public void predictInterval(boolean isDivergenceNode) {
    List<List<BinaryExpression>> atomicConstraintGroups = this.constraint.getAtomicConstraintGroups();
    List<Coodinate> tempCoodinateList;
    CLFF clff;

    if (this.isTrue) {
      // 当前约束条件不包含||
      // 对由&&连接的各个子约束模块，只考虑分歧子约束
      if (atomicConstraintGroups.size() == 1) {
        int groupSize = atomicConstraintGroups.get(0).size();
        // 从后向前定位分歧子约束
        for (int i = groupSize - 1; i >= 0; i--) {
          tempCoodinateList = this.values.get(atomicConstraintGroups.get(0).get(i).getId());
          if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
            clff = new CLFF(tempCoodinateList, atomicConstraintGroups.get(0).get(i).getOp());
            predictNewBoundary(clff);
            if (isDivergenceNode) {
              TestBuilder.paramsInExtensionCord.addAll(clff.getParamsInExtensionCord());
            }
            break;
          }
        }
      } else {// 当前约束条件包含||
        // 对由||连接的各个子约束模块，只将距离当前搜索区域最近的点作为预测变量
        int groupsSize = atomicConstraintGroups.size();
        for (int i = 0; i < groupsSize; i++) {
          int groupSize = atomicConstraintGroups.get(i).size();
          for (int j = groupSize - 1; j >= 0; j--) {
            tempCoodinateList = this.values.get(atomicConstraintGroups.get(i).get(j).getId());
            if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
              clff = new CLFF(tempCoodinateList, atomicConstraintGroups.get(i).get(j).getOp());
              predictNewBoundary(clff);
              if (isDivergenceNode) {
                TestBuilder.paramsInExtensionCord.addAll(clff.getParamsInExtensionCord());
              }
              break;
            }
          }
        }
      }
    } else {
      // 当前约束条件不包含||
      // 对由&&连接的各个子约束模块取反后等同"||"处理
      if (atomicConstraintGroups.size() == 1) {
        int groupSize = atomicConstraintGroups.get(0).size();
        for (int i = 0; i < groupSize; i++) {
          tempCoodinateList = this.values.get(atomicConstraintGroups.get(0).get(i).getId());
          if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
            Operator comparisonOperator = Operator.getReverseOp(atomicConstraintGroups.get(0).get(i).getOp());
            clff = new CLFF(tempCoodinateList, comparisonOperator);
            predictNewBoundary(clff);
            if (isDivergenceNode) {
              TestBuilder.paramsInExtensionCord.addAll(clff.getParamsInExtensionCord());
            }
          }
        }
      } else {// 当前约束条件包含||
        // 对由||连接的各个子约束模块取反后等同"&&"处理
        boolean predicted = false;
        int groupsSize = atomicConstraintGroups.size();
        for (int i = groupsSize - 1; i >= 0; i--) {
          int groupSize = atomicConstraintGroups.get(i).size();
          for (int j = 0; j < groupSize; j++) {
            tempCoodinateList = this.values.get(atomicConstraintGroups.get(i).get(j).getId());
            if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
              Operator comparisonOperator = Operator.getReverseOp(atomicConstraintGroups.get(i).get(j).getOp());
              clff = new CLFF(tempCoodinateList, comparisonOperator);
              predictNewBoundary(clff);
              if (isDivergenceNode) {
                TestBuilder.paramsInExtensionCord.addAll(clff.getParamsInExtensionCord());
              }
              predicted = true;
            }
          }
          if (predicted) {
            break;
          }
        }
      }
    }
  }

  public void predictIntervalForCondition(BinaryExpression targetCondition, boolean truthValue) {
    List<List<BinaryExpression>> atomicConstraintGroups = this.constraint.getAtomicConstraintGroups();
    List<Coodinate> tempCoodinateList;
    CLFF clff;
    // 获取目标条件在主析取范式中的位置
    int outIndex = -1, inIndex = -1;
    List<List<BinaryExpression>> atomicGroups = this.getConstraint().getAtomicConstraintGroups();
    for (int i = 0; i < atomicGroups.size(); i++) {
      for (int j = 0; j < atomicGroups.get(i).size(); j++) {
        if (atomicGroups.get(i).get(j).equals(targetCondition)) {
          outIndex = i;
          inIndex = atomicGroups.get(i).indexOf(targetCondition);
          break;
        }
      }
      if (outIndex != -1)
        break;
    }

    tempCoodinateList = this.values.get(targetCondition.getId());
    if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
      Operator op = truthValue ? targetCondition.getOp() : Operator.getReverseOp(targetCondition.getOp());
      clff = new CLFF(tempCoodinateList, op);
      predictNewBoundary(clff);
      TestBuilder.paramsInExtensionCord.addAll(clff.getParamsInExtensionCord());
    }
    if (this.isLeftTransboundary && this.isRightTransboundary)
      return;
    for (int h = inIndex - 1; h > -1; h--) {// 目标条件所在的块中位于目标条件前面的条件取值为真
      tempCoodinateList = this.values.get(atomicConstraintGroups.get(outIndex).get(h).getId());
      if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
        clff = new CLFF(tempCoodinateList, atomicConstraintGroups.get(outIndex).get(h).getOp());
        clff.getNewBoundary();
        if (!this.isLeftTransboundary && clff.isLeftTransboundary()) {
          this.leftBoundary = clff.getLeftBoundary();
          this.isLeftTransboundary = true;
        }
        if (!this.isRightTransboundary && clff.isRightTransboundary()) {
          this.rightBoundary = clff.getRightBoundary();
          this.isRightTransboundary = true;
        }
        break;
      }
    }
    if (this.isLeftTransboundary && this.isRightTransboundary)
      return;
    for (int i = outIndex - 1; i > -1; i--) {// 目标条件所在的块前面的合取范式为假
      for (int j = 0; j < atomicGroups.get(i).size(); j++) {
        tempCoodinateList = this.values.get(atomicConstraintGroups.get(i).get(j).getId());
        if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
          clff = new CLFF(tempCoodinateList, atomicConstraintGroups.get(i).get(j).getOp());
          clff.getNewBoundary();
          if (!this.isLeftTransboundary && clff.isLeftTransboundary()) {
            this.leftBoundary = clff.getLeftBoundary();
            this.isLeftTransboundary = true;
          }
          if (!this.isRightTransboundary && clff.isRightTransboundary()) {
            this.rightBoundary = clff.getRightBoundary();
            this.isRightTransboundary = true;
          }
          break;
        }
      }
    }
  }

  /**
   * 计算新的拓展边界
   * <p>
   * 对由||联立的各个子约束，取最近的拓展边界值
   * 
   * @param clff
   */
  private void predictNewBoundary(CLFF clff) {
    clff.getNewBoundary();
    if (clff.isLeftTransboundary()) {
      double temp = clff.getLeftBoundary();
      if (this.isLeftTransboundary) {
        if (temp > this.leftBoundary) {
          this.leftBoundary = temp;
        }
      } else {
        this.leftBoundary = temp;
        this.isLeftTransboundary = true;
      }
    }

    if (clff.isRightTransboundary()) {
      double temp = clff.getRightBoundary();
      if (this.isRightTransboundary) {
        if (temp < this.rightBoundary) {
          this.rightBoundary = temp;
        }
      } else {
        this.rightBoundary = temp;
        this.isRightTransboundary = true;
      }
    }
  }

  /**
   * 获取可用取值区间
   * 
   * @return 取值区间
   */
  public List<Interval> getEffectiveIntervalList() {
    // Modified by ZYF.
    // Check for cancellation.
    if (Thread.interrupted()) {
      throw new CancellationException();
    }

    List<Interval> effectiveIntervalList = new ArrayList<Interval>();
    List<List<BinaryExpression>> atomicConstraintGroups = this.constraint.getAtomicConstraintGroups();
    List<Coodinate> tempCoodinateList;
    List<Interval> tempIntervalList;
    CLFF clff;
    if (this.isTrue) {
      // 当前约束条件不包含||
      if (atomicConstraintGroups.size() == 1) {
        // 初始化effectiveIntervalList为全集
        Interval initialInterval = ATG.maxInterval();
        effectiveIntervalList.add(initialInterval);
        int groupSize = atomicConstraintGroups.get(0).size();
        for (int i = 0; i < groupSize; i++) {
          tempCoodinateList = this.values.get(atomicConstraintGroups.get(0).get(i).getId());
          if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
            clff = new CLFF(tempCoodinateList, atomicConstraintGroups.get(0).get(i).getOp());
            tempIntervalList = clff.getEffectiveIntervalList();
          } else {
            Interval tempInterval = new Interval();
            tempIntervalList = new ArrayList<Interval>();
            tempIntervalList.add(tempInterval);
          }
          // 求所有原子约束可用区间的交集
          List<Interval> temp = Interval.getIntersection(effectiveIntervalList, tempIntervalList);
          effectiveIntervalList = temp;
        }
      } else	// 当前约束条件包含||
      {
        int groupsSize = atomicConstraintGroups.size();
        for (int i = 0; i < groupsSize; i++) {
          int groupSize = atomicConstraintGroups.get(i).size();
          List<Interval> groupIntervalList = new ArrayList<Interval>();
          // 初始化groupIntervalList为全集
          Interval initialInterval = ATG.maxInterval();
          groupIntervalList.add(initialInterval);
          for (int j = 0; j < groupSize; j++) {
            tempCoodinateList = this.values.get(atomicConstraintGroups.get(i).get(j).getId());
            if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
              clff = new CLFF(tempCoodinateList, atomicConstraintGroups.get(i).get(j).getOp());
              tempIntervalList = clff.getEffectiveIntervalList();
            } else {
              Interval tempInterval = new Interval();
              tempIntervalList = new ArrayList<Interval>();
              tempIntervalList.add(tempInterval);
            }
            // 求所有原子约束可用区间的交集
            List<Interval> temp = Interval.getIntersection(groupIntervalList, tempIntervalList);
            groupIntervalList = temp;
          }
          // 求所有原子约束分组的区间并集
          List<Interval> temp = Interval.getUnion(effectiveIntervalList, groupIntervalList);
          effectiveIntervalList = temp;
        }
      }
    } else {
      // 当前约束条件不包含||
      if (atomicConstraintGroups.size() == 1) {
        int groupSize = atomicConstraintGroups.get(0).size();
        for (int i = 0; i < groupSize; i++) {
          tempCoodinateList = this.values.get(atomicConstraintGroups.get(0).get(i).getId());
          if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
            // tempCoodinateList的size必须为2个或2个以上，因为一个坐标无法做线性拟合
            clff = new CLFF(tempCoodinateList, atomicConstraintGroups.get(0).get(i).getOp());
            tempIntervalList = clff.getEffectiveIntervalList();
          } else {
            tempIntervalList = new ArrayList<Interval>();
          }
          // 求所有原子约束的补集的并集
          List<Interval> temp = Interval.getUnion(effectiveIntervalList, Interval.getComplementary(tempIntervalList));
          effectiveIntervalList = temp;
        }
      } else// 当前约束条件包含||
      {
        // 初始化effectiveIntervalList为全集
        Interval initialInterval = ATG.maxInterval();
        effectiveIntervalList.add(initialInterval);
        int groupsSize = atomicConstraintGroups.size();
        for (int i = 0; i < groupsSize; i++) {
          int groupSize = atomicConstraintGroups.get(i).size();
          // 初始化groupIntervalList为空集
          List<Interval> groupIntervalList = new ArrayList<Interval>();
          for (int j = 0; j < groupSize; j++) {
            tempCoodinateList = this.values.get(atomicConstraintGroups.get(i).get(j).getId());
            if (tempCoodinateList != null && tempCoodinateList.size() > 1) {
              clff = new CLFF(tempCoodinateList, atomicConstraintGroups.get(i).get(j).getOp());
              tempIntervalList = clff.getEffectiveIntervalList();
            } else {
              tempIntervalList = new ArrayList<Interval>();
            }
            List<Interval> temp = Interval.getUnion(groupIntervalList, Interval.getComplementary(tempIntervalList));
            groupIntervalList = temp;
          }
          List<Interval> temp = Interval.getIntersection(effectiveIntervalList, groupIntervalList);
          effectiveIntervalList = temp;
        }
      }
    }

    return effectiveIntervalList;
  }

  /**
   * 为复合约束中的目标条件获取可用取值区间
   * 
   * @param condition
   * @return
   */
  public List<Interval> getEffectiveIntervalListForCondition(BinaryExpression condition, boolean truthValue) {
    // 获取目标条件在主析取范式中的位置
    int outIndex = -1, inIndex = -1;
    List<List<BinaryExpression>> atomicGroups = this.getConstraint().getAtomicConstraintGroups();
    for (int i = 0; i < atomicGroups.size(); i++) {
      for (int j = 0; j < atomicGroups.get(i).size(); j++) {
        if (atomicGroups.get(i).get(j).equals(condition)) {
          outIndex = i;
          inIndex = atomicGroups.get(i).indexOf(condition);
          break;
        }
      }
      if (outIndex != -1)
        break;
    }
    List<Interval> effectiveIntervalList = new ArrayList<Interval>();
    List<Coodinate> tempCoodinateList;
    List<Interval> tempIntervalList;
    CLFF clff;
    // 获取可用输入区间
    Interval initialInterval = ATG.maxInterval();
    effectiveIntervalList.add(initialInterval);
    for (int i = 0; i < outIndex; i++) {// 目标条件所在的块前面的合取范式为假
      List<Interval> tempEffectiveIntervalList = new ArrayList<Interval>();
      for (int j = 0; j < atomicGroups.get(i).size(); j++) {
        tempCoodinateList = this.values.get(atomicGroups.get(i).get(j).getId());
        clff = new CLFF(tempCoodinateList, atomicGroups.get(i).get(j).getOp());
        tempIntervalList = clff.getEffectiveIntervalList();
        List<Interval> temp = Interval.getUnion(tempEffectiveIntervalList, Interval.getComplementary(tempIntervalList));
        tempEffectiveIntervalList = temp;
      }
      effectiveIntervalList = Interval.getIntersection(effectiveIntervalList, tempEffectiveIntervalList);
    }
    for (int h = 0; h < inIndex; h++) {// 目标条件所在的块中位于目标条件前面的条件取值为真
      tempCoodinateList = this.values.get(atomicGroups.get(outIndex).get(h).getId());
      clff = new CLFF(tempCoodinateList, atomicGroups.get(outIndex).get(h).getOp());
      tempIntervalList = clff.getEffectiveIntervalList();
      effectiveIntervalList = Interval.getIntersection(effectiveIntervalList, tempIntervalList);
    }
    // 目标条件取值
    tempCoodinateList = this.getValues().get(condition.getId());
    Operator op = truthValue ? condition.getOp() : Operator.getReverseOp(condition.getOp());
    clff = new CLFF(tempCoodinateList, op);
    tempIntervalList = clff.getEffectiveIntervalList();
    effectiveIntervalList = Interval.getIntersection(effectiveIntervalList, tempIntervalList);

    return effectiveIntervalList;
  }

  public boolean isEqual(SimpleCFGNode myNode) {
    if (this.getName().equals(myNode.getName())) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 还原判断是否越界的标记（2014-6-7）
   */
  public void restore() {
    this.isLeftTransboundary = false;
    this.isRightTransboundary = false;
  }

  /**
   * 判断当前节点是否是含有&&逻辑符的复合约束
   * 
   * @return
   */
  public boolean isAndCompositeConstraint() {
    List<List<BinaryExpression>> atomicConstraintGroups = this.constraint.getAtomicConstraintGroups();
    if (this.isTrue) {
      for (List<BinaryExpression> constraint : atomicConstraintGroups) {
        if (constraint.size() > 1) {
          return true;
        }
      }
    } else {
      if (atomicConstraintGroups.size() > 1) {
        return true;
      }
    }

    return false;
  }

  /**
   * 当前约束为复合约束时，计算其被覆盖的简单约束比例
   * 
   * @return
   */
  public double coveredNodeLength() {
    List<List<BinaryExpression>> atomicConstraintGroups = this.constraint.getAtomicConstraintGroups();
    double length = 0, temp;
    if (this.isTrue) {
      for (List<BinaryExpression> constraint : atomicConstraintGroups) {
        int coveredLength = -1;
        for (BinaryExpression atomicConstraint : constraint) {
          if (this.values.get(atomicConstraint.getId()) != null) {
            coveredLength++;
          }
        }
        temp = (double) coveredLength / constraint.size();
        if (temp > length) {
          length = temp;
        }
      }
    } else {
      int coveredLength = -1;
      for (List<BinaryExpression> constraint : atomicConstraintGroups) {
        for (BinaryExpression atomicConstraint : constraint) {
          if (this.values.get(atomicConstraint.getId()) != null) {
            coveredLength++;
            break;
          }
        }
      }
      length = (double) coveredLength / atomicConstraintGroups.size();
    }
    return length;
  }

  /**
   * 获取分歧节点中具有最大路径覆盖度评估值
   * <p>
   * F = DC + max{1 / Ncon(i) * (CC + 1 / (1 + abs(EP))}
   * 
   * @return
   */
  public double getOptimalInput() {
    List<List<BinaryExpression>> atomicConstraintGroups = this.constraint.getAtomicConstraintGroups();
    List<Coodinate> tempCoodinateList;
    double fp = 0, tempValue, tempInput = 0, tempFp;

    for (int i = 0; i < atomicConstraintGroups.size(); i++) {
      int groupSize = atomicConstraintGroups.get(i).size();
      for (int j = groupSize - 1; j >= 0; j--) {
        tempCoodinateList = this.values.get(atomicConstraintGroups.get(i).get(j).getId());
        if (tempCoodinateList != null && tempCoodinateList.size() > 0) {
          tempValue = Double.MAX_VALUE;
          for (Coodinate c : tempCoodinateList) {
            if (Math.abs(c.getY()) < tempValue) {
              tempValue = Math.abs(c.getY());
              tempInput = c.getX();
            }
          }
          tempFp = (j + 1 / (1 + tempValue)) / groupSize;
          if (fp < tempFp) {
            fp = tempFp;
            this.optimal = tempInput;
          }
          break;
        }
      }
    }
    return fp;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getType() {
    return this.type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public Constraint getConstraint() {
    return this.constraint;
  }

  public void setConstraint(Constraint constraint) {
    this.constraint = constraint;
  }

  public HashMap<String, List<Coodinate>> getValues() {
    return this.values;
  }

  public void setValues(HashMap<String, List<Coodinate>> values) {
    this.values = values;
  }

  public boolean isTrue() {
    return this.isTrue;
  }

  public void setTrue(boolean isTrue) {
    this.isTrue = isTrue;
  }

  public double getLeftBoundary() {
    return this.leftBoundary;
  }

  public double getRightBoundary() {
    return this.rightBoundary;
  }

  public boolean isLeftTransboundary() {
    return this.isLeftTransboundary;
  }

  public boolean isRightTransboundary() {
    return this.isRightTransboundary;
  }

  public double getOptimal() {
    return this.optimal;
  }
}
