package com.verndatech.smsclassifier.server.nb;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

/**
 * @author Bhaswanth
 *
 */
public class NaiveBayes {
	private int totalTrainingSet = 0;
	private Map<String, List<String>> inputDataset = new ConcurrentHashMap<>();
	private Map<String, Double> logClassPriors = new ConcurrentHashMap<>();
	private Set<String> vocab = ConcurrentHashMap.newKeySet();
	private Map<String, Map<String, Long>> wordCount = new ConcurrentHashMap<>();
	ITokenizer tokenizer = new NGramImpl();
	private Map<String, Long> labelItemCount = new HashMap<>();

	public void train(File csvFile, int indexOfClass, int indexOfText) {
		readData(csvFile, indexOfClass, indexOfText, -1);
		calculateLogClassPriors();
		calculateWordCounts();

	}

	public void train(File csvFile, int indexOfClass, int indexOfText, int trainingSamples) {
		readData(csvFile, indexOfClass, indexOfText, trainingSamples);
		calculateLogClassPriors();
		calculateWordCounts();
		validate(csvFile, indexOfClass, indexOfText, trainingSamples);
	}

	private void validate(File csvFile, int indexOfClass, int indexOfText, int trainingSamples) {
		CsvReader csvReader = new CsvReader();
		csvReader.setContainsHeader(false);
		int count = 0;
		int correctCount = 0;
		int validatedCount=0;
		try (CsvParser csvParser = csvReader.parse(csvFile, StandardCharsets.UTF_8)) {
			CsvRow row;
			while ((row = csvParser.nextRow()) != null) {
				count++;
				if (count < trainingSamples) {
					continue;
				}
				validatedCount++;
				String givenClass = row.getField(indexOfClass);

				String predicatedClass = predict(row.getField(indexOfText));
				if (givenClass.equalsIgnoreCase(predicatedClass)) {
					correctCount++;
				}
			}
			System.out.println("Correctly Predicted " + correctCount + " out of " + validatedCount);
			System.out.println("Accuracy percent" + (correctCount * 100 / validatedCount));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void calculateWordCounts() {
		Iterator<String> it = inputDataset.keySet().iterator();
		while (it.hasNext()) {
			String label = it.next();
			inputDataset.get(label).stream().forEach(item -> caculateWordcountForLabelData(label, item));
			labelItemCount.put(label, (long) inputDataset.get(label).size());
			 it.remove();
		}
	}

	private void caculateWordcountForLabelData(String label, String item) {
		 tokenizer.tokenize(item).parallelStream().forEach(token -> processTokenForCount(label, item, token));
	}

	private void processTokenForCount(String label, String item, String token) {
		vocab.add(token);
		if (wordCount.containsKey(label)) {
			if (wordCount.get(label).containsKey(token)) {
				try {
					wordCount.get(label).put(token,
							((wordCount.get(label).get(token) == null) ? 0 : (wordCount.get(label).get(token))) + 1);
				} catch (Exception e) {
					e.printStackTrace();

				}
			} else {
				wordCount.get(label).put(token, 1L);
			}
		} else {
			Map<String, Long> value = new ConcurrentHashMap<>();
			value.put(token, 1L);
			wordCount.put(label, value);
		}

	}

	private void readData(File csvFile, int indexOfClass, int indexOfText, int trainingSamples) {
		System.out.println("------------------------------------------");

		System.out.println("Reading file started");

		CsvReader csvReader = new CsvReader();
		csvReader.setContainsHeader(false);
		int count = 0;
		try (CsvParser csvParser = csvReader.parse(csvFile, StandardCharsets.UTF_8)) {
			CsvRow row;
			while ((row = csvParser.nextRow()) != null) {
				if (count == trainingSamples) {
					break;
				}
				try {
					if (inputDataset.containsKey(row.getField(indexOfClass))) {
						inputDataset.get(row.getField(indexOfClass)).add(row.getField(indexOfText));
					} else {
						List<String> values = new ArrayList<String>();
						values.add(row.getField(indexOfText));
						inputDataset.put(row.getField(indexOfClass), values);
					}
					totalTrainingSet++;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("line failed to parse:" + row.toString());
				}
				count++;

			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("finished reading file:" + totalTrainingSet);
		System.out.println("------------------------------------------");
		System.gc();

	}

	private void calculateLogClassPriors() {
		System.out.println("------------------------------------------");
		for (String label : inputDataset.keySet()) {
			double value = (double) inputDataset.get(label).size() / totalTrainingSet;
			Double logValue = Math.log(value);
			logClassPriors.put(label, logValue);
			System.out.println("Log prior for " + label + ": " + logValue);
		}
		System.out.println("------------------------------------------");

	}

	public String predict(String inputString) {
		List<String> tokens = tokenizer.tokenize(inputString);
		Map<String, Integer> occurences = getKeywordCounts(tokens);
		HashMap<String, Double> logValues = new HashMap<String, Double>();

		for (String token : tokens) {
			if (!vocab.contains(token)) {
				continue;
			}

			for (String label : labelItemCount.keySet()) {
				if (logValues.containsKey(label)) {
					Long tokenCount = (wordCount.get(label).getOrDefault(token, 0L));

					Double value = Math
							.log((double) (tokenCount + 1) / (labelItemCount.getOrDefault(label, 0L) + vocab.size()));
					logValues.put(label, logValues.get(label) + occurences.get(token) * value);
				} else {

					Long tokenCount = (wordCount.get(label).getOrDefault(token, 0L));
					Double value = Math
							.log((double) (tokenCount + 1) / (labelItemCount.getOrDefault(label, 0L) + vocab.size()));

					logValues.put(label, occurences.get(token) * value);

				}
			}

		}
		Double maxScore = Double.NEGATIVE_INFINITY;
		String predictLabel = null;
		for (String label : logValues.keySet()) {
			Double value = logValues.get(label) + logClassPriors.get(label);
			logValues.put(label, value);
			{
				if (value > maxScore) {
					maxScore = value;
					predictLabel = label;
				}

			}

		}
		return predictLabel;
	}

	private Map<String, Integer> getKeywordCounts(List<String> keywordArray) {
		Map<String, Integer> counts = new HashMap<>();

		Integer counter;
		for (int i = 0; i < keywordArray.size(); ++i) {
			counter = counts.get(keywordArray.get(i));
			if (counter == null) {
				counter = 0;
			}
			counts.put(keywordArray.get(i), ++counter); 
		}

		return counts;
	}

}
