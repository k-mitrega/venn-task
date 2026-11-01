package org.example.venn;

import org.example.venn.config.UsageLimits;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({UsageLimits.class})
public class VennApplication {

    public static void main(String[] args) {
        if (args.length > 0) {
            final var context = new SpringApplicationBuilder(VennApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
            SpringApplication.exit(context);
            return;
        }
        SpringApplication.run(VennApplication.class, args);
    }
}
