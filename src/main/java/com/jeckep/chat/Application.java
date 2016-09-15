package com.jeckep.chat;


import com.jeckep.chat.model.User;
import com.jeckep.chat.repository.UserService;
import com.jeckep.chat.util.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootApplication
@EnableOAuth2Client
public class Application extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Autowired
    UserService userService;

    public static void main(String[] args) {
//        info();
        SpringApplication.run(Application.class, args);
    }

    public static void info(){
        log.info("freeMemory:" + Runtime.getRuntime().freeMemory()/1024/1204 + "Mb");
        log.info("totalMemory:" + Runtime.getRuntime().totalMemory()/1024/1204 + "Mb");
        log.info("maxMemory:" + Runtime.getRuntime().maxMemory()/1024/1204 + "Mb");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // @formatter:off
        http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**", "/webjars/**"
                ,Path.Web.LOGIN, "/fonts/**", Path.Web.CONTACT, Path.Web.LOGOUT).permitAll().anyRequest()
                .authenticated().and().exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(Path.Web.LOGIN)).and().logout()
                .logoutSuccessUrl("/").permitAll().and().csrf().disable()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
        // @formatter:on
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
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
        filter.setTokenServices(userInfoTokenServices);
        return filter;
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

    class ClientResources {
        private PrincipalExtractor principalExtractor;

        public ClientResources(PrincipalExtractor principalExtractor) {
            this.principalExtractor = principalExtractor;
        }

        @NestedConfigurationProperty
        private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

        @NestedConfigurationProperty
        private ResourceServerProperties resource = new ResourceServerProperties();

        public AuthorizationCodeResourceDetails getClient() {
            return client;
        }

        public ResourceServerProperties getResource() {
            return resource;
        }

        public PrincipalExtractor getPrincipalExtractor() {
            return principalExtractor;
        }
    }

    public static class GithubPrincipalExtractor implements PrincipalExtractor {
        final UserService userService;

        public GithubPrincipalExtractor(UserService userService) {
            this.userService = userService;
        }

        @Override
        public Object extractPrincipal(Map<String, Object> map) {
            final String name =  ((String) map.get("name")).split("\\s")[0];
            final String surname = ((String) map.get("name")).split("\\s")[1];
            final String email = (String) map.get("email");
            final String picture = (String) map.get("avatar_url");

            return userService.findOrCreate(new User(name, surname, email, picture));
        }
    }

    public static class FbPrincipalExtractor implements PrincipalExtractor {
        final UserService userService;

        public FbPrincipalExtractor(UserService userService) {
            this.userService = userService;
        }

        @Override
        public Object extractPrincipal(Map<String, Object> map) {
            final String name =  (String) map.get("first_name");
            final String surname = (String) map.get("last_name");
            final String email = (String) map.get("email");
            final String picture = (String) ((Map) ((Map) map.get("picture")).get("data")).get("url");

            return userService.findOrCreate(new User(name, surname, email, picture));
        }
    }

    public static class LdPrincipalExtractor implements PrincipalExtractor {
        final UserService userService;

        public LdPrincipalExtractor(UserService userService) {
            this.userService = userService;
        }

        @Override
        public Object extractPrincipal(Map<String, Object> map) {
            final String name =  (String) map.get("firstName");
            final String surname = (String) map.get("lastName");
            final String email = (String) map.get("emailAddress");
            final String picture = (String) map.get("pictureUrl");

            return userService.findOrCreate(new User(name, surname, email, picture));
        }
    }
}