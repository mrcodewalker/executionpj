package com.example.zero2dev.configurations;

import com.example.zero2dev.filter.JwtTokenFilter;
import com.example.zero2dev.handler.CustomAccessDeniedHandler;
import com.example.zero2dev.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Ensure this is added
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
//                                CERTIFICATE
                                .requestMatchers("/api/v1/certificate/**").hasAnyRole(Role.fullRoleAccess())
//                                FRAME
                                .requestMatchers("/api/v1/frame/create").hasRole(Role.adminAccess())
                                .requestMatchers("/api/v1/frame/**").hasRole(Role.adminAccess())
                                .requestMatchers("/api/v1/frame/assign/**").hasRole(Role.adminAccess())
                                .requestMatchers("/api/v1/frame/current").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/frame/toggle/**").hasRole(Role.adminAccess())
                                .requestMatchers("/api/v1/frame/apply").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/frame/active").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/frame/purchase").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/frame/filter").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/frame/image/**").permitAll()
//                                TOKEN
                                .requestMatchers("/api/v1/auth/verify").permitAll()
                                .requestMatchers("/api/v1/auth/logout").permitAll()
                                .requestMatchers("/api/v1/user/verify").permitAll()
                                .requestMatchers("/api/v1/user/image/**").permitAll()
                                .requestMatchers("/api/v1/user/forgot-password").permitAll()
                                .requestMatchers("/api/v1/user/reset-password").permitAll()
                                .requestMatchers("/api/v1/token/filter").hasRole(Role.adminAccess())
//                                REFRESH TOKEN
                                .requestMatchers("/api/v1/refresh_token/filter").hasRole(Role.adminAccess())
//                                BLACKLISTED TOKEN
                                .requestMatchers("/api/v1/blacklisted_token/filter").hasRole(Role.adminAccess())
//                                BLACKLISTED IP
                                .requestMatchers("/api/v1/blacklisted_ip/filter").hasRole(Role.adminAccess())
                                .requestMatchers("/api/v1/blacklisted_ip/unban/**").hasRole(Role.adminAccess())
                                .requestMatchers("/api/v1/blacklisted_ip/**").hasRole(Role.adminAccess())
//                                USER
                                .requestMatchers("/api/v1/user/register").permitAll()
                                .requestMatchers("/api/v1/user/login").permitAll()
                                .requestMatchers("/api/v1/user/login").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/user/ban").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/user/list/available").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/user/list/unavailable").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/user/collect/list").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/user/list/highest").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/user/info**").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/user/filter**").hasAnyRole(Role.adminAccess())
//                               CATEGORY
                                .requestMatchers("/api/v1/category/create").hasRole(Role.adminAccess())
                                .requestMatchers("/api/v1/category/update").hasRole(Role.adminAccess())
                                .requestMatchers("/api/v1/category/get/**").permitAll()
                                .requestMatchers("/api/v1/category/filter/list/**").permitAll()
                                .requestMatchers("/api/v1/category/delete/**").hasRole(Role.adminAccess())
//                              CODE STORAGE
                                .requestMatchers("/api/v1/code/storage/create").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/code/storage/info").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/code/storage/filter/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/code/storage/delete/**").hasAnyRole(Role.adminAccess())
//                              CONTEST
                                .requestMatchers("/api/v1/contest/filter/all").permitAll()
                                .requestMatchers("/api/v1/contest/create").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/contest/get**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/contest/available/contest**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/contest/delete/**").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/contest/update/**").hasAnyRole(Role.adminAccess())
//                              CONTEST PARTICIPANT
                                .requestMatchers("/api/v1/participant/create").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/participant/filter/contest/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/participant/filter/user/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/participant/delete/**").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/participant/update/**").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/participant/filter/key").hasAnyRole(Role.fullRoleAccess())
//                              LANGUAGE
                                .requestMatchers("/api/v1/language/create").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/language/get/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/language/get/list").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/language/delete/**").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/language/update/**").hasAnyRole(Role.adminAccess())
//                              PROBLEM
                                .requestMatchers("/api/v1/problem/create").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/problem/update/**").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/problem/delete/**").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/problem/get/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/problem/search").hasAnyRole(Role.fullRoleAccess())
//                               ROLE
                                .requestMatchers("/api/v1/role/create").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/role/filter/**").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/role/update/**").hasAnyRole(Role.adminAccess())
                                .requestMatchers("/api/v1/role/delete/**").hasAnyRole(Role.adminAccess())
//                              SUBMISSION
                                .requestMatchers("/api/v1/submission/create").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/submission/filter/ranking").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/submission/problem/solved/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/submission/ranking/contest/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/submission/collect/user/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/submission/filter/submit").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/submission/collect/problem/**").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/submission/collect/language/**").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/submission/collect/status/**").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/submission/collect/memory/lowest").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/submission/collect/exec_time/lowest").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/submission/delete/**").hasAnyRole(Role.adminAccess())
//                              POST
                                .requestMatchers("/api/v1/post/create").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/post/update/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/post/delete/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/post/filter/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/post/filter").hasAnyRole(Role.fullRoleAccess())
//                              COMMENT
                                .requestMatchers("/api/v1/comment/create").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/comment/update/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/comment/delete/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/comment/filter/**").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/comment/filter").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/api/v1/comment/paging").hasAnyRole(Role.fullRoleAccess())
//                              LIKE
                                .requestMatchers("/api/v1/like/toggle").hasAnyRole(Role.fullRoleAccess())
//                              TEST CASE READER
                                .requestMatchers("/api/v1/test_case/create").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/test_case/problem").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/test_case/position/**").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/test_case/update").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/test_case/delete").hasAnyRole(Role.developerAccess())
//                              TEST CASE
                                .requestMatchers("/api/v1/test_cases/create").hasAnyRole(Role.developerAccess())
                                .requestMatchers("/api/v1/test_cases/**").hasAnyRole(Role.developerAccess())

                                .requestMatchers("/api/v1/submission/create").hasAnyRole(Role.fullRoleAccess())
                                .requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**").permitAll()
                                .anyRequest().authenticated()
                ); // Yêu cầu xác thực cho các yêu cầu khác

            http.exceptionHandling(exception -> exception
                    .accessDeniedHandler(customAccessDeniedHandler)
            );
//                .oauth2Login(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults());
//                .sessionManagement(sessionManagement ->
//                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Không tạo session
//                );
//        http.oauth2Login(oauth2 ->
//                oauth2
//                        .successHandler(authenticationSuccessHandler())
//        );
//                .addFilterBefore(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // Thêm filter tùy chỉnh của bạn
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("Origin", "Authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }
}
