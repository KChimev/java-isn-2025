package bg.unisofia.fmi.electronicstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Electronic Store API")
                .version("1.0")
                .description("API for online electronics store with concurrent purchase handling")
                .contact(new Contact()
                    .name("FMI Sofia University")
                    .email("contact@fmi.uni-sofia.bg")));
    }
}
