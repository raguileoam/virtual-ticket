package com.raguileoam.virtualticket.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.raguileoam.virtualticket.security.service.JWTService;
import com.raguileoam.virtualticket.security.service.JpaUserDetailsService;
import com.raguileoam.virtualticket.security.service.filter.JWTAuthenticationFilter;
import com.raguileoam.virtualticket.security.service.filter.JWTAuthorizationFilter;

@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public class SecurityConfig {

    @Autowired
    private JpaUserDetailsService jpaUserDetailsService;

    @Autowired
    private AuthenticationConfiguration authConfig;

    @Autowired
    private JWTService jwtService;

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter() {
        return new JWTAuthorizationFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * It allows to assign username, password (it store encrypted) and user roles.
     * Permite asignar nombre de usuario, contraseña (se almacenada cifrada) y roles
     * a los usuarios.
     * 
     * Reemplaza a configureGlobal
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(jpaUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * It allows to assign the access to specific views for the non-registered users
     * and set the configuration about login and logout.
     * Permite asignar el acceso a vistas específicas para los usuarios no
     * registrados, configurar el login y logout.
     *
     * Reemplaza a configure
     * 
     * @param http It allows configurate the security based in web for specific http
     *             requests.
     *             Permite configurar la seguridad basada en web para solicitudes
     *             http específicas.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui/**", "/data-info/**").permitAll()
                .anyRequest().authenticated().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(new JWTAuthenticationFilter(authenticationManager(), jwtService),
                UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * It allows to encrypt a password using BCrypt.
     * Permite cifrar una clave utilizando BCrypt.
     *
     * @param password Password to encrypt.
     *                 Clave a cifrar.
     * @return Encypted passowrd. Clave cifrada.
     */
    public String encryptPassword(String password) {
        return passwordEncoder().encode(password);
    }

}
