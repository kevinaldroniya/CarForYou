package com.car.foryou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@ComponentScan(basePackages = {"com.car.foryou"})
public class CarForYouApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarForYouApplication.class, args);
//		for (int i = 1; i <= 10; i++) {
//			System.out.println(String.format("admin"));
//		}
//		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//		keyGenerator.init(256);
//		SecretKey key = keyGenerator.generateKey();
//		String s = Base64.getEncoder().encodeToString(key.getEncoded());
//		System.out.println(s);
	}

}
