package br.com.dio.lib_padroes_projeto_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class LibPadroesProjetoSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibPadroesProjetoSpringApplication.class, args);
	}

}
