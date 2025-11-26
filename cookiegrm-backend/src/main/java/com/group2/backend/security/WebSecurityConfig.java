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
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/session/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/payments/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/staff/**").hasAnyRole("ADMIN","STAFF")
                                .requestMatchers("/api/auth/me").authenticated()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }


    @Bean
    @Transactional
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // First ensure all roles exist and are managed
            Role userRole = ensureRoleExists(roleRepository, AppRole.ROLE_USER);
            Role staffRole = ensureRoleExists(roleRepository, AppRole.ROLE_STAFF);
            Role adminRole = ensureRoleExists(roleRepository, AppRole.ROLE_ADMIN);

            // Create users with their roles
            createOrUpdateUser("user1", "user1@example.com", "password1", "user1", "user1", "1234567890", 
                    List.of(userRole), userRepository, passwordEncoder);
                    
            createOrUpdateUser("staff1", "staff1@example.com", "password2", "staff1", "staff1", "1234567890", 
                    List.of(staffRole), userRepository, passwordEncoder);
                    
            createOrUpdateUser("admin", "admin@example.com", "adminPass", "admin", "admin", "1234567890", 
                    List.of(adminRole, staffRole, userRole), userRepository, passwordEncoder);
        };
    }
    
    @Transactional
    protected Role ensureRoleExists(RoleRepository roleRepository, AppRole roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    Role role = new Role(roleName);
                    return roleRepository.save(role);
                });
    }

    @Transactional
    protected void createOrUpdateUser(String username, String email, String password, String firstName, String lastName, 
                                   String mobileNumber, List<Role> roles, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        userRepository.findByUserName(username).ifPresentOrElse(
            user -> {
                // Update existing user
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(password));
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setMobileNumber(mobileNumber);
                
                // Clear existing roles and add new ones
                user.getRoles().clear();
                roles.forEach(user::addRole);
                
                userRepository.save(user);
            },
            () -> {
                // Create new user
                User newUser = new User(username, email, passwordEncoder.encode(password), firstName, lastName, mobileNumber);
                roles.forEach(newUser::addRole);
                userRepository.save(newUser);
            }
        );
    }

    @Bean
    public CommandLineRunner initCookies(CookieRepository cookieRepository) {
        return args -> {
            if (cookieRepository.count() == 0) {
                // Chocolate Chip Cookie
                Cookie chocolateChip = new Cookie();
                chocolateChip.setName("Promotion 1");
                chocolateChip.setType("Chocolate");
                chocolateChip.setColor("Brown");
                chocolateChip.setMessage("Fresh Baked");
                chocolateChip.setIcing("None");
                chocolateChip.setDescription("Coming Soon...");
                chocolateChip.setBasePrice(2.99);
                chocolateChip.setDiscount(0.0);
                chocolateChip.setAvailable(true);
                chocolateChip.setImageUrl("https://example.com/chocolate-chip.jpg");
                chocolateChip.setIngredients(new HashSet<>(Arrays.asList(
                        "flour", "brown sugar", "white sugar", "butter", "eggs", "vanilla extract", "baking soda", "salt", "chocolate chips"
                )));
                chocolateChip.setCustomizable(true);
                cookieRepository.save(chocolateChip);

                // Sugar Cookie
                Cookie sugarCookie = new Cookie();
                sugarCookie.setName("Promotion 2");
                sugarCookie.setType("Sugar");
                sugarCookie.setColor("White");
                sugarCookie.setMessage("Sweet Treats");
                sugarCookie.setIcing("Vanilla");
                sugarCookie.setDescription("Coming Soon...");
                sugarCookie.setBasePrice(2.49);
                sugarCookie.setDiscount(0.0);
                sugarCookie.setAvailable(true);
                sugarCookie.setImageUrl("https://example.com/sugar-cookie.jpg");
                sugarCookie.setIngredients(new HashSet<>(Arrays.asList(
                        "flour", "sugar", "butter", "eggs", "vanilla extract", "baking powder", "salt"
                )));
                sugarCookie.setCustomizable(true);
                cookieRepository.save(sugarCookie);

                // Oatmeal Raisin
                Cookie oatmealRaisin = new Cookie();
                oatmealRaisin.setName("Promotion 3");
                oatmealRaisin.setType("Oatmeal");
                oatmealRaisin.setColor("Golden Brown");
                oatmealRaisin.setMessage("Homemade");
                oatmealRaisin.setIcing("None");
                oatmealRaisin.setDescription("Coming Soon...");
                oatmealRaisin.setBasePrice(2.79);
                oatmealRaisin.setDiscount(0.0);
                oatmealRaisin.setAvailable(true);
                oatmealRaisin.setImageUrl("https://example.com/oatmeal-raisin.jpg");
                oatmealRaisin.setIngredients(new HashSet<>(Arrays.asList(
                        "oats", "flour", "brown sugar", "butter", "eggs", "cinnamon", "raisins", "baking soda", "vanilla extract"
                )));
                oatmealRaisin.setCustomizable(true);
                cookieRepository.save(oatmealRaisin);
            }
        };
        }
    }

