package org.zerock.api01.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  
  // springfox 방법
//  @Bean
//  public Docket api() {
//    return new Docket(DocumentationType.OAS_30)
//            .useDefaultResponseMessages(false)
//            .select()
//            .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
//            .paths(PathSelectors.any())
//            .build()
//            .apiInfo(apiInfo());
//
//  }
//
//  private ApiInfo apiInfo() {
//    return new ApiInfoBuilder()
//            .title("Boot 01 Project Swagger")
//            .build();
//  }

  
  //springdoc 방법
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
            .group("public-apis")
            .pathsToMatch("/**")
            .build();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
            .info(new Info()
                    .title("Boot 01 Project API")
                    .version("1.0")
                    .description("API documentation for Boot 01 Project"))
            .addSecurityItem(new SecurityRequirement().addList("Authorization"))
            .components(new Components()
                    .addSecuritySchemes("Authorization", new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")));
  }

}
