package com.auth.infrastructure.security;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Configuration
    @Order(1)
    public class JwksSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .inMemoryAuthentication()
                    .withUser("systemUser")
                    .password("{noop}systemPassword")
                    .roles("SYSTEM");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/.well-known/jwks.json")
                    .authorizeRequests()
                    .anyRequest().hasRole("SYSTEM")
                    .and()
                    .httpBasic()
                    .and()
                    .cors()
                    .and()
                    .csrf().disable()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .addFilterBefore(new BasicAuthenticationFilter(super.authenticationManagerBean()) {
                        @Override
                        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                FilterChain chain)
                                throws IOException, ServletException {

                            Enumeration<String> headerNames = request.getHeaderNames();

                            if (headerNames != null) {
                                while (headerNames.hasMoreElements()) {
                                    System.out.println(
                                            "Header: " + request.getHeader(headerNames.nextElement()));
                                }
                            }

                            System.out.println(request.getAuthType());
                            System.out.println(request.getAttributeNames());
                            System.out.println(request.getPathInfo());
                            System.out.println(request.getQueryString());
                            System.out.println(request.getRequestURI());
                            System.out.println(request.getAuthType());
                        }
                    }, BasicAuthenticationFilter.class);
        }
    }

    @Configuration
    @Order(2)
    public class GeneralSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private KeyPair keyPair;

        @Bean
        protected AuthenticationManager getAuthenticationManager() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        protected PasswordEncoder passwordEncoder() {
            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/**")
                    .cors().and()
                    .csrf().disable()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .addFilterBefore(new JWTFilter(getAuthenticationManager(), keyPair), JWTFilter.class);
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
                    .antMatchers("/auth/register/**");
        }
    }

}
