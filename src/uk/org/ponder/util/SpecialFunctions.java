/*
 * Created on 12-Apr-2004
 */
package uk.org.ponder.util;

/**
 * @author Bosmon
 *
 * The class 
 */
public class SpecialFunctions {
  private static double[] gammalncof =
    new double[] {
      76.18009172947146,
      -86.50532032941677,
      24.01409824083091,
      -1.231739572450155,
      0.1208650973866179e-2,
      -0.5395239384953e-5 };
      
  double gammaln(double xx) {
    double y = xx, x = xx;
    double tmp = x + 5.5;
    tmp -= (x + 0.5) * Math.log(tmp);
    double ser = 1.000000000190015;
    for (int j = 0; j <= 5; j++) {
      ser += gammalncof[j] / ++y;
    }
    return -tmp + Math.log(2.5066282746310005 * ser / x);
  }
  //Returns ln(n!).
  double factln(int n) {
    if (n <= 1) return 0.0;
    return gammaln(n + 1.0);
  }
//Returns the binomial coefficient (n k) as a floating-point number.
  double bico(int n, int k) {
    return Math.floor(0.5 + Math.exp(factln(n) - factln(k) - factln(n-k)));
  
  }
}
