package tsad.media.provider.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private final ServerProperties serverProperties;

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    public OpenApiConfig(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        String appVersion = System.getenv("VERSION");

        Info infoConfig = new Info()
                .title("Media Server API")
                .description("TSAD - Media Server")
                .version(appVersion);

        int port = serverProperties.getPort() != null ? serverProperties.getPort() : 8080;

        List<Server> servers = List.of(
                new Server().url(String.format("http://localhost:%d", port)).description("localhost"),
                new Server().url("https://astral-containers.com/tsad/media").description("dev"),
                new Server().url("https://astral-containers.com/tsad/media").description("uat")
        );

        return new OpenAPI()
                .info(infoConfig)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .servers(servers);
    }
}
