package com.baasexample.demo;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@MapperScan(value = {"com.baasexample.demo.Mapper"})
public class DemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);
    }

}
