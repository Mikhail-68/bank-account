package ru.egorov.bankaccount.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Bank Account Api",
                description = "Bank Account", version = "1.0.0",
                contact = @Contact(
                        name = "Egorov Mikhail",
                        email = "misha2003200@gmail.com"
                )
        )
)
public class OpenApiConfiguration {
}
