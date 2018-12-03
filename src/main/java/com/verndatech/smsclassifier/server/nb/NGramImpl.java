package com.verndatech.smsclassifier.server.nb;
import java.util.ArrayList;
import java.util.List;

public class NGramImpl implements ITokenizer {

	@Override
	public List<String> tokenize(String inputString) {

		ArrayList<String> sanitizedWords = Ngrams.sanitiseToWords(inputString);
		List<String> tokens = Ngrams.ngrams(sanitizedWords, 1);
//		tokens.addAll(Ngrams.ngrams(sanitizedWords, 2));
//		tokens.addAll(Ngrams.ngrams(sanitizedWords, 3));
		return tokens;
	}

}
