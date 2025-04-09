package org.main;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.Scanner;

@Configuration
@ComponentScan(basePackages = "org.main")
public class AppConfig {
    private static final String DB_URL = "jdbc:sqlite:chatbot.db";
    private final String credentialsProfileFile;
    private final String credentialProfile;

    public AppConfig(
            @Value("${aws.credentials.file.path}") String credentialsFilePath,
            @Value("${aws.profile.name}") String credentialProfile) {
        this.credentialsProfileFile = credentialsFilePath;
        this.credentialProfile = credentialProfile;
    }

    /**
     * Defines the DataSource bean.
     *
     * @return DataSource
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl(DB_URL);
        // SQLite does not require username/password
        return dataSource;
    }

    /**
     * Defines the JdbcTemplate bean.
     *
     * @param dataSource the DataSource
     * @return JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Bean
    public BedrockRuntimeAsyncClient bedrockRuntimeAsyncClient() {
        if (this.credentialsProfileFile == null || this.credentialsProfileFile.isEmpty()) {
            return BedrockRuntimeAsyncClient.builder()
                    .region(Region.US_EAST_1)
                    .build();
        } else {
            AwsCredentialsProvider credentialsProvider = ProfileCredentialsProvider.builder()
                    .profileFile(ProfileFile.builder()
                            .content(Path.of(this.credentialsProfileFile))
                            .type(ProfileFile.Type.CREDENTIALS)
                            .build())
                    .profileName(this.credentialProfile)
                    .build();

            return BedrockRuntimeAsyncClient.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(Region.US_EAST_1)
                    .build();
        }
    }

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migrations")
                .load();
    }
}
