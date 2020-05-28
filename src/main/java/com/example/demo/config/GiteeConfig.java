package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("oauth2.gitee")
public class GiteeConfig {

    private AuthorizationCodeResourceDetails client;
    private String userInfoUri;
}
