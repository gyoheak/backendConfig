package metaverse.backend.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
@PropertySource("classpath:application.properties")
public class PostgreSqlConfig {
    @Value("${spring.datasource.url}")
    private String DBUri;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public Connection getPostgreSqlConnection() throws Exception {
        return DriverManager.getConnection(DBUri, userName, password);
    }
}
