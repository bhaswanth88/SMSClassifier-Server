package com.verndatech.smsclassifier.server;

import java.io.File;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.verndatech.smsclassifier.server.nb.NaiveBayes;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

	/**
	 * This event is executed as late as conceivably possible to indicate that the
	 * application is ready to service requests.
	 */
	// public static NaiveBayes nb;

	public static NaiveBayes inHouseNb;

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {

		inHouseNb = new NaiveBayes();
		inHouseNb.train(new File("smsdata.csv"), 0, 1);
	}

}