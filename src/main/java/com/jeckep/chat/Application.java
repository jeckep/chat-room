package com.jeckep.chat;


import com.jeckep.chat.login.oauth.*;
import com.jeckep.chat.login.oauth.vk.OAuth2ClientAuthenticationProcessingFilter;
import com.jeckep.chat.login.oauth.vk.UserInfoTokenServices;
import com.jeckep.chat.login.oauth.vk.VkUserInfoModifier;
import com.jeckep.chat.service.UserService;
import com.jeckep.chat.util.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootApplication
@EnableOAuth2Client
public class Application extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Autowired
    UserService userService;

    public static void main(String[] args) {
        info();
        SpringApplication.run(Application.class, args);
    }

    public static void info(){
        log.info("freeMemory:" + Runtime.getRuntime().freeMemory()/1024/1204 + "Mb");
        log.info("totalMemory:" + Runtime.getRuntime().totalMemory()/1024/1204 + "Mb");
        log.info("maxMemory:" + Runtime.getRuntime().maxMemory()/1024/1204 + "Mb");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**", "/webjars/**"
                ,Path.Web.LOGIN, "/fonts/**", Path.Web.CONTACT, Path.Web.LOGOUT, Path.Web.PAYMENTS).permitAll().anyRequest()
                .authenticated().and().exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(Path.Web.LOGIN)).and().logout()
                .logoutSuccessUrl("/").permitAll().and().csrf().disable()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(vk(), Path.Web.LOGIN_VK));
        filters.add(ssoFilter(google(), Path.Web.LOGIN_GOOGLE));
        filters.add(ssoFilter(facebook(), Path.Web.LOGIN_FB));
        filters.add(ssoFilter(github(), Path.Web.LOGIN_GITHUB));
        filters.add(ssoFilter(linkedin(), Path.Web.LOGIN_LINKEDIN));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices userInfoTokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        userInfoTokenServices.setPrincipalExtractor(client.getPrincipalExtractor());
        userInfoTokenServices.setUserInfoRequestCreator(client.getUserInfoRequestCreator());
        userInfoTokenServices.setModifier(client.getModifier());
        filter.setTokenServices(userInfoTokenServices);
        return filter;
    }

    @Bean
    @ConfigurationProperties("vk")
    public ClientResources vk() {
        return new ClientResources(new VkPrincipalExtractor(userService), new VkUserInfoModifier(), new VkUserInfoModifier());
    }

    @Bean
    @ConfigurationProperties("google")
    public ClientResources google() {
        return new ClientResources(new GooglePrincipalExtractor(userService));
    }

    @Bean
    @ConfigurationProperties("github")
    public ClientResources github() {
        return new ClientResources(new GithubPrincipalExtractor(userService));
    }

    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        return new ClientResources(new FbPrincipalExtractor(userService));
    }

    @Bean
    @ConfigurationProperties("linkedin")
    public ClientResources linkedin() {
        return new ClientResources(new LdPrincipalExtractor(userService));
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }
}