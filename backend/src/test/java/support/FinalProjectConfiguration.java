package support;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@TestConfiguration
public class FinalProjectConfiguration {
    @Bean
    public DataSource dataSource() throws ManagedProcessException {
        var configBuilder = DBConfigurationBuilder.newBuilder();
        configBuilder.setPort(0);

        var db = DB.newEmbeddedDB(configBuilder.build());
        db.start();
        db.createDB("test", "root", "");

        return DataSourceBuilder.create()
                .url("jdbc:mariadb://localhost:" + db.getConfiguration().getPort() + "/test")
                .username("root")
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
}