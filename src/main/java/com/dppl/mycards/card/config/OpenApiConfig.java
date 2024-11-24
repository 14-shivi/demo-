package com.dppl.mycards.card.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.headers.Header;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dppl.mycards.card.service.dto.RequestDTO;

@Configuration
public class OpenApiConfig {
	
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components().addHeaders("authToken", new Header().description("JWT Authorization Token")
            		.schema(new Schema<String>().type("string"))))
            .path("/api/users/login", new PathItem().post(
                new Operation()
                    .addParametersItem(new Parameter()
                        .in("header")
                        .name("requestId")
                        .description("UUID for each request")
                        .required(true)
                        .schema(new Schema<String>().type("string"))
                    )
                    .requestBody(new RequestBody().content(
                        new Content().addMediaType("application/json",
                            new MediaType()
                                .schema(new Schema<RequestDTO>())
                                .example(new Example().value(OpenApiConfigConstants.USER_LOGIN_REQUEST))
                        )
                    ))
            ));
    }
}
