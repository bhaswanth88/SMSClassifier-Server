package com.verndatech.smsclassifier.server.nb;
import java.util.List;

public interface ITokenizer {
	public List<String> tokenize(String inputString);
}
