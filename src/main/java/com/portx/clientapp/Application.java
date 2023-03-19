package com.portx.clientapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        List<String> arguments = Arrays.stream(args)
                .filter(arg -> arg.contains("paymentRequestCount"))
                .collect(Collectors.toList());
        if (!arguments.isEmpty()) {
            String[] split = arguments.get(0).split("=");
            System.getProperties().setProperty(split[0], split[1]);
        }
        SpringApplication.run(Application.class, args);
    }
}
