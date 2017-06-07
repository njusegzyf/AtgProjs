import org.junit.Test;

public class JpfTargetTcasTest {

  @Test
  public void test0() {
    Tcas.start_symbolic(0,-1000000,-1000000,0,0,0,0,0,0,0,1,0);
  }

  @Test
  public void test1() {
    Tcas.start_symbolic(0,-1000000,-1000000,0,0,0,0,0,0,0,-1000000,0);
  }

  @Test
  public void test2() {
    Tcas.start_symbolic(0,-1000000,1,0,0,0,0,0,0,0,1,0);
  }

  @Test
  public void test3() {
    Tcas.start_symbolic(0,-1000000,1,0,0,0,0,0,0,0,-1000000,0);
  }

  @Test
  public void test4() {
    Tcas.start_symbolic(0,-1000000,1,0,0,0,0,0,0,-1000000,1,0);
  }

  @Test
  public void test5() {
    Tcas.start_symbolic(0,-1000000,1,0,0,0,0,0,0,-1000000,-1000000,0);
  }

  @Test
  public void test6() {
    Tcas.start_symbolic(0,1,-1000000,0,601,0,0,0,0,0,1,0);
  }

  @Test
  public void test7() {
    Tcas.start_symbolic(0,1,-1000000,0,601,0,0,0,0,0,-1000000,0);
  }

  @Test
  public void test8() {
    Tcas.start_symbolic(0,1,1,0,601,0,0,0,0,0,1,0);
  }

  @Test
  public void test9() {
    Tcas.start_symbolic(0,1,1,0,601,0,0,0,0,0,-1000000,0);
  }

  @Test
  public void test10() {
    Tcas.start_symbolic(0,1,1,0,601,0,0,0,0,-1000000,1,0);
  }

  @Test
  public void test11() {
    Tcas.start_symbolic(0,1,1,0,601,0,0,0,0,-1000000,-1000000,0);
  }

  @Test
  public void test12() {
    Tcas.start_symbolic(-1000000,1,-1000000,0,-1000000,0,0,0,0,0,1,0);
  }

  @Test
  public void test13() {
    Tcas.start_symbolic(-1000000,1,-1000000,0,-1000000,0,0,0,0,0,-1000000,0);
  }

  @Test
  public void test14() {
    Tcas.start_symbolic(-1000000,1,1,0,-1000000,0,0,0,0,0,1,0);
  }

  @Test
  public void test15() {
    Tcas.start_symbolic(-1000000,1,1,0,-1000000,0,0,0,0,0,-1000000,0);
  }

  @Test
  public void test16() {
    Tcas.start_symbolic(-1000000,1,1,0,-1000000,0,0,0,0,-1000000,1,0);
  }

  @Test
  public void test17() {
    Tcas.start_symbolic(-1000000,1,1,0,-1000000,0,0,0,0,-1000000,-1000000,0);
  }

  @Test
  public void test18() {
    Tcas.start_symbolic(601,1,-1000000,0,-1000000,0,0,0,0,0,1,0);
  }

  @Test
  public void test19() {
    Tcas.start_symbolic(601,1,-1000000,-1000000,-1000000,-1000000,0,-1000000,-1000000,0,-1000000,1);
  }

  @Test
  public void test20() {
    Tcas.start_symbolic(601,1,-1000000,-1000000,-1000000,-1000000,0,-1000000,-1000000,0,-1000000,-1000000);
  }

  @Test
  public void test21() {
    Tcas.start_symbolic(601,1,-1000000,-1000000,-1000000,-1000000,0,-1000000,-999700,0,-1000000,1);
  }

  @Test
  public void test22() {
    Tcas.start_symbolic(601,1,-1000000,-1000000,-1000000,-1000000,0,-999999,-1000000,0,-1000000,-1000000);
  }

  @Test
  public void test23() {
    Tcas.start_symbolic(601,1,-1000000,-1000000,-1000000,-999999,0,-1000000,-1000000,0,-1000000,1);
  }

  @Test
  public void test24() {
    Tcas.start_symbolic(601,1,-1000000,-1000000,-1000000,-999999,0,101,400,0,-1000000,1);
  }

  @Test
  public void test25() {
    Tcas.start_symbolic(601,1,-1000000,-1000000,-1000000,-999999,0,401,400,0,-1000000,-1000000);
  }

