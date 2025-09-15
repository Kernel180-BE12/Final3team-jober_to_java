package com.example.final_projects.config.swagger;

import com.example.final_projects.dto.ApiResult;
import com.example.final_projects.exception.code.BaseErrorCode;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Jober API")
                .version("1.0")
                .description("Jober API Documentation");

        String jwtSchemeName = "jwtAuth";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    @Bean
    public OperationCustomizer errorCodeCustomizer() {
        return (operation, handlerMethod) -> {
            ApiErrorCodeExample annotation = handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);
            if (annotation != null) {
                addErrorCodeExamples(operation.getResponses(), annotation.value());
            }
            return operation;
        };
    }

    private void addErrorCodeExamples(ApiResponses responses, Class<? extends BaseErrorCode> errorCodeClass) {
        BaseErrorCode[] errorCodes = errorCodeClass.getEnumConstants();

        Map<Integer, List<BaseErrorCode>> groupedByStatus = Arrays.stream(errorCodes)
                .collect(Collectors.groupingBy(ec -> ec.getErrorReason().getStatus()));

        groupedByStatus.forEach((status, list) -> {
            Content content = new Content();
            MediaType mediaType = new MediaType();

            for (BaseErrorCode ec : list) {
                Example example = new Example();
                example.setSummary(ec.getErrorReason().getMessage());

                example.setValue(ApiResult.error(
                        ec.getErrorReason().getCode(),
                        ec.getErrorReason().getMessage()
                ));

                mediaType.addExamples(ec.getErrorReason().getCode(), example);
            }

            content.addMediaType("application/json", mediaType);
            ApiResponse apiResponse = new ApiResponse().content(content);
            responses.addApiResponse(String.valueOf(status), apiResponse);
        });
    }
}
