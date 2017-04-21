package cn.nju.seg.atg.callCPP;

public class CallCPP {

  public CallCPP() {

  }

  public native double callEi(double x, String pathFile);

  public native double callGammp(double a, double x, String pathFile);

  public native int callJulday(int mm, int id, int iyyy, String pathFile);

  public native double callBessi(int n, double x, String pathFile);

  public native double callBessj(int n, double x, String pathFile);

  public native double callExpint(int n, double x, String pathFile);

  public native double callCaldat(int julian, int mm, int id, int iyyy, String pathFile);

  public native double callPlgndr(int l, int m, double x, String pathFile);

  /****************
   * | 51-500 |
   ***************/
  public native void callBessik(double x, double xnu, double ri, double rk, double rip, double rkp, String pathFile);

  public native double callBetacf(double a, double b, double x, String pathFile);

  public native double callBrent(double ax, double bx, double cx, double tol, String pathFile);

  public native double callZbrent(double x1, double x2, double tol, String pathFile);

  public native double callRan2(int idum, String pathFile);

  public native double callRan3(int idum, String pathFile);

  public native double callDbrent(double ax, double bx, double cx, double tol, double xmin, String pathFile);

  /****************
   * | 1-10 |
   ***************/
  // public native double callBessi0(double x, String pathFile);
  // public native double callBessi1(double x, String pathFile);
  // public native double callBessk0(double x, String pathFile);
  // public native double callBessk1(double x, String pathFile);
  // public native double callBetai(double a, double b, double x, String pathFile);
  // public native void callFlmoon(int n, int nph, int jd, double frac, String pathFile);
  // public native double callGammp(double a, double x, String pathFile);
  // public native double callGammq(double a, double x, String pathFile);
  // public native void callGcf(double gammcf, double a, double x, double gln, String pathFile);
  // public native void callGser(double gamser, double a, double x, double gln, String pathFile);
  // public native double callMidexp(double aa, double bb, int n, String pathFile);
  // public native double callMidinf(double aa, double bb, int n, String pathFile);
  // public native double callMidpnt(double a, double b, int n, String pathFile);
  // public native double callMidsql(double aa, double bb, int n, String pathFile);
  // public native double callMidsqu(double aa, double bb, int n, String pathFile);
  // public native double callProbks(double alam, String pathFile);
  // public native double callPythag(double a, double b, String pathFile);
  // public native double callRan0(int idum, String pathFile);
  // public native void callSphbes(int n, double x, double sj, double sy,double sjp, double syp, String pathFile);
  // public native double callTrapzd(double a, double b, int n, String pathFile);
  // public native boolean callZbrac(double x1, double x2, String pathFile);

  /************************************************
   * |Example Program |
   ***********************************************/
  // public native void callExample(double X, double Y, double Z);
  // public native double callSimExample(double X, double Y, double Z, String pathFile);

  /************************************************
   * | WCET_BENCH中的函数 |
   ***********************************************/
  /*
   * adpcm.c
   */
  // public native int callMy_abs(int n);
  // public native int callMy_fabs(int n);
  // public native int callMy_sin(int rad);
  // public native void callDecode(int input);
  // public native int callEncode(int xin1, int xin2);
  // public native int callFiltep(int rlt1, int al1, int rlt2, int al2);
  // public native int callQuantl(int el, int detl);
  // public native int callLogscl(int il, int nbl);
  // public native void callUpzero(int dlt, int dlti, int bli);
  // public native int callUppol2(int al1, int al2, int plt, int plt1, int plt2);
  // public native int callUppol1(int al1, int apl2, int plt, int plt1);
  // public native int callLogsch(int ih, int nbh);
  //
  // /*
  // * crc.c
  // */
  // public native int callIcrc(int crc, long len, short jinit, int jrev);
  // /*
  // * ludcmp.c
  // */
  // public native int callLudcmp(int n, double eps);

  /************************************************
   * | SamplePrj_Neusoft_20150109 中的函数 |
   ***********************************************/
  /*
   * feature.cpp
   */
  // public native int callBinSearch_double(double e1,double e2,double e3,double e4,double e5,int start,int end,double key,String pathFile);
  // public native int callBinSearch_int(int e1,int e2,int e3,int e4,int e5,int start,int end,int key,String pathFile);
  // public native int callCalcGradientOrientation2(int dx,int dy,double val,double ratio,String pathFile);
  // public native void callHaar2(int rows, int cols,String pathFile);
  // public native void callCalcFeature_HIST(int height, int width,String pathFile);
  // public native int callCreateNeqImageHeader(int width, int height, int depth,String pathFile);
  // public native void callHaar1(int n, int w, String pathFile);
  // public native boolean callPrediction2(double thres, String pathFile);
  // public native void callL2Norm(int vec_len, String pathFile);

