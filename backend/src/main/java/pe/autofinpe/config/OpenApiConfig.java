package pe.autofinpe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String JWT_SECURITY_SCHEME = "bearer-jwt";

    @Bean
    public OpenAPI autoFinPeOpenApi() {
        return new OpenAPI()
                .info(apiInfo())
                .components(new Components().addSecuritySchemes(JWT_SECURITY_SCHEME, jwtSecurityScheme()))
                .addSecurityItem(new SecurityRequirement().addList(JWT_SECURITY_SCHEME));
    }

    private Info apiInfo() {
        return new Info()
                .title("AutoFinPe API")
                .description("Sistema de Gestion y Simulacion de Credito Vehicular Compra Inteligente")
                .version("1.0.0")
                .contact(new Contact().name("Equipo AutoFinPe"));
    }

    private SecurityScheme jwtSecurityScheme() {
        return new SecurityScheme()
                .name(JWT_SECURITY_SCHEME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
