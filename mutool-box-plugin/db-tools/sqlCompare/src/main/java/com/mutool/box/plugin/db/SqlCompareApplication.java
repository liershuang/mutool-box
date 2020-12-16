package com.mutool.box.plugin.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mutool.box.plugin.db")
public class SqlCompareApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqlCompareApplication.class, args);
	}

}