  /*************************
   * | coral/JPFBenchmark |
   *************************/
  public native void callBenchmark01(double x, double y, String pathFile);

  public native void callBenchmark02(double x, double y, String pathFile);

  public native void callBenchmark03(double x, double y, String pathFile);

  public native void callBenchmark04(double x, String pathFile);

  public native void callBenchmark05(double x, double y, double z, String pathFile);

  public native void callBenchmark06(double x, double y, double z, String pathFile);

  public native void callBenchmark07(double x, double y, double z, double w, String pathFile);

  public native void callBenchmark08(double x, double y, double z, String pathFile);

  public native void callBenchmark09(double x, double y, String pathFile);

  public native void callBenchmark10(double x, double y, double z, String pathFile);

  public native void callBenchmark11(double x, double y, double z, double w, String pathFile);

  public native void callBenchmark12(double x, double y, double z, double w, String pathFile);

  public native void callBenchmark13(double x, double y, double z, double w, String pathFile);

  public native void callBenchmark14(double x, double y, double z, double w, String pathFile);

  public native void callBenchmark15(double x, double y, double z, double w, String pathFile);

  public native void callBenchmark16(double x, double y, double z, double w, double v, String pathFile);

  public native void callBenchmark17(double x, String pathFile);

  public native void callBenchmark18(double x, String pathFile);

  public native void callBenchmark19(double x, double y, double z, String pathFile);

  public native void callBenchmark20(double x, double y, String pathFile);

  public native void callBenchmark21(double x, double y, String pathFile);

  public native void callBenchmark22(double x, double y, double z, double w, double v, double t, double q, String pathFile);

  public native void callBenchmark23(double x, double y, double z, double w, String pathFile);

  public native void callBenchmark25(double x, double y, double z, double w, double v, String pathFile);

  public native void callBenchmark26(double x, double y, double z, double w, double v, String pathFile);

  public native void callBenchmark27(double x, double y, double z, String pathFile);

  public native void callBenchmark29(double x, String pathFile);

  public native void callBenchmark32(double x, String pathFile);

  public native void callBenchmark33(double x, String pathFile);

  public native void callBenchmark34(double x, String pathFile);

  public native void callBenchmark35(double x, double y, String pathFile);

  public native void callBenchmark38(double x, double y, String pathFile);

  public native void callBenchmark39(double x, double y, String pathFile);

  public native void callBenchmark40(double x, double y, String pathFile);

  public native void callBenchmark41(double x, double y, String pathFile);

  public native void callBenchmark42(double x, double y, String pathFile);

  public native void callBenchmark43(double x, double y, String pathFile);

  public native void callBenchmark44(double x, double y, String pathFile);

  public native void callBenchmark45(double x, double y, double z, String pathFile);

  public native void callBenchmark46(double x, double y, double z, double w, String pathFile);

  public native void callBenchmark47(double x, double y, double z, String pathFile);

  public native void callBenchmark48(double x, double y, double z, String pathFile);

  public native void callBenchmark49(double x, double y, String pathFile);

  public native void callBenchmark50(double x, double y, String pathFile);

  public native void callBenchmark52(double x, double y, double z, String pathFile);

  public native void callBenchmark53(double x, double y, double z, String pathFile);

  public native void callBenchmark56(double x, double y, double z, double w, double t, String pathFile);

  public native void callBenchmark61(double x, double y, double z, double w, String pathFile);

  public native void callBenchmark62(double x, double y, double z, double w, String pathFile);

  public native void callBenchmark70(double a, double b, double c, double d, double e, double f, double g, double h,
                                     double i, double j, double k, double l, double m, double n, double o, double p, double q, double r, double s, double t, double u, double v, double x, double z,
                                     String pathFile);

  public native void callBenchmark71(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double k, double l, String pathFile);

  public native void callBenchmark72(double a, double b, double c, double d, double e, double f, double g, String pathFile);

  public native void callBenchmark73(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, String pathFile);

  public native void callBenchmark74(double a, double b, double c, double d, double e, double f, double g, double h, String pathFile);

