package uk.org.ponder.doubleutil;

import uk.org.ponder.util.Constants;

public class DoubleArrayUtil {
   
  public static double[] select(double[] from, int[] indices) {
    double[] togo = new double[indices.length];
    for (int i = 0; i < indices.length; ++ i) {
      togo[i] = from[indices[i]];
    }
    return togo;
  }
  
  public static double[] getEmptyArray(int size) {
    double[] togo = new double[size];
    for (int i = 0; i < size; ++ i) {
      togo[i] = Constants.MissingValue;
      }
    return togo;
    }

  public static double[] resize(double[] toexpand, int newsize) {
    double[] togo = new double[newsize];
    System.arraycopy(toexpand, 0, togo, 0, toexpand.length);
    for (int i = toexpand.length; i < newsize; ++ i) {
      togo[i] = Constants.MissingValue;
      }
    return togo;
    }

  public static boolean equals(double[] array1, double[] array2) {
    if (array1 == null && array2 == null) return true;
    if (array1 == null && array2 != null 
	|| array1 != null && array2 == null) return false;
    if (array1.length != array2.length) return false;
    for (int i = 0; i < array1.length; ++ i) {
      if (array1[i] != array2[i]) return false;
      }
    return true;
    }

    public static void copy(double[] target, double[] source) {
    for (int i = 0; i < source.length; ++ i) {
      target[i] = source[i];
      }
    }

  public static double[] copy(double[] tocopy) {
    double[] togo = new double[tocopy.length];
    copy(togo, tocopy);
    return togo;
    }

  public static void bounds(double[] toscan, double[] bounds) {
    double min = bounds[0];
    double max = bounds[1];
    for (int i = 0; i < toscan.length; ++ i) {
      double thisnum = toscan[i];
      if (thisnum != Constants.MissingValue) {
        if (thisnum < min) min = thisnum;
        if (thisnum > max) max = thisnum;
        }
      }
    bounds[0] = min;
    bounds[1] = max;
    }

  public static double dotprod(double[] vec1, double[] vec2) {
    double total = 0;
    for (int i = vec1.length-1; i >= 0; --i) {
      total += vec1[i] * vec2[i];
    }
    return total;
  }
  
  public static void multiplyhorr(double[] target, double[][]A, double[]x) {
    //double[] togo = new double[A.length];
    for (int i = 0; i < A.length; ++ i) {
      target[i] = dotprod(A[i], x);
    }
    //return togo;
  }
  
  public static double[] addInto(double[] target, double[] operand) {
    for (int i = 0; i < target.length; ++ i) {
      target[i] += operand[i];
      }
    return target;
    }

  public static double[] scale(double[] toscale, double factor) {
    for (int i = 0; i < toscale.length; ++ i) {
      toscale[i] *= factor;
      }
    return toscale;
    }

  public static double[] addScale(double[] target, double factor, double[] operand) {
    for (int i = 0; i < target.length; ++ i) {
      target[i] += factor * operand[i];
      }
    return target;
    }


  private static int shift (double v)
   {
     int shift = 0;

     double power = 1;
     double abs = v < 0 ? -v : v;
     while (abs >= power)
       {
         power *= 10;
         shift++;
       }
     while (abs < (power *= .1))
       shift--;

     return shift;
   }

   public static void append (StringBuffer buffer,double d)
   {
     if (d == 0)
       buffer.append ('0');
     else
       append (buffer,d,15 - shift (d));
   }

   public static void append (StringBuffer buffer,double d,int digits)
   {
     boolean negative = d < 0;
     if (negative)
       d = -d;

     if (d == Double.POSITIVE_INFINITY)
       throw new IllegalArgumentException ("trying to print infinity");

     double lowest = 1;
     while (digits-- > 0)
       lowest *= .1;

     d += .5 * lowest;

     if (d < lowest)
       {
         buffer.append (0);
         return;
       }

     if (negative)
       buffer.append ('-');

     double power = 1;
     while (power <= d)
       power *= 10;
    
     while (d >= lowest || power > 1)
       {
         if (power == 1)
           buffer.append ('.');
         power *= .1;
         int digit = (int) (d / power);
         buffer.append ((char) ('0' + digit));
         d -= digit * power;
       }
   }



  /**
   * @param buffer
   * @param ds
   */
  public static StringBuffer append(StringBuffer buffer, double[] arr) {
    for (int i = 0;i < arr.length;i++)
    {
      if (i != 0)
        buffer.append (' ');
      append (buffer,arr [i]);
    }
    return buffer;
}

    public String toString(double[] arr) {
      return append(new StringBuffer(), arr).toString();
    }
  }

