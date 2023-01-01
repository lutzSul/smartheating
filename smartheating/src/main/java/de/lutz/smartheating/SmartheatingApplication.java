package de.lutz.smartheating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "de.lutz.smartheating")
public class SmartheatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartheatingApplication.class, args);
	}

}
