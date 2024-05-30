package com.bjtu.warehousesystemwithwebflux.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2023/3/4 21:55
 * @since:knife4j-openapi3-spring-webflux-demo
 */
@Configuration
public class WebFluxKnife4jConfig {

//    @Bean
//    public GroupedOpenApi tweetsOpenApi(@Value("${springdoc.version}") String appVersion) {
//        String[] paths = { "/user/**" };
//        return GroupedOpenApi.builder().
//                group("user")
//                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("user Api").version(appVersion)))
//                .pathsToMatch(paths)
//                .build();
//    }

    @Bean
    public GroupedOpenApi userApi(@Value("${springdoc.version}") String appVersion) {
        return GroupedOpenApi.builder()
                .group("user")
                .packagesToScan("com.bjtu.warehousesystemwithwebflux") // 扫描包含接口的包路径
                .build();
    }

    @Bean
    public GroupedOpenApi streamOpenApi(@Value("${springdoc.version}") String appVersion) {
        String[] paths = { "/stream/**" };
        String[] packagedToMatch = { "com.github.xiaoymin" };
        return GroupedOpenApi.builder().group("x-stream")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("Stream API").version(appVersion)))
                .pathsToMatch(paths).packagesToScan(packagedToMatch)
                .build();
    }

}
