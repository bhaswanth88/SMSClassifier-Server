package com.verndatech.smsclassifier.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSerializationService {
	static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public static String getAsString(Object obj) {
		return gson.toJson(obj);
	}

	public static <T> T getObject(String json, Class<T> t) throws IOException {
		return gson.fromJson(json, t);
	}

	public static <T> T getObject(File file, Class<T> t) throws IOException {
		return gson.fromJson(new FileReader(file), t);
	}

}