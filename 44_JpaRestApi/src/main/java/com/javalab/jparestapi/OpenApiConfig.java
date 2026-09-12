package com.javalab.jparestapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** springdoc-openapiが自動生成するAPI仕様書のタイトル・説明・バージョンをカスタマイズする。 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jpaRestApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("商品在庫管理API")
                        .description("44_JpaRestApi: Spring Data JPAで永続化する商品在庫管理REST API")
                        .version("v1"));
    }
}
