package estagio.opus.beanbalance;

import org.springframework.boot.SpringApplication;

public class TestBeanbalanceApplication {

	public static void main(String[] args) {
		SpringApplication.from(BeanbalanceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
