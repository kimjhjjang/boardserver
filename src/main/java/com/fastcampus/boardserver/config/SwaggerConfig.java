package com.fastcampus.boardserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Board Server API")
                        .description("대규모 트래픽 게시판 API 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("FastCampus")
                                .email("support@fastcampus.co.kr")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("개발 서버")
                ));
    }
}