package practice.rev;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleStatCreator {

	public static DateTimeFormatter ARG_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	public static DateTimeFormatter CSV_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static long current = 0;
	private static long total = 0;
	
	public static void main(String args[]) {
		
		final LocalDateTime s = LocalDateTime.now();
		
		final LocalDateTime start = LocalDateTime.parse(args[0], ARG_FORMAT);
		final LocalDateTime stop  = LocalDateTime.parse(args[1], ARG_FORMAT);
		
		final ArrayList<String> ids = new ArrayList<>();
		for(int i = 1; i <= 500; i++) {
			ids.add("AC0" + String.format("%03d", i));
		}
		
		total = ids.size();
		current = 0;

		
		ids.stream()
			.parallel()
			.forEach(id -> accumulate(start, stop, id));

		final LocalDateTime e = LocalDateTime.now();
		
		System.out.println(ChronoUnit.MILLIS.between(s, e));
	}
	
	public static void accumulate(final LocalDateTime start, final LocalDateTime stop, final String id) {
		final DataProvider provider = new DataProvider();
		final List<StatRecord> three = provider.getAccumulatedData(id);
		final List<StatRecord> table = provider.getStatTable();
		
		final List<StatRecord> target = table.stream()
					.filter(record -> !(record.stopTime.isBefore(start) || record.startTime.isAfter(stop)))
					.collect(Collectors.toList());
		
		final List<File> files = provider.files(start, stop, id).stream()
				.map(filename -> new File(filename))
				.filter(file -> file.exists())
				.collect(Collectors.toList());
		
		merge(table, three);
		
		target.stream()
			.forEach(x -> x.init());
		
		files.stream()
			.forEach(file -> {	
				try(final BufferedReader reader = 
						new BufferedReader(
								new InputStreamReader(
										new FileInputStream(file)))) {
					reader.lines()
						.map(line -> line.split(","))
						.forEach(record -> {
							final LocalDateTime t = LocalDateTime.parse(record[0], CSV_FORMAT);
							final double value = Double.parseDouble(record[1]);
							target.stream()
								.filter(m -> (t.isEqual(m.startTime) || t.isAfter(m.startTime) && t.isBefore(m.stopTime)))
								.forEach(m -> m.add(t, value));
						});
					reader.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
				
		merge(table, target);
		
		provider.save(table, id);
		
		countup();
	}
	
	private static void countup() {
		current++;
		System.out.print(new StringBuilder("\r")
					.append(current)
					.append("/")
					.append(total)
					.append(" ")
					.append(LocalDateTime.now().toString()));
	}
	
	public static void merge(List<StatRecord> table, List<StatRecord> three) {
		if(three == null) return;
		
		table.stream()
			.forEach(record -> three.stream()
					.filter(x -> x != null)
					.filter(x -> Math.abs(ChronoUnit.SECONDS.between(record.startTime, x.startTime)) <= 10)
					.forEach(x -> record.setCalculated(x)));
	}
	
	
}
