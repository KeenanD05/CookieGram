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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
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
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Retrieve or create roles
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            Role staffRole = roleRepository.findByRoleName(AppRole.ROLE_STAFF)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.ROLE_STAFF);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> staffRoles = Set.of(staffRole);
            Set<Role> adminRoles = Set.of(userRole, staffRole, adminRole);


            // Create users if not already present
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"),"user1","user1","1234567890");
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("seller1")) {
                User staff1 = new User("staff1", "staff1@example.com", passwordEncoder.encode("password2"),"staff1","staff1","1234567890");
                userRepository.save(staff1);
            }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"),"admin","admin","1234567890");
                userRepository.save(admin);
            }

            // Update roles for existing users
            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByUserName("staff1").ifPresent(staff -> {
                staff.setRoles(staffRoles);
                userRepository.save(staff);
            });

            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });

            if (cookieRepository.count() == 0) {
                // Chocolate Chip Cookie
                Cookie chocolateChip = new Cookie();
                chocolateChip.setName("Classic Chocolate Chip");
                chocolateChip.setType("Chocolate");
                chocolateChip.setColor("Brown");
                chocolateChip.setMessage("Fresh Baked");
                chocolateChip.setIcing("None");
                chocolateChip.setDescription("Classic chocolate chip cookies with melty chocolate chips in every bite");
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
                sugarCookie.setName("Vanilla Sugar Cookie");
                sugarCookie.setType("Sugar");
                sugarCookie.setColor("White");
                sugarCookie.setMessage("Sweet Treats");
                sugarCookie.setIcing("Vanilla");
                sugarCookie.setDescription("Soft and chewy sugar cookies with vanilla icing");
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
                oatmealRaisin.setName("Oatmeal Raisin");
                oatmealRaisin.setType("Oatmeal");
                oatmealRaisin.setColor("Golden Brown");
                oatmealRaisin.setMessage("Homemade");
                oatmealRaisin.setIcing("None");
                oatmealRaisin.setDescription("Hearty oatmeal cookies packed with plump raisins and cinnamon");
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

