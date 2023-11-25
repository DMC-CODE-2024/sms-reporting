package com.ceir.SmsCallbackProcess;

import com.ceir.SmsCallbackProcess.service.CallbackProcessor;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableEncryptableProperties
@ComponentScan(basePackages = "com.ceir.SmsCallbackProcess")
public class SmsCallbackProcessApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(SmsCallbackProcessApplication.class, args);
		CallbackProcessor processor = ctx.getBean(CallbackProcessor.class);
		new Thread(processor).start();
	}

}
