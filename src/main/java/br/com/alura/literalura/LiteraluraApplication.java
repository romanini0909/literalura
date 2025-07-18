package br.com.alura.literalura;


import br.com.alura.literalura.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication // Já inclui @ComponentScan para o pacote base
public class LiteraluraApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(LiteraluraApplication.class, args);
    }

    @Autowired
    private Principal principal;

    @Override
    public void run(String... args) throws Exception {
        principal.exibeMenu();
    }
}