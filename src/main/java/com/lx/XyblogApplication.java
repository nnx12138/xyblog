package com.lx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.lx.blog.dao")
@SpringBootApplication
public class XyblogApplication {
    public static void main(String[] args) {
        SpringApplication.run(XyblogApplication.class, args);
    }


}
