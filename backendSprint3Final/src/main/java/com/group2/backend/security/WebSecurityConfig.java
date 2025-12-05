package com.group2.backend.security;

import com.group2.backend.model.AppRole;
import com.group2.backend.model.Cookie;
import com.group2.backend.model.Role;
import com.group2.backend.model.User;
import com.group2.backend.repository.CookieRepository;
import com.group2.backend.repository.RoleRepository;
import com.group2.backend.repository.UserRepository;
import com.group2.backend.security.jwt.AuthEntryPointJwt;
import com.group2.backend.security.jwt.AuthTokenFilter;
import com.group2.backend.security.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private CookieRepository cookieRepository;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // ----------------------------
    // JWT Filter
    // ----------------------------
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // ----------------------------
    // Authentication Provider
    // ----------------------------
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // ----------------------------
    // Auth Manager
    // ----------------------------
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // ----------------------------
    // Password Encoder
    // ----------------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ----------------------------
    // CORS CONFIG
    // ----------------------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ALLOW SPECIFIC ORIGINS (Exact matches)
        // We include localhost with and without ports to be safe
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:80",
                "http://localhost",
                "http://127.0.0.1"
        ));

        // ALLOW ALL METHODS (GET, POST, PUT, DELETE, OPTIONS)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));

        // ALLOW ALL HEADERS (Authorization, Content-Type, etc)
        config.setAllowedHeaders(List.of("*"));

        // ALLOW CREDENTIALS (Cookies/Auth Tokens)
        config.setAllowCredentials(true);

        // Expose headers if needed
        config.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // ----------------------------
    // Spring Security Chain
    // ----------------------------
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/api/auth/**").permitAll()
            	    .requestMatchers("/api/session/**").permitAll()
            	    .requestMatchers("/api/public/**").permitAll()
            	    .requestMatchers("/api/public-orders/**").permitAll()
            	    .requestMatchers("/v3/api-docs/**").permitAll()
            	    .requestMatchers("/h2-console/**").permitAll()
            	    .requestMatchers("/payments/**").permitAll()
            	    .requestMatchers("/api/test/**").permitAll()
            	    .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()

            	    .requestMatchers("/api/admin/**").hasRole("ADMIN")
            	    .requestMatchers("/api/staff/**").hasAnyRole("ADMIN", "STAFF")

            	    .anyRequest().authenticated()
            	)

;

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Fix H2 console
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    // ----------------------------
    // Swagger, static resources ignore
    // ----------------------------
    @Bean
    public org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }

    // ----------------------------
    // Data Seeder
    // ----------------------------
    @Bean
    @Transactional
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = ensureRoleExists(roleRepository, AppRole.ROLE_USER);
            Role staffRole = ensureRoleExists(roleRepository, AppRole.ROLE_STAFF);
            Role adminRole = ensureRoleExists(roleRepository, AppRole.ROLE_ADMIN);

            createOrUpdateUser("user1", "user1@example.com", "password1",
                    "User", "One", "1234567890",
                    List.of(userRole), userRepository, passwordEncoder);

            createOrUpdateUser("staff1", "staff1@example.com", "password2",
                    "Staff", "One", "1234567890",
                    List.of(staffRole), userRepository, passwordEncoder);

            createOrUpdateUser("admin", "admin@example.com", "adminPass",
                    "Admin", "User", "1234567890",
                    List.of(adminRole, staffRole, userRole), userRepository, passwordEncoder);
        };
    }

    @Transactional
    protected Role ensureRoleExists(RoleRepository roleRepository, AppRole roleName) {
        return roleRepository.findByRoleName(roleName)
            .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    @Transactional
    protected void createOrUpdateUser(String username, String email, String password,
                                      String firstName, String lastName, String mobileNumber,
                                      List<Role> roles, UserRepository userRepository, PasswordEncoder passwordEncoder) {

        userRepository.findByUserName(username).ifPresentOrElse(
            user -> {
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(password));
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setMobileNumber(mobileNumber);
                user.getRoles().clear();
                roles.forEach(user::addRole);
                userRepository.save(user);
            },
            () -> {
                User newUser = new User(username, email, passwordEncoder.encode(password),
                        firstName, lastName, mobileNumber);
                roles.forEach(newUser::addRole);
                userRepository.save(newUser);
            }
        );
    }

    // ----------------------------
    // Init Cookies
    // ----------------------------
    @Bean
    public CommandLineRunner initCookies(CookieRepository cookieRepository) {
        return args -> {
            if (cookieRepository.count() == 0) {

                Cookie c1 = new Cookie();
                c1.setName("Classic Chocolate Chip");
                c1.setType("Chocolate");
                c1.setColor("Brown");
                c1.setMessage("Fresh Baked");
                c1.setIcing("None");
                c1.setDescription("Classic chocolate chip cookies with melty chocolate chips.");
                c1.setBasePrice(2.99);
                c1.setDiscount(0.0);
                c1.setAvailable(true);
                c1.setImageUrl("https://example.com/choco.jpg");
                c1.setIngredients(new HashSet<>(Arrays.asList("flour","sugar","butter","eggs","chocolate")));
                c1.setCustomizable(true);

                Cookie c2 = new Cookie();
                c2.setName("Vanilla Sugar Cookie");
                c2.setType("Sugar");
                c2.setColor("White");
                c2.setMessage("Sweet Treats");
                c2.setIcing("Vanilla");
                c2.setDescription("Soft and chewy sugar cookies.");
                c2.setBasePrice(2.49);
                c2.setDiscount(0.0);
                c2.setAvailable(true);
                c2.setImageUrl("https://example.com/sugar.jpg");
                c2.setIngredients(new HashSet<>(Arrays.asList("flour","sugar","butter","eggs")));
                c2.setCustomizable(true);

                cookieRepository.saveAll(List.of(c1, c2));
            }
        };
        
    }
}
