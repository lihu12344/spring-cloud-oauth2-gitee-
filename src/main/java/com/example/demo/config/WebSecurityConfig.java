package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private OAuth2ClientContext oAuth2ClientContext;

    @Resource
    private CustomTokenRequestEnhancer tokenRequestEnhancer;

    @Resource
    private GiteeConfig giteeConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login/form").and().authorizeRequests()
                .antMatchers("/hello").hasAuthority("ROLE_USER")
                .antMatchers("/**").permitAll()
                .and().addFilterBefore(initFilter(), BasicAuthenticationFilter.class);
    }

    public Filter initFilter(){
        CompositeFilter compositeFilter=new CompositeFilter();

        List<Filter> filters=new ArrayList<>();
        filters.add(giteeFilter());
        compositeFilter.setFilters(filters);

        return compositeFilter;
    }

    private Filter giteeFilter(){
        OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter=new OAuth2ClientAuthenticationProcessingFilter("/login/gitee");

        AuthorizationCodeAccessTokenProvider authorizationCodeAccessTokenProvider=new AuthorizationCodeAccessTokenProvider();
        authorizationCodeAccessTokenProvider.setTokenRequestEnhancer(tokenRequestEnhancer);
        AccessTokenProviderChain chain=new AccessTokenProviderChain(Collections.singletonList(authorizationCodeAccessTokenProvider));

        OAuth2RestTemplate oAuth2RestTemplate=new OAuth2RestTemplate(giteeConfig.getClient(),oAuth2ClientContext);
        oAuth2RestTemplate.setAccessTokenProvider(chain);
        oAuth2ClientAuthenticationProcessingFilter.setRestTemplate(oAuth2RestTemplate);

        CustomUserInfoTokenServices userInfoTokenServices=new CustomUserInfoTokenServices(giteeConfig.getUserInfoUri(),giteeConfig.getClient().getClientId());
        userInfoTokenServices.setRestTemplate(oAuth2RestTemplate);
        oAuth2ClientAuthenticationProcessingFilter.setTokenServices(userInfoTokenServices);

        return oAuth2ClientAuthenticationProcessingFilter;
    }
}
