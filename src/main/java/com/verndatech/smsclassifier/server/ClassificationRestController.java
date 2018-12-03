package com.verndatech.smsclassifier.server;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClassificationRestController {

	@RequestMapping(value = "/sms/classify", method = RequestMethod.POST)
	public String classify(HttpServletRequest request, @RequestBody SMSObject sms) {
//		return ApplicationStartup.nb.predict(sms.getSmsText());
		return ApplicationStartup.inHouseNb.predict(sms.getSmsText());
	}

}
