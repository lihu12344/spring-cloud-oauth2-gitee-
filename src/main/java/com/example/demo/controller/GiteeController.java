package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.GiteeConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class GiteeController {

    @Resource
    private GiteeConfig giteeConfig;

    @Resource
    private RestTemplate restTemplate;

    @RequestMapping(value = "/test")
    public String test(String code){
        return code;
    }

    @RequestMapping(value = "/oauth/token")
    public OAuth2AccessToken accessToken(String code, String state){
        Map<String,String> params=new HashMap<>();

        params.put("client_id","0e2cae5e8c1498c4276b113355c815b265428e68a189bd75025e1bd7e9e4bbc1");
        params.put("client_secret","a375bffedce8119c9ebba0a7896ed070e6406355f1c143f8a045c58cafc4b9ee");
        params.put("grant_type","authorization_code");
        params.put("redirect_uri","http://localhost:8080/login/gitee");
        params.put("code",code);
        System.out.println("state："+state);

        HttpHeaders headers=new HttpHeaders();
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        //headers.add("User-Agent",request.getHeader("User-Agent"));

        HttpEntity<Map<String,String>> entity=new HttpEntity<>(params,headers);
        String result=restTemplate.postForObject("https://gitee.com/oauth/token",entity,String.class);
        System.out.println(result);

        JSONObject object= JSONObject.parseObject(result);
        System.out.println("map："+object.getInnerMap());

        String accessToken=object.getString("access_token");
        String refreshToken=object.getString("refresh_token");
        String expiresIn=object.getString("expires_in");
        String tokenType=object.getString("token_type");

        String scope=object.getString("scope");
        Set<String> scopes = new HashSet<>(Arrays.asList(scope.split(" ")));

        System.out.println("scope："+object.getString("scope"));
        System.out.println("created_at："+object.getString("created_at"));
        System.out.println("token_type："+object.getString("token_type"));
        System.out.println("expires_in："+object.getString("expires_in"));

        DefaultOAuth2AccessToken oAuth2AccessToken=new DefaultOAuth2AccessToken(accessToken);
        oAuth2AccessToken.setRefreshToken(new DefaultOAuth2RefreshToken(refreshToken));
        oAuth2AccessToken.setTokenType(tokenType);
        oAuth2AccessToken.setExpiration(new Date(Long.parseLong(expiresIn)));
        oAuth2AccessToken.setScope(scopes);

        return oAuth2AccessToken;
    }

    @RequestMapping("/user")
    public String getUser2(HttpServletRequest request) throws Exception {
        Map<String,String> params=new HashMap<>();
        params.put("access_token",request.getParameter("accessToken"));

        HttpHeaders httpHeaders=new HttpHeaders();
        //httpHeaders.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        httpHeaders.add("User-Agent",request.getHeader("User-Agent"));

        HttpEntity<Object> entity=new HttpEntity<>(httpHeaders);

        return restTemplate.exchange("https://gitee.com/api/v5/user?access_token={access_token}", HttpMethod.GET,entity,String.class,params).getBody();
    }
}