  @Test
  public void test26() {
    Tcas.start_symbolic(601,1,-1000000,-1000000,-1000000,-999999,0,-999999,-1000000,0,-1000000,-1000000);
  }

  @Test
  public void test27() {
    Tcas.start_symbolic(601,1,-1000000,-999999,-1000000,-1000000,0,-1000000,-1000000,0,-1000000,-1000000);
  }

  @Test
  public void test28() {
    Tcas.start_symbolic(601,1,-1000000,-999999,-1000000,-1000000,0,-1000000,-999700,0,-1000000,1);
  }

  @Test
  public void test29() {
    Tcas.start_symbolic(601,1,-1000000,-999999,-1000000,-1000000,0,400,400,0,-1000000,-1000000);
  }

  @Test
  public void test30() {
    Tcas.start_symbolic(601,1,-1000000,-999999,-1000000,-1000000,0,400,700,0,-1000000,1);
  }

  @Test
  public void test31() {
    Tcas.start_symbolic(601,1,1,0,-1000000,0,0,0,0,-1000000,1,0);
  }

  @Test
  public void test32() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-1000000,-1000000,0,-1000000,1);
  }

  @Test
  public void test33() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-1000000,-1000000,0,-1000000,-1000000);
  }

  @Test
  public void test34() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-1000000,-1000000,0,1,1);
  }

  @Test
  public void test35() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-1000000,-1000000,0,1,-1000000);
  }

  @Test
  public void test36() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-1000000,-1000000,-1000000,-1000000,1);
  }

  @Test
  public void test37() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-1000000,-1000000,-1000000,-1000000,-1000000);
  }

  @Test
  public void test38() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-1000000,-999700,0,-1000000,1);
  }

  @Test
  public void test39() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-1000000,-999700,0,1,1);
  }

  @Test
  public void test40() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-1000000,-999700,-1000000,-1000000,1);
  }

  @Test
  public void test41() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-999999,-1000000,0,-1000000,-1000000);
  }

  @Test
  public void test42() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-999999,-1000000,0,1,-1000000);
  }

  @Test
  public void test43() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-1000000,0,-999999,-1000000,-1000000,-1000000,-1000000);
  }

  @Test
  public void test44() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,-1000000,-1000000,0,-1000000,1);
  }

  @Test
  public void test45() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,-1000000,-1000000,0,1,1);
  }

  @Test
  public void test46() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,-1000000,-1000000,-1000000,-1000000,1);
  }

  @Test
  public void test47() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,101,400,0,-1000000,1);
  }

  @Test
  public void test48() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,101,400,0,1,1);
  }

  @Test
  public void test49() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,101,400,-1000000,-1000000,1);
  }

  @Test
  public void test50() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,401,400,0,-1000000,-1000000);
  }

  @Test
  public void test51() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,401,400,0,1,-1000000);
  }

  @Test
  public void test52() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,401,400,-1000000,-1000000,-1000000);
  }

  @Test
  public void test53() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,-999999,-1000000,0,-1000000,-1000000);
  }

  @Test
  public void test54() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,-999999,-1000000,0,1,-1000000);
  }

  @Test
  public void test55() {
    Tcas.start_symbolic(601,1,1,-1000000,-1000000,-999999,0,-999999,-1000000,-1000000,-1000000,-1000000);
  }

  @Test
  public void test56() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,-1000000,-1000000,0,-1000000,-1000000);
  }

  @Test
  public void test57() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,-1000000,-1000000,0,1,-1000000);
  }

  @Test
  public void test58() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,-1000000,-1000000,-1000000,-1000000,-1000000);
  }

  @Test
  public void test59() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,-1000000,-999700,0,-1000000,1);
  }

  @Test
  public void test60() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,-1000000,-999700,0,1,1);
  }

  @Test
  public void test61() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,-1000000,-999700,-1000000,-1000000,1);
  }

  @Test
  public void test62() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,400,400,0,-1000000,-1000000);
  }

  @Test
  public void test63() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,400,400,0,1,-1000000);
  }

  @Test
  public void test64() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,400,400,-1000000,-1000000,-1000000);
  }

  @Test
  public void test65() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,400,700,0,-1000000,1);
  }

  @Test
  public void test66() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,400,700,0,1,1);
  }

  @Test
  public void test67() {
    Tcas.start_symbolic(601,1,1,-999999,-1000000,-1000000,0,400,700,-1000000,-1000000,1);
  }
}