  public native void callBenchmark75(double a, double b, double c, double d, double e, double f, double g, double h, double i, String pathFile);

  public native void callBenchmark76(double a, double b, double c, double d, double e, double f, double g, double h, double i, String pathFile);

  public native void callBenchmark77(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, String pathFile);

  public native void callBenchmark78(double a, double b, double c, double d, double e, int f, int g, int h, String pathFile);

  public native void callBenchmark79(double a, double b, double c, double d, int e, String pathFile);

  public native void callBenchmark80(double a, double b, String pathFile);

  public native void callBenchmark81(double a, double b, String pathFile);

  public native void callBenchmark82(double a, double b, String pathFile);

  public native void callBenchmark83(double a, double b, String pathFile);

  public native void callBenchmark84(double a, double b, double c, double d, String pathFile);

  public native void callBenchmark91(double x, double y, String pathFile);

  /******************************
   * |blind/hash/opti benchmark |
   ******************************/
  public native void callCommitEarly(int a, int b, String pathFile);

  public native void callTestCollision1(int x1, long y1, int z1, int x2, long y2, int z2, String pathFile);

  public native void callTestCollision2(long y1, int z1, long y2, int z2, String pathFile);

  public native void callTestCollision3(long y1, long y2, String pathFile);

  public native void callTestCollision4(int x1, long y1, int z1, String pathFile);

  public native void callTestCollision5(long y1, int z1, String pathFile);

  public native void callBeale(double x1, double x2, String pathFile);

  public native void callFreudensteinRoth(double x1, double x2, String pathFile);

  public native void callHelicalValley(double x1, double x2, double x3, String pathFile);

  public native void callPowell(double x1, double x2, String pathFile);

  public native void callRosenbrock(double x1, double x2, String pathFile);

  public native void callWood(double x1, double x2, double x3, double x4, String pathFile);

  /**************************************************
   * |dart/power/ray/sine/stat/tcas/tsafe benchmark |
   **************************************************/
  public native void callDart(int x, int y, String pathFile);

  public native void callPower(int x, int y, String pathFile);

  public native double callConflict(double psi1, double vA, double vC, double xC0, double yC0, double psiC, double bank_ang, String pathFile);

  public native double callTurnLogic(double x0, double y0, double gspeed, double x1, double y1, double x2, double y2, double dt, String pathFile);

  public native double callMysin(double x, String pathFile);

  public native void callStat(int val, String pathFile);

  /*************************
   * | 补充实验coral/JPFBenchmark |
   *************************/
  public native void callBenchmark28(double x, String pathFile);

  public native void callBenchmark30(double x, String pathFile);

  public native void callBenchmark31(double x, String pathFile);

  public native void callBenchmark36(double x, double y, double z, String pathFile);

  public native void callBenchmark37(double x, double y, double z, String pathFile);

  public native void callBenchmark51(double x, double y, String pathFile);

  public native void callBenchmark54(double x, double y, double z, String pathFile);

  public native void callBenchmark55(double x, double y, double z, String pathFile);

  public native void callBenchmark57(double x, double y, double z, double w, double t, String pathFile);

  public native void callBenchmark58(double x, double y, double z, double w, double t, String pathFile);

  public native void callBenchmark59(double x, double y, double z, double w, double t, String pathFile);

  public native void callBenchmark60(double x, double y, double z, double w, double t, String pathFile);

  public native void callBenchmark63(double x, double y, double z, double w, double t, String pathFile);

  public native void callBenchmark64(double x, double y, double z, double w, double t, String pathFile);

  public native void callBenchmark65(double x, double y, double z, double w, double t, String pathFile);

  public native void callBenchmark66(double x, double y, double z, double w, double t, String pathFile);

  public native void callBenchmark67(double x, double y, double z, double w, double t, double v, String pathFile);

  public native void callBenchmark68(double x, double y, double z, double w, double t, double v, String pathFile);

  public native void callBenchmark69(double x, double y, double z, double w, double t, double v, String pathFile);

  public native int callTest(int a, int b, String pathFile);

  static {
    CallCPPLibLoader.loadLibs();
    
//    try {
//      // 加载本地方法所在的链接库名
//      System.loadLibrary("callCPP");
//    } catch (UnsatisfiedLinkError e) {
//      System.err.println("Cannot load callCPP library:\n" + e.toString());
//    }
  }

}
