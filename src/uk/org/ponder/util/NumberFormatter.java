package uk.org.ponder.util;

import java.text.DecimalFormat;

public class NumberFormatter {
	/// The DecimalFormat used for tick labels.
	private DecimalFormat formatter = new DecimalFormat();
	/// The StringBuffer used to assess the form of the tick labels.
	private StringBuffer formatstring = new StringBuffer();
    
    // adjust the format of the labels 
	public void adjustFormat(double stepsize, double minval, double maxval) {
		formatstring.setLength(0);
		formatstring.append('0');
		double furthest = Math.abs(minval) > Math.abs(maxval)? minval : maxval;
		int figs = (int)Math.ceil(-Math.log(stepsize) / Math.log(10));
		int bigfigs = (int)Math.ceil(-Math.log(Math.abs(furthest)) / Math.log(10));
		boolean scientific = false;
		//		System.out.println("stepsize " + stepsize + " minval " + minval 
		//											 + " maxval " + maxval + " figs " + figs + " bigfigs " + bigfigs);
		// bigfigs will be lower than figs
		// positive figs represents small decimal numbers.
		if ( ((bigfigs > 3) || (bigfigs < -3))) {
			scientific = true;
			//			formatter.applyPattern("0.0E0");
			//			return;
			}
		if (figs > 0 || scientific) formatstring.append('.');
		int limit = 0;
		if (figs > 0 && !scientific) limit = figs;
		if (scientific) limit = figs - bigfigs;
		for (int i = 0; i < limit; ++ i) {
			formatstring.append('0');
			}
		if (scientific) {
			formatstring.append("E0");
			}
		formatter.applyPattern(formatstring.toString());
		}

  public String formatQuick(double number) {
    adjustFormat(number, 0, number);
    return format(number);
    }                                           
  
	public String format(double number) {
		if (number == 0.0) {
			return "0";
			}
		else return formatter.format(number);
		}
	}
