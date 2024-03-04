package com.example.isolatedcontext;

import com.example.isolatedcontext.service.internal.context.OptionalContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
            .initializers(new OptionalContextInitializer())
            .run(args);
    }

}
