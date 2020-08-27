package hw.ebank.services;

import static java.util.Locale.getDefault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class MsgResourceServiceImpl implements MsgResourceService {

	@Autowired
	private MessageSource messageSource;

	@Override
	public String getMessage(String code) {
		return messageSource.getMessage(code, null, "???" + code + "???", getDefault());
	}

}
