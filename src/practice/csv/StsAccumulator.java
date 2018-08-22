package practice.csv;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class StsAccumulator {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

	public static void main(String args[]) {
		
		System.out.println("start : " + LocalDateTime.now().toString());
		final LocalDateTime st = LocalDateTime.parse("2015-08-24T00:00:00.000", formatter);
		final LocalDateTime en = LocalDateTime.parse("2015-08-25T00:00:00.000", formatter);
		try (final Writer writer = new FileWriter("./result.csv")){
			Files.list(Paths.get("C:/Users/Y/Documents/STK 10/SGPTEST"))
				.filter(path -> path.toString().endsWith(".csv"))
				.map(path -> getCsvRecordStream(path))
				.forEach(stream -> saveToFile(st, en, stream, writer));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("end   : " + LocalDateTime.now().toString());
	}
	
	
	private final static boolean isTargetRecord(LocalDateTime startTime, LocalDateTime stopTime, String firstRecord) {
		try {
			final LocalDateTime t = LocalDateTime.parse(firstRecord.toString(), formatter);
			return startTime.isBefore(t) && t.isBefore(stopTime);
		} catch (Exception e) {
			return false;
		}
	}
	
	private final static Stream<CSVRecord> getCsvRecordStream(Path path) {
		try(Reader in = new FileReader(path.toFile())){
			final CSVParser parser = CSVFormat
				.EXCEL
				.withIgnoreHeaderCase()
				.withAllowMissingColumnNames()
				.withIgnoreSurroundingSpaces()
				.parse(in);						
			return parser.getRecords().stream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private final static void saveToFile(LocalDateTime startTime, LocalDateTime stopTime, Stream<CSVRecord> stream, Writer writer) {
		stream
		.filter(record -> isTargetRecord(startTime, stopTime, record.get(0)))
		.forEach(record -> {
			try {
				record.forEach(field -> {
					try {
						writer.write(field);
						writer.write(",");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				writer.write("\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
 
}
