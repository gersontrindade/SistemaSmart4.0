package com.tecdes.sistema_bancada.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

public class ExternalFileEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "externalConfig";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String path = environment.getProperty("db.config.path");
        System.out.println(">> Carregando arquivo externo: " + path);

        if (path != null) {
            try (FileInputStream fis = new FileInputStream(path)) {
                Properties props = new Properties();
                props.load(fis);
                environment.getPropertySources()
                           .addFirst(new PropertiesPropertySource(PROPERTY_SOURCE_NAME, props));
                System.out.println("✔ Propriedades carregadas com sucesso.");
            } catch (IOException e) {
                throw new RuntimeException("Erro ao carregar propriedades de: " + path, e);
            }
        }
    }
}
