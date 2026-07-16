package dev.kostya.ticketplatform.auth.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String accessSecret;
    private String refreshSecret;
    private long accessExpiration;
    private long refreshExpiration;
    private Cookie cookie = new Cookie();

    @Getter @Setter
    public static class Cookie {
        private String accessName;
        private String refreshName;
        private String accessPath;
        private String refreshPath;
        private boolean secure;
        private boolean httpOnly;
        private String sameSite;
    }
}
