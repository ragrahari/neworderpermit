package domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class H2SpringApp implements CommandLineRunner{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	PermitJdbcRepository repository;
	
	public static void main(String args[]) {
		SpringApplication.run(H2SpringApp.class, args);
	}
	
	public void run(String args[]) throws Exception {
		logger.info("Inside run method");
	}
}
