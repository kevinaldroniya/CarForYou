package com.car.foryou.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class EmailTemplateLoader {

    private final ResourceLoader resourceLoader;

    public EmailTemplateLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String loadTemplate(String templateName){
       try {
           Resource resource = resourceLoader.getResource("classpath:templates/notification/email/" + templateName);
           return new String(Files.readAllBytes(Path.of(resource.getURI())));
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }
}
