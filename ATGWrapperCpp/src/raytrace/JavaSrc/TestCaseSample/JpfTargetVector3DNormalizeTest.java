import org.junit.Test;

public class JpfTargetVector3DNormalizeTest {

  @Test
  public void test0() {
    TestDrivers.vector3DNormalize(0.0f,0.0f,0.0f);
  }

  @Test
  public void test1() {
    TestDrivers.vector3DNormalize(0.34462687f,0.0f,0.0f);
  }

  @Test
  public void test2() {
    TestDrivers.vector3DNormalize(1.0f,0.0f,0.0f);
  }

  @Test
  public void test3() {
    TestDrivers.vector3DNormalize(22.425516f,0.0f,0.0f);
  }
}
