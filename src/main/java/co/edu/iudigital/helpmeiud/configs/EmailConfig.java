package co.edu.iudigital.helpmeiud.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {

    private String host;
    private int port;
    private String username;
    private String password;
    private boolean smtpAuth;
    private boolean starttlsEnable;

    // Getters and Setters

    @Value("${spring.mail.host}")
    public void setHost(String host) {
        this.host = host;
    }

    @Value("${spring.mail.port}")
    public void setPort(int port) {
        this.port = port;
    }

    @Value("${spring.mail.username}")
    public void setUsername(String username) {
        this.username = username;
    }

    @Value("${spring.mail.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Value("${spring.mail.properties.mail.smtp.auth}")
    public void setSmtpAuth(boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    public void setStarttlsEnable(boolean starttlsEnable) {
        this.starttlsEnable = starttlsEnable;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", smtpAuth);
        properties.put("mail.smtp.starttls.enable", starttlsEnable);
        properties.put("mail.debug", "true");

        return javaMailSender;
    }
}
