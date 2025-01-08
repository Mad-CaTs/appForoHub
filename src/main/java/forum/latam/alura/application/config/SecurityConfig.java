package forum.latam.alura.application.config;


import forum.latam.alura.application.config.filter.JwtTokenValidator;
import forum.latam.alura.infrastructure.services.auth.CustomUserDetailsService;
import forum.latam.alura.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

//configuration of my security filter chain

    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {

                    http.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll();
                    http.requestMatchers(HttpMethod.GET,"/api/roles/single").authenticated();
                    http.requestMatchers("/api/permissions/**").hasAnyRole( "DEVELOPER", "ADMIN");
                    http.requestMatchers("/api/roles/**").hasAnyRole( "DEVELOPER", "ADMIN");
                    http.requestMatchers("/api/users/**").authenticated();
                    http.requestMatchers(HttpMethod.POST,"/api/users/*/add-role").hasRole("ADMIN");
                    http.requestMatchers(HttpMethod.DELETE, "/api/users/*/remove-role").hasRole("ADMIN");
                    http.requestMatchers( "/api/forums/**").authenticated();
                    http.requestMatchers( "/api/messages/**").authenticated();

                    http.anyRequest().denyAll();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }




}

