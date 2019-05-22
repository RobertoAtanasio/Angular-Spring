package com.example.algamoney.api.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@Configuration
public class MailConfig {

	@Autowired
	private AlgamoneyApiProperty property;

	@Bean
	public JavaMailSender javaMailSender() {
		
		// para maiores informações veja o site: 
		// https://www.concretepage.com/spring/spring-gmail-smtp-send-email-with-attachment-using-annotation
		
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.connectiontimeout", 10000);	// 10 segundos
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setJavaMailProperties(props);
		mailSender.setHost(property.getMail().getHost());
		mailSender.setPort(property.getMail().getPort());
		mailSender.setUsername(property.getMail().getUsername());
		mailSender.setPassword(property.getMail().getPassword());
		
		return mailSender;
	}
}