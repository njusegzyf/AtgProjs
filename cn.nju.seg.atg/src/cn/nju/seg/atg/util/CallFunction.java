package cn.nju.seg.atg.util;

import java.util.concurrent.CancellationException;

/**
 * 用于函数执行的类
 * 
 * @author zy
 */
public class CallFunction {
  /**
   * 当前执行参数
   */
  private double[] parameters;

  /**
   * 插装函数打印路径
   */
  private String pathFile;

  /**
   * 函数执行时间
   */
  private double execute_time;

  /**
   * 带参构造函数
   * 
   * @param params
   * @param pathFile
   */
  public CallFunction(double[] params, String pathFile) {
    this.parameters = params;
    this.pathFile = pathFile;
  }

  /**
   * 执行待测试函数
   */
  @SuppressWarnings("unqualified-field-access")
  public void callFunction() {
    // 开始计时
    double start_time = System.currentTimeMillis();

    // @since 0.1, check for cancellation
    if (Thread.interrupted()) {
      throw new CancellationException();
    }

    // if (ATG.callFunctionName.equals("callBessik"))
    // {
    // ATG.callCPP.callBessik(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callBetacf"))
    // {
    // ATG.callCPP.callBetacf(parameters[0], parameters[1], parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callBrent"))
    // {
    // ATG.callCPP.callBrent(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callZbrent"))
    // {
    // ATG.callCPP.callZbrent(parameters[0], parameters[1], parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callRan2"))
    // {
    // ATG.callCPP.callRan2((int)parameters[0], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callRan3"))
    // {
    // ATG.callCPP.callRan3((int)parameters[0], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callCaldat"))
    // {
    // ATG.callCPP.callCaldat((int)parameters[0], (int)parameters[1], (int)parameters[2], (int)parameters[3], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callJulday"))
    // {
    // ATG.callCPP.callJulday((int)parameters[0], (int)parameters[1], (int)parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callBessi"))
    // {
    // ATG.callCPP.callBessi((int)parameters[0], parameters[1], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callBessj"))
    // {
    // ATG.callCPP.callBessj((int)parameters[0], parameters[1], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callEi"))
    // {
    // ATG.callCPP.callEi(parameters[0], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callPlgndr"))
    // {
    // ATG.callCPP.callPlgndr((int)parameters[0], (int)parameters[1], parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callExpint"))
    // {
    // ATG.callCPP.callExpint((int)parameters[0], parameters[1], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callGammp"))
    // {
    // ATG.callCPP.callGammp(parameters[0], parameters[1], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callDbrent"))
    // {
    // ATG.callCPP.callDbrent(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
    // }
    //
    // if (ATG.callFunctionName.equals("callBessi0"))
    // {
    // ATG.callCPP.callBessi0(parameters[0], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callBessi1"))
    // {
    // ATG.callCPP.callBessi1(parameters[0], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callBessk0"))
    // {
    // ATG.callCPP.callBessk0(parameters[0], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callBessk1"))
    // {
    // ATG.callCPP.callBessk1(parameters[0], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callBetai"))
    // {
    // ATG.callCPP.callBetai(parameters[0], parameters[1], parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callFlmoon"))
    // {
    // ATG.callCPP.callFlmoon((int)parameters[0], (int)parameters[1], (int)parameters[2], parameters[3], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callGammp"))
    // {
    // ATG.callCPP.callGammp(parameters[0], parameters[1], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callGammq"))
    // {
    // ATG.callCPP.callGammq(parameters[0], parameters[1], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callGcf"))
    // {
    // ATG.callCPP.callGcf(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callGser"))
    // {
    // ATG.callCPP.callGser(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callMidexp"))
    // {
    // ATG.callCPP.callMidexp(parameters[0], parameters[1], (int)parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callMidinf"))
    // {
    // ATG.callCPP.callMidinf(parameters[0], parameters[1], (int)parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callMidpnt"))
    // {
    // ATG.callCPP.callMidpnt(parameters[0], parameters[1], (int)parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callMidsql"))
    // {
    // ATG.callCPP.callMidsql(parameters[0], parameters[1], (int)parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callMidsqu"))
    // {
    // ATG.callCPP.callMidsqu(parameters[0], parameters[1], (int)parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callProbks"))
    // {
    // ATG.callCPP.callProbks(parameters[0], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callPythag"))
    // {
    // ATG.callCPP.callPythag(parameters[0], parameters[1], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callRan0"))
    // {
    // ATG.callCPP.callRan0((int)parameters[0], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callSphbes"))
    // {
    // ATG.callCPP.callSphbes((int)parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callTrapzd"))
    // {
    // ATG.callCPP.callTrapzd(parameters[0], parameters[1], (int)parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callZbrac"))
    // {
    // ATG.callCPP.callZbrac(parameters[0], parameters[1], pathFile);
    // }

    // else if (ATG.callFunctionName.equals("callExample"))
    // {
    // ATG.callCPP.callExample(parameters[0], parameters[1], parameters[2]);
    // }
    // else if (ATG.callFunctionName.equals("callSimExample"))
    // {
    // ATG.callCPP.callSimExample(parameters[0], parameters[1], parameters[2], pathFile);
    // }

    // if (ATG.callFunctionName.equals("callBinSearch_double")){
    // ATG.callCPP.callBinSearch_double(parameters[0],parameters[1],parameters[2],parameters[3],
    // parameters[4],(int)parameters[5],(int)parameters[6],parameters[7],pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callBinSearch_int")){
    // ATG.callCPP.callBinSearch_int((int)parameters[0],(int)parameters[1],(int)parameters[2],(int)parameters[3],
    // (int)parameters[4],(int)parameters[5],(int)parameters[6],(int)parameters[7],pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callCalcGradientOrientation2")){
    // ATG.callCPP.callCalcGradientOrientation2((int)parameters[0],(int)parameters[1],parameters[2],parameters[3],pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callHaar2")){
    // ATG.callCPP.callHaar2((int)parameters[0],(int)parameters[1], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callCalcFeature_HIST")){
    // ATG.callCPP.callCalcFeature_HIST((int)parameters[0],(int)parameters[1], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callCreateNeqImageHeader")){
    // ATG.callCPP.callCreateNeqImageHeader((int)parameters[0],(int)parameters[1],(int)parameters[2], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callHaar1")){
    // ATG.callCPP.callHaar1((int)parameters[0],(int)parameters[1],pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callL2Norm")){
    // ATG.callCPP.callL2Norm((int)parameters[0], pathFile);
    // }
    // else if (ATG.callFunctionName.equals("callPrediction2")){
    // ATG.callCPP.callPrediction2(parameters[0], pathFile);
    // }
    // FSE14
    switch (ATG.callFunctionName) {
    case "expint":
      ATG.callCPP.callExpint((int) parameters[0], parameters[1], pathFile);
      break;
    case "test":
      ATG.callCPP.callTest((int) parameters[0], (int) parameters[1], pathFile);
      break;

    case "benchmark01":
      ATG.callCPP.callBenchmark01(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark02":
      ATG.callCPP.callBenchmark02(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark03":
      ATG.callCPP.callBenchmark03(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark04":
      ATG.callCPP.callBenchmark04(parameters[0], pathFile);
      break;
    case "benchmark05":
      ATG.callCPP.callBenchmark05(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark06":
      ATG.callCPP.callBenchmark06(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark07":
      ATG.callCPP.callBenchmark07(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark08":
      ATG.callCPP.callBenchmark08(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark09":
      ATG.callCPP.callBenchmark09(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark10":
      ATG.callCPP.callBenchmark10(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark11":
      ATG.callCPP.callBenchmark11(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark12":
      ATG.callCPP.callBenchmark12(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark13":
      ATG.callCPP.callBenchmark13(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark14":
      ATG.callCPP.callBenchmark14(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark15":
      ATG.callCPP.callBenchmark15(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark16":
      ATG.callCPP.callBenchmark16(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark17":
      ATG.callCPP.callBenchmark17(parameters[0], pathFile);
      break;
    case "benchmark18":
      ATG.callCPP.callBenchmark18(parameters[0], pathFile);
      break;
    case "benchmark19":
      ATG.callCPP.callBenchmark19(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark20":
      ATG.callCPP.callBenchmark20(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark21":
      ATG.callCPP.callBenchmark21(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark22":
      ATG.callCPP.callBenchmark22(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], pathFile);
      break;
    case "benchmark23":
      ATG.callCPP.callBenchmark23(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark25":
      ATG.callCPP.callBenchmark25(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark26":
      ATG.callCPP.callBenchmark26(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark27":
      ATG.callCPP.callBenchmark27(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark29":
      ATG.callCPP.callBenchmark29(parameters[0], pathFile);
      break;
    case "benchmark32":
      ATG.callCPP.callBenchmark32(parameters[0], pathFile);
      break;
    case "benchmark33":
      ATG.callCPP.callBenchmark33(parameters[0], pathFile);
      break;
    case "benchmark34":
      ATG.callCPP.callBenchmark34(parameters[0], pathFile);
      break;
    case "benchmark35":
      ATG.callCPP.callBenchmark35(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark38":
      ATG.callCPP.callBenchmark38(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark39":
      ATG.callCPP.callBenchmark39(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark40":
      ATG.callCPP.callBenchmark40(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark41":
      ATG.callCPP.callBenchmark41(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark42":
      ATG.callCPP.callBenchmark42(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark43":
      ATG.callCPP.callBenchmark43(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark44":
      ATG.callCPP.callBenchmark44(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark45":
      ATG.callCPP.callBenchmark45(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark46":
      ATG.callCPP.callBenchmark46(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark47":
      ATG.callCPP.callBenchmark47(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark48":
      ATG.callCPP.callBenchmark48(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark49":
      ATG.callCPP.callBenchmark49(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark50":
      ATG.callCPP.callBenchmark50(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark52":
      ATG.callCPP.callBenchmark52(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark53":
      ATG.callCPP.callBenchmark53(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark56":
      ATG.callCPP.callBenchmark56(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark61":
      ATG.callCPP.callBenchmark61(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark62":
      ATG.callCPP.callBenchmark62(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark70":
      ATG.callCPP.callBenchmark70(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8], parameters[9],
                                  parameters[10], parameters[11], parameters[12], parameters[13], parameters[14], parameters[15], parameters[16], parameters[17], parameters[18], parameters[19],
                                  parameters[20], parameters[21], parameters[22], parameters[23], pathFile);
      break;
    case "benchmark71":
      ATG.callCPP.callBenchmark71(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6],
                                  parameters[7], parameters[8], parameters[9], parameters[10], parameters[11], pathFile);
      break;
    case "benchmark72":
      ATG.callCPP.callBenchmark72(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], pathFile);
      break;
    case "benchmark73":
      ATG.callCPP.callBenchmark73(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8], parameters[9], pathFile);
      break;
    case "benchmark74":
      ATG.callCPP.callBenchmark74(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], pathFile);
      break;
    case "benchmark75":
      ATG.callCPP.callBenchmark75(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8], pathFile);
      break;
    case "benchmark76":
      ATG.callCPP.callBenchmark76(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8], pathFile);
      break;
    case "benchmark77":
      ATG.callCPP.callBenchmark77(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8], parameters[9], pathFile);
      break;
    case "benchmark78":
      ATG.callCPP.callBenchmark78(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], (int) parameters[5], (int) parameters[6], (int) parameters[7], pathFile);
      break;
    case "benchmark79":
      ATG.callCPP.callBenchmark79(parameters[0], parameters[1], parameters[2], parameters[3], (int) parameters[4], pathFile);
      break;
    case "benchmark80":
      ATG.callCPP.callBenchmark80(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark81":
      ATG.callCPP.callBenchmark81(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark82":
      ATG.callCPP.callBenchmark82(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark83":
      ATG.callCPP.callBenchmark83(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark84":
      ATG.callCPP.callBenchmark84(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "benchmark91":
      ATG.callCPP.callBenchmark91(parameters[0], parameters[1], pathFile);
      break;
    case "commitEarly":
      ATG.callCPP.callCommitEarly((int) parameters[0], (int) parameters[1], pathFile);
      break;
    case "testCollision1":
      ATG.callCPP.callTestCollision1((int) parameters[0], (long) parameters[1], (int) parameters[2], (int) parameters[3], (long) parameters[4], (int) parameters[5], pathFile);
      break;
    case "testCollision2":
      ATG.callCPP.callTestCollision2((long) parameters[0], (int) parameters[1], (long) parameters[2], (int) parameters[3], pathFile);
      break;
    case "testCollision3":
      ATG.callCPP.callTestCollision3((long) parameters[0], (long) parameters[1], pathFile);
      break;
    case "testCollision4":
      ATG.callCPP.callTestCollision4((int) parameters[0], (long) parameters[1], (int) parameters[2], pathFile);
      break;
    case "testCollision5":
      ATG.callCPP.callTestCollision5((long) parameters[0], (int) parameters[1], pathFile);
      break;
    case "beale":
      ATG.callCPP.callBeale(parameters[0], parameters[1], pathFile);
      break;
    case "freudensteinRoth":
      ATG.callCPP.callFreudensteinRoth(parameters[0], parameters[1], pathFile);
      break;
    case "helicalValley":
      ATG.callCPP.callHelicalValley(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "powell":
      ATG.callCPP.callPowell(parameters[0], parameters[1], pathFile);
      break;
    case "rosenbrock":
      ATG.callCPP.callRosenbrock(parameters[0], parameters[1], pathFile);
      break;
    case "wood":
      ATG.callCPP.callWood(parameters[0], parameters[1], parameters[2], parameters[3], pathFile);
      break;
    case "dart":
      ATG.callCPP.callDart((int) parameters[0], (int) parameters[1], pathFile);
      break;
    case "power":
      ATG.callCPP.callPower((int) parameters[0], (int) parameters[1], pathFile);
      break;
    case "conflict":
      ATG.callCPP.callConflict(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], pathFile);
      break;
    case "turnLogic":
      ATG.callCPP.callTurnLogic(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], pathFile);
      break;
    case "mysin":
      ATG.callCPP.callMysin(parameters[0], pathFile);
      break;
    case "stat":
      ATG.callCPP.callStat((int) parameters[0], pathFile);
      break;

    // CW 补充实验
    case "benchmark28":
      ATG.callCPP.callBenchmark28(parameters[0], pathFile);
      break;
    case "benchmark30":
      ATG.callCPP.callBenchmark30(parameters[0], pathFile);
      break;
    case "benchmark31":
      ATG.callCPP.callBenchmark31(parameters[0], pathFile);
      break;
    case "benchmark36":
      ATG.callCPP.callBenchmark36(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark37":
      ATG.callCPP.callBenchmark37(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark51":
      ATG.callCPP.callBenchmark51(parameters[0], parameters[1], pathFile);
      break;
    case "benchmark54":
      ATG.callCPP.callBenchmark54(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark55":
      ATG.callCPP.callBenchmark55(parameters[0], parameters[1], parameters[2], pathFile);
      break;
    case "benchmark57":
      ATG.callCPP.callBenchmark57(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark58":
      ATG.callCPP.callBenchmark58(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark59":
      ATG.callCPP.callBenchmark59(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark60":
      ATG.callCPP.callBenchmark60(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark63":
      ATG.callCPP.callBenchmark63(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark64":
      ATG.callCPP.callBenchmark64(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark65":
      ATG.callCPP.callBenchmark65(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark66":
      ATG.callCPP.callBenchmark66(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], pathFile);
      break;
    case "benchmark67":
      ATG.callCPP.callBenchmark67(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], pathFile);
      break;
    case "benchmark68":
      ATG.callCPP.callBenchmark68(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], pathFile);
      break;
    case "benchmark69":
      ATG.callCPP.callBenchmark69(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], pathFile);
      break;
    }

    // 结束计时
    execute_time = System.currentTimeMillis() - start_time;
  }

  public double executeTime() {
    return this.execute_time;
  }
}
