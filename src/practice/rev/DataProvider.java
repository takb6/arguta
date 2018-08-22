package practice.rev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataProvider {

	private static final String CSV_PATH = "C:/Users/Y/Documents/java/workspace/practice/src/testdata/csv";
	private static final String ACC_PATH = "C:/Users/Y/Documents/java/workspace/practice/src/testdata/rev/3m";
	private static final String REV_TABLE_FILENAME = "C:/Users/Y/Documents/java/workspace/practice/src/testdata/rev/RevTable.csv";
	private static final DateTimeFormatter FILENAME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	public List<String> files(LocalDateTime start, LocalDateTime stop, String id) {
		LocalDate st = start.toLocalDate();
		LocalDate en = stop.toLocalDate();
		
		ArrayList<String> list = new ArrayList<>();

		for(LocalDate d = st; en.isAfter(d) || en.isEqual(d); d = d.plusDays(1)) {
			String filename =  new StringBuilder()
						.append(CSV_PATH)
						.append("/")
						.append(FILENAME_FORMAT.format(d))
						.append("_")
						.append(id)
						.append(".csv").toString();
			list.add(filename);
		}
		return list;
	}
	
	public List<StatRecord> getStatTable() {
		return load(REV_TABLE_FILENAME);
	}
	
	public List<StatRecord> getAccumulatedData(String id) {
		String filename = new StringBuilder(ACC_PATH)
				.append("/")
				.append(id)
				.append(".csv").toString();
		
		return load(filename);
	}
	
	public List<StatRecord> load(String filename) {
		final File file = new File(filename);
		if(!file.exists()) return null;
		try (BufferedReader reader = 
				new BufferedReader(
						new InputStreamReader(
								new FileInputStream(file)))){
			return reader.lines()
					.onClose(() ->  {
						try {
							reader.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					})
					.map(line -> line.split(","))
					.map(array -> StatRecord.of(array))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<StatRecord> load(File file) {
		if(!file.exists()) return null;
		try (BufferedReader reader = 
				new BufferedReader(
						new InputStreamReader(
								new FileInputStream(file)))){
			return reader.lines()
					.onClose(() ->  {
						try {
							reader.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					})
					.map(line -> StatRecord.parse(line))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	

	public void save(List<StatRecord> data, String id) {
		String filename = new StringBuilder(ACC_PATH)
				.append("/")
				.append(id)
				.append(".csv").toString();
	
		try(BufferedWriter writer = 
				new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(filename)))) {
			
			data.stream()
				.forEach(record -> {
					try {
						writer.write(record.toRecrodString() + "\n");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
