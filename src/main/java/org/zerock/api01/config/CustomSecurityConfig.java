package org.zerock.api01.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zerock.api01.security.APIUserDetailsService;
import org.zerock.api01.security.filter.APILoginFilter;
import org.zerock.api01.security.filter.RefreshTokenFilter;
import org.zerock.api01.security.filter.TokenCheckFilter;
import org.zerock.api01.security.handler.APILoginSuccessHandler;
import org.zerock.api01.util.JWTUtil;

import javax.sql.DataSource;
import java.util.List;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {
  private final PasswordEncoder passwordEncoder;
  private final APIUserDetailsService apiUserDetailsService;
  private final JWTUtil jwtUtil;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.info("----------------Security Configure----------------");

    //AuthenticationManager 설정
    AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);

    authenticationManagerBuilder
            .userDetailsService(apiUserDetailsService)
            .passwordEncoder(passwordEncoder);

    // Get AuthenticationManager
    AuthenticationManager authenticationManager =
            authenticationManagerBuilder.build();

    //APILoginFilter
    APILoginFilter apiLoginFilter = new APILoginFilter(("/generateToken"));
    apiLoginFilter.setAuthenticationManager(authenticationManager);

    //APILoginSuccessHandler
    APILoginSuccessHandler successHandler = new APILoginSuccessHandler(jwtUtil);
    //SuccessHandler 세팅
    apiLoginFilter.setAuthenticationSuccessHandler(successHandler);

    http
            .csrf(AbstractHttpConfigurer::disable)

            // 세션 사용 x
            .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationManager(authenticationManager) // 반드시 필요

            //APiLoginFilter 위치 조정
            .addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class
            ) //username password 처리하는 필터의 앞에 apiLoginFilter

            //api로 시작하는 모든 경로는 TokenCheckFilter 동작
            //873p jwt검증 후 스프링시큐리티를 사용하기 위해 인증정보 구성
            .addFilterBefore(             
                    tokenCheckFilter(jwtUtil, apiUserDetailsService),
                    UsernamePasswordAuthenticationFilter.class
            )

            //refreshToken 호출처리
            .addFilterBefore(new RefreshTokenFilter("/refreshToken", jwtUtil),
                    TokenCheckFilter.class)

            .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(
                    corsConfigurationSource())
            );
    
    return http.build();
  }


  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    log.info("----------------WebSecurity Customizer----------------");

    return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
  }
  
  //810p 발행/재발행 토큰 채크
  private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil, APIUserDetailsService apiUserDetailsService) {
    return new TokenCheckFilter(apiUserDetailsService, jwtUtil);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

}
