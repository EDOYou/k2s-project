package com.edoyou.k2sbeauty;

//import com.edoyou.k2sbeauty.utils.PasswordUpdater;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class K2sbeautyApplication {
	public static void main(String[] args) {
		SpringApplication.run(K2sbeautyApplication.class, args);

//		PasswordUpdater passwordUpdater = context.getBean(PasswordUpdater.class);
//		passwordUpdater.updatePasswords();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}