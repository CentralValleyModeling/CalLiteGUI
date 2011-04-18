package com.limno.calgui;

import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
public class DoSummaryTable {
	int n[][];
	double x[][];
	double xx[][];
	double avg[][];
	double sdev[][];
	double min[][];
	double max[][];
	private static int ylt[][] = { { 1920, 2, 2, 1, 1, 0, 3, 2, 0, }, { 1921, 2, 2, 1, 1, 0, 3, 2, 0, },
		{ 1922, 2, 1, 1, 1, 0, 4, 2, 0, }, { 1923, 3, 2, 3, 1, 0, 4, 3, 0, }, { 1924, 5, 5, 4, 2, 1, 5, 6, 0, },
		{ 1925, 4, 3, 1, 1, 0, 2, 5, 0, }, { 1926, 4, 4, 3, 1, 0, 4, 5, 0, }, { 1927, 1, 2, 1, 1, 0, 2, 1, 0, },
		{ 1928, 2, 3, 1, 1, 0, 3, 2, 1, }, { 1929, 5, 5, 3, 1, 0, 5, 6, 1, }, { 1930, 4, 5, 2, 1, 0, 4, 5, 1, },
		{ 1931, 5, 5, 4, 2, 1, 5, 6, 1, }, { 1932, 4, 2, 4, 1, 0, 4, 5, 1, }, { 1933, 5, 4, 4, 1, 0, 4, 6, 1, },
		{ 1934, 5, 5, 4, 2, 1, 5, 6, 1, }, { 1935, 3, 2, 1, 1, 0, 4, 3, 0, }, { 1936, 3, 2, 1, 1, 0, 3, 3, 0, },
		{ 1937, 3, 1, 2, 1, 0, 4, 3, 0, }, { 1938, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1939, 4, 4, 3, 2, 0, 5, 5, 0, },
		{ 1940, 2, 2, 1, 1, 0, 2, 2, 0, }, { 1941, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1942, 1, 1, 1, 1, 0, 2, 1, 0, },
		{ 1943, 1, 1, 1, 1, 0, 3, 1, 0, }, { 1944, 4, 3, 3, 1, 0, 5, 5, 0, }, { 1945, 3, 2, 1, 1, 0, 3, 3, 0, },
		{ 1946, 3, 2, 1, 1, 0, 2, 3, 0, }, { 1947, 4, 4, 3, 1, 0, 4, 5, 0, }, { 1948, 3, 3, 1, 1, 0, 3, 3, 0, },
		{ 1949, 4, 3, 2, 1, 0, 3, 5, 0, }, { 1950, 3, 3, 2, 1, 0, 4, 3, 0, }, { 1951, 2, 2, 1, 1, 0, 2, 2, 0, },
		{ 1952, 1, 1, 1, 1, 0, 2, 1, 0, }, { 1953, 1, 3, 1, 1, 0, 2, 1, 0, }, { 1954, 2, 3, 1, 1, 0, 2, 2, 0, },
		{ 1955, 4, 4, 2, 1, 0, 4, 5, 0, }, { 1956, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1957, 2, 3, 1, 1, 0, 3, 2, 0, },
		{ 1958, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1959, 3, 4, 1, 1, 0, 3, 3, 0, }, { 1960, 4, 5, 1, 1, 0, 3, 5, 0, },
		{ 1961, 4, 5, 1, 1, 0, 3, 5, 0, }, { 1962, 3, 3, 1, 1, 0, 3, 3, 0, }, { 1963, 1, 2, 1, 1, 0, 2, 1, 0, },
		{ 1964, 4, 4, 3, 1, 0, 4, 5, 0, }, { 1965, 1, 1, 1, 1, 0, 2, 1, 0, }, { 1966, 3, 3, 1, 1, 0, 3, 3, 0, },
		{ 1967, 1, 1, 1, 1, 0, 2, 1, 0, }, { 1968, 3, 4, 1, 1, 0, 3, 3, 0, }, { 1969, 1, 1, 1, 1, 0, 1, 1, 0, },
		{ 1970, 1, 2, 1, 1, 0, 2, 1, 0, }, { 1971, 1, 3, 1, 1, 0, 2, 1, 0, }, { 1972, 3, 4, 1, 1, 0, 3, 3, 0, },
		{ 1973, 2, 2, 1, 1, 0, 2, 2, 0, }, { 1974, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1975, 1, 1, 1, 1, 0, 2, 1, 0, },
		{ 1976, 5, 5, 3, 2, 0, 4, 6, 2, }, { 1977, 5, 5, 4, 2, 1, 5, 7, 2, }, { 1978, 2, 1, 1, 1, 0, 1, 2, 0, },
		{ 1979, 3, 2, 2, 1, 0, 4, 3, 0, }, { 1980, 2, 1, 1, 1, 0, 2, 2, 0, }, { 1981, 4, 4, 2, 2, 0, 4, 5, 0, },
		{ 1982, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1983, 1, 1, 1, 1, 0, 1, 1, 0, }, { 1984, 1, 2, 1, 1, 0, 2, 1, 0, },
		{ 1985, 4, 4, 3, 1, 0, 4, 5, 0, }, { 1986, 1, 1, 1, 1, 0, 2, 1, 3, }, { 1987, 4, 5, 3, 2, 0, 4, 5, 3, },
		{ 1988, 5, 5, 3, 2, 1, 4, 6, 3, }, { 1989, 4, 5, 1, 1, 0, 3, 5, 3, }, { 1990, 5, 5, 3, 2, 0, 4, 6, 3, },
		{ 1991, 5, 5, 4, 1, 1, 5, 6, 3, }, { 1992, 5, 5, 4, 2, 0, 4, 6, 3, }, { 1993, 2, 1, 1, 1, 0, 2, 2, 0, },
		{ 1994, 5, 5, 4, 2, 0, 5, 6, 0, }, { 1995, 1, 1, 1, 1, 0, 1, 0, 0, }, { 1996, 1, 1, 1, 1, 0, 2, 0, 0, },
		{ 1997, 1, 1, 1, 1, 0, 2, 0, 0, }, { 1998, 1, 1, 1, 1, 0, 1, 0, 0, }, { 1999, 1, 2, 1, 1, 0, 2, 0, 0, },
		{ 2000, 2, 2, 1, 1, 0, 2, 0, 0, }, { 2001, 4, 4, 1, 2, 0, 4, 0, 0, }, { 2002, 4, 4, 1, 1, 0, 3, 0, 0, },
		{ 2003, 2, 3, 1, 1, 0, 2, 0, 0, } };
	private void update(int i1, int i2, double value) {
		n[i1][i2]++;
		x[i1][i2] += value;
		xx[i1][i2] += (value * value);
		if (min[i1][i2] > value)
			min[i1][i2] = value;
		if (max[i1][i2] < value)
			max[i1][i2] = value;
	}
	public void main(TimeSeriesContainer tsc) {
		n = new int[6][6];
		x = new double[6][6];
		xx = new double[6][6];
		min = new double[6][6];
		max = new double[6][6];
		for (int i1 = 0; i1 < 6; i1++)
			for (int i2 = 0; i2 < 6; i2++) {
				n[i1][i2] = 0;
				x[i1][i2] = 0;
				xx[i1][i2] = 0;
				min[i1][i2] = 1e20;
				max[i1][i2] = 0;
			}
		HecTime ht = new HecTime();
		for (int i = 0; i < tsc.numberValues; i++) {
			ht.set(tsc.times[i]);
			int y = ht.year();
			int m = ht.month();
			int wy = (m < 10) ? y : y - 1;
			int ySac403030 = (m < 2) ? y - 1 : y;
			int ySHASTAindex = (m < 3) ? y - 1 : y;
			int yFEATHERindex = (m < 2) ? y - 1 : y;
			int ySJRindex = (m < 2) ? y - 1 : y;
			update(0, 0, tsc.values[i]);
			update(1, ylt[ySac403030 - 1920][1], tsc.values[i]);
			update(2, ylt[ySHASTAindex - 1920][3], tsc.values[i]);
			update(3, ylt[yFEATHERindex - 1920][5], tsc.values[i]);
			update(4, ylt[ySJRindex - 1920][2], tsc.values[i]);
			if (ylt[wy - 1920][8] != 0) {
				update(5, ylt[wy - 1920][8], tsc.values[i]);
				update(5, 0, tsc.values[i]);
			}
		}
		avg = new double[6][6];
		sdev = new double[6][6];
		String[] leftPart = { "All", "Sac 40-30-30", "Shasta", "Feather", "SJR", "Dry" };
		String[] rightPartsDry = { "All dry periods", "1928-1934", "1976-1977", "1986-1992" };
		DecimalFormat df1 = new DecimalFormat("#.#");
		DecimalFormat df2 = new DecimalFormat("#.##");
		for (int i1 = 0; i1 < 6; i1++)
			for (int i2 = 0; i2 < 6; i2++)
				if (n[i1][i2] != 0) {
					avg[i1][i2] = x[i1][i2] / n[i1][i2];
					sdev[i1][i2] = Math.sqrt(Math.abs(xx[i1][i2] / n[i1][i2] - avg[i1][2] * avg[i1][2]));
					String rightPart;
					if (i1 == 0)
						rightPart = "";
					else if (i1 <= 4)
						rightPart = " " + Integer.toString(i2);
					else
						rightPart = " (" + rightPartsDry[i2] + ")";
					System.out.print(leftPart[i1] + rightPart);
					System.out.print("\t");
					System.out.print(n[i1][i2]);
					System.out.print(" ");
					System.out.print(df1.format(avg[i1][i2]));
					System.out.print(" ");
					System.out.print(df2.format(sdev[i1][i2]));
					System.out.print(" ");
					System.out.print(df1.format(min[i1][i2]));
					System.out.print(" ");
					System.out.println(df1.format(max[i1][i2]));
				}
	}
}
