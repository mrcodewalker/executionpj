package com.example.zero2dev.configurations;

import com.example.zero2dev.repositories.UserRepository;
import com.example.zero2dev.storage.MESSAGE;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private String apiPrefix = "api/v1";
    private final UserRepository userRepository;
//    private Dotenv dotenv = Dotenv.configure()
//            .directory(".") // Chỉ định thư mục chứa file .env
//            .filename("SYSTEM32.env") // Tên file .env
//            .load();
    @Bean
    public UserDetailsService userDetailsService(){
        return username ->
                (UserDetails) userRepository.getUserByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        ClientRegistration googleClient = ClientRegistration.withRegistrationId("google")
//                .clientId(dotenv.get("GOOGLE_CLIENT_ID"))  // Đọc từ .env
//                .clientSecret(dotenv.get("GOOGLE_CLIENT_SECRET"))  // Đọc từ .env
//                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
//                .tokenUri("https://oauth2.googleapis.com/token")
//                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
//                .redirectUri("https://www.laptopaz.id.vn/login/oauth2/code/google")
//                .scope("openid", "profile", "email")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
//                .userNameAttributeName("sub")
//                .build();
//
//        ClientRegistration facebookClient = ClientRegistration.withRegistrationId("facebook")
//                .clientId(dotenv.get("FACEBOOK_CLIENT_ID"))  // Đọc từ .env
//                .clientSecret(dotenv.get("FACEBOOK_CLIENT_SECRET"))  // Đọc từ .env
//                .authorizationUri("https://www.facebook.com/v17.0/dialog/oauth")
//                .tokenUri("https://graph.facebook.com/v17.0/oauth/access_token")
//                .userInfoUri("https://graph.facebook.com/me?fields=id,name,email")
//                .redirectUri("https://www.laptopaz.id.vn/login/oauth2/code/facebook")
//                .scope("public_profile", "email")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .userNameAttributeName("id")
//                .build();
//
//        ClientRegistration githubClient = ClientRegistration.withRegistrationId("github")
//                .clientId(dotenv.get("GITHUB_CLIENT_ID"))  // Đọc từ .env
//                .clientSecret(dotenv.get("GITHUB_CLIENT_SECRET"))  // Đọc từ .env
//                .authorizationUri("https://github.com/login/oauth/authorize")
//                .tokenUri("https://github.com/login/oauth/access_token")
//                .userInfoUri("https://api.github.com/user")
//                .redirectUri("https://www.laptopaz.id.vn/login/oauth2/code/github")
//                .scope("user:email")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .userNameAttributeName("login")
//                .build();
//
//        return new InMemoryClientRegistrationRepository(Arrays.asList(googleClient, facebookClient, githubClient));
//    }
//
//    @Bean
//    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
//        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
//    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}