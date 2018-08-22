package practice.rev;

import java.time.LocalDateTime;

public class StatRecord {
	
	LocalDateTime startTime;
	LocalDateTime stopTime;
	String mode = null;

	private long dataCount = 0;
	private double average = 0.0;
	private double sigma = 0.0;
	private LocalDateTime maxTime;
	private double max = Double.MIN_VALUE;
	private LocalDateTime minTime;
	private double min = Double.MAX_VALUE;
	
	public static StatRecord of(final String start, final String stop, final String mode) {
		final StatRecord m = new StatRecord();
		m.startTime = LocalDateTime.parse(start, SimpleStatCreator.CSV_FORMAT);
		m.stopTime  = LocalDateTime.parse(stop,  SimpleStatCreator.CSV_FORMAT);
		m.mode = mode;
		return m;
	}
	
	public static StatRecord of(String[] record) {
		StatRecord s = new StatRecord();
		s.setRecord(record);
		return s;
	}
	
	public static StatRecord parse(String line) {
		return StatRecord.of(line.split(","));
	}
	
	public void init() {
		dataCount = 0;
		average = 0.0;
		sigma = 0.0;
		maxTime = null;
		max = Double.MIN_VALUE;
		minTime = null;
		min = Double.MAX_VALUE;
	}
		
	public void setCalculated(
			final long dataCount,
			final double average,
			final double sigma,
			final LocalDateTime maxTime,
			final double max,
			final LocalDateTime minTime,
			final double min
			) {
		if(dataCount == 0) return;
		this.dataCount = dataCount;
		this.average = average;
		this.sigma = sigma;
		this.maxTime = maxTime;
		this.max = max;
		this.minTime = minTime;
		this.min = min;
	}
	
	public void add(LocalDateTime time, Double value) {
		this.dataCount++;
		final double x = value;
		final double n = dataCount;
		final double u = average;
		final double o = sigma;
		final double c1 = 1/n;
		final double c2 = (n - 1);
		final double U = c1*(c2*u + x);
		final double O = (c2*(o*o + u*u) + x*x)*c1 - U*U;
		average = U;
		sigma = (O < 0) ? 0 : Math.sqrt(O);
			
		
		if(max < x) {
			max = x;
			maxTime = time;
		}
		if(min > x) {
			min = x;
			minTime = time;
		}
	}
	
	public void setRecord(String recordLine) {
		this.setRecord(recordLine.split(","));
	}
	
	public void setRecord(String[] record) {
		startTime = LocalDateTime.parse(record[0], SimpleStatCreator.CSV_FORMAT);
		stopTime = LocalDateTime.parse(record[1], SimpleStatCreator.CSV_FORMAT);
		mode = record[2];
		if(record.length == 3) return;
		dataCount = Long.parseLong(record[3]);
		average = Double.parseDouble(record[4]);
		sigma = Double.parseDouble(record[5]);
		maxTime = LocalDateTime.parse(record[6], SimpleStatCreator.CSV_FORMAT);
		max = Double.parseDouble(record[7]);
		minTime = LocalDateTime.parse(record[8], SimpleStatCreator.CSV_FORMAT);
		min = Double.parseDouble(record[9]);
	}
		
	public void setCalculated(StatRecord m) {
		this.setCalculated(m.dataCount, m.average, m.sigma, m.maxTime, m.max, m.minTime, m.min);
	}
	
	public String toRecrodString() {
		return this.dataCount > 0 
			? new StringBuilder()
				 .append(SimpleStatCreator.CSV_FORMAT.format(this.startTime)).append(",")
				 .append(SimpleStatCreator.CSV_FORMAT.format(this.stopTime)).append(",")
				 .append(this.mode).append(",")
				 .append(this.dataCount).append(",")
				 .append(this.average).append(",")
				 .append(this.sigma).append(",")
				 .append(SimpleStatCreator.CSV_FORMAT.format(this.maxTime)).append(",")
				 .append(this.max).append(",")
				 .append(SimpleStatCreator.CSV_FORMAT.format(this.minTime)).append(",")
				 .append(this.min).toString()
			: new StringBuilder()
				 .append(SimpleStatCreator.CSV_FORMAT.format(this.startTime)).append(",")
				 .append(SimpleStatCreator.CSV_FORMAT.format(this.stopTime)).append(",")
				 .append(this.mode).toString();
	}
}

