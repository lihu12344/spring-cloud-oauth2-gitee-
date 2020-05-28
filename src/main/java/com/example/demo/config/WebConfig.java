package com.example.demo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login/form").setViewName("login.html");
    }

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> initFilterRegistrationBean(OAuth2ClientContextFilter oAuth2ClientContextFilter){
        FilterRegistrationBean<OAuth2ClientContextFilter> registrationBean=new FilterRegistrationBean<>();
        registrationBean.setFilter(oAuth2ClientContextFilter);
        registrationBean.setOrder(-100);

        return registrationBean;
    }

    @Bean
    public RestTemplate initRestTemplate(){
        return new RestTemplate();
    }
}
