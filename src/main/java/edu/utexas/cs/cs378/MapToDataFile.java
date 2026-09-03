package edu.utexas.cs.cs378;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class MapToDataFile {
	
	
	static private Pattern pattern = Pattern.compile("[^a-zA-Z]");

	/**
	 * 
	 * @param file
	 * @param batchSize
	 * @param outputTempFile
	 */
	static void mapIt(String file, int batchSize, String outputTempFile) {

		try {
			FileInputStream fin = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fin);

			// Here we uncompress .bz2 file
			CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
			BufferedReader br = new BufferedReader(new InputStreamReader(input));

			// Initialize a bunch of variables

			StringBuilder batch = new StringBuilder("");

		

			mapToFile(batchSize, outputTempFile, br, batch);



			fin.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CompressorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param batchSize
	 * @param outputTempFile
	 * @param br
	 * @param batch
	 * @param lineCounter
	 * @throws IOException
	 */
	static void mapToFile(int batchSize, String outputTempFile, BufferedReader br, StringBuilder batch)
			throws IOException {
		String line;
		Map<String, Long> wordCountTmp = new HashMap<String, Long>(batchSize);
		
		Long lineCounter = 0l;
		// Start reading the file line by line.
		long total = 0L;
		long valid = 0L;
		long error = 0L;
		ArrayList<String> validLines = new ArrayList<>();
		ArrayList<File> validFiles = new ArrayList<>();
		int id = 1;
		int limit = 500000;
		while ((line = br.readLine()) != null) {

			lineCounter += 1;
			total++;
			if(validate(line)) {
				valid++;
				validLines.add(line);
				if(validLines.size() >= limit) {
					Collections.sort(validLines, (a,b) -> {
						float fareA = Float.parseFloat(a.split(",")[11].trim());
						float fareB = Float.parseFloat(b.split(",")[11].trim());
						return Float.compare(fareA, fareB);
					});
					File file = new File("miniSortedFile" + (id) + ".csv");
					id++;
					Files.write(file.toPath(), validLines);
					validFiles.add(file);
					validLines = new ArrayList<>();
				}
			}
			else {
				error++;
				if(error <= 5) {
					System.out.println("Found error in line: " + line);
				}
			}
	
			// add the current text line to the data batch that we want to process.
			/* 
			batch.append(line);

			if (lineCounter % batchSize == 0) {
				wordCountTmp = MapToDataFile.processLine(batch.toString());

				System.out.println(lineCounter + "  Pages processed!");

				// We can write the map into disk and read it back if it is too big.
				// System.out.println(lineCounter + " Pages processed! ");
				MapToDataFile.appendToTempFile(wordCountTmp, outputTempFile);

				// reset the batch to empty string and restart.
				batch = new StringBuilder("");

			}
			*/
		}
		//left over bits
		if(!validLines.isEmpty()) {
			Collections.sort(validLines, (a,b) -> {
				float fareA = Float.parseFloat(a.split(",")[11].trim());
				float fareB = Float.parseFloat(b.split(",")[11].trim());
				return Float.compare(fareA, fareB);
			});
			File file = new File("miniSortedFile" + (id) + ".csv");
			id++;
			Files.write(file.toPath(), validLines);
			validFiles.add(file);
		}
		System.out.println("Done processing.");
		System.out.println("Total lines: " + total);
		System.out.println("Valid lines: " + valid);
		System.out.println("Error lines: " + error);
	}

	/**
	 * 
	 * @param input
	 * @return
	 */

	private static boolean validate(String line) {
		if(line == null) {
			return false;
		}
		// -1 delimeters bc trailing commas are now dealth with
		String[] split = line.split(",", -1);
		if(split.length != 17) {
			return false;
		}
		String fare = split[11].trim();
		try {
			Float.parseFloat(fare);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public static Map<String, Long> processLine(String input) {

		String[] lines = input.split("\\R");
		
		Map<String, Long> wordCount = Arrays.stream(lines).flatMap(line -> Arrays.stream(line.trim().split(" "))) // split
																													// by
																													// space
				.filter(word -> !pattern.matcher(word).find()) //
				.map(word -> word.toLowerCase().trim()) // Drop all words with special chars
														// and convert it to lower case.
				.filter(word -> !word.isEmpty()) // Drop all empty words
				.map(word -> new SimpleEntry<>(word, 1))
				.collect(Collectors.groupingBy(SimpleEntry::getKey, Collectors.counting()));

		return wordCount;

	}

	public static void appendToTempFile(Map<String, Long> data, String outputFile) {

		// new file object
		File file = new File(outputFile);
		BufferedWriter bf = null;

		try {

			// create new BufferedWriter for the output file
			// true means append
			bf = new BufferedWriter(new FileWriter(file, true));

			// iterate map entries
			for (Map.Entry<String, Long> entry : data.entrySet()) {

				// put key and value separated by a comma
				// better use a string builder.
				// better use a data serializiation library

				bf.write(entry.getKey() + "," + entry.getValue());

				// new line
				bf.newLine();
			}

			bf.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				// always close the writer
				bf.close();
			} catch (Exception e) {
			}
		}
	}

}
