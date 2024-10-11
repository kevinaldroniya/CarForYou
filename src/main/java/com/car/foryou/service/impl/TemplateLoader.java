package com.car.foryou.service.impl;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class TemplateLoader {

    private final ResourceLoader resourceLoader;

    public TemplateLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String loadTemplate(String templateName){
       try {
           Resource resource = resourceLoader.getResource("classpath:templates/" + templateName);
           return new String(Files.readAllBytes(Path.of(resource.getURI())));
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }
}
