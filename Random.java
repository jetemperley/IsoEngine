
class Random {

  int a, b, m;
  int x = 1;

  Random(int a, int b, int m, int x) {
    this.a = a;
    this.b = b;
    this.m = m;
    this.x = x;
  }

  Random(int a, int b, int m) {
    this(a, b, m, 1);
  }

  // gets the raw random output whos range is determined by the parameter m
  int getRandRaw(){
    x = (x * a + b) % m;
    return x;
  }

  // gets rand between 0-100
  int getRandi() {
    float f = getRandf();
    return (int) (f * 100);
  }

  // gets rand between 0.0 - 1.0
  float getRandf() {
    x = (x * a + b) % m;
    return x / ((float) m);
  }

}