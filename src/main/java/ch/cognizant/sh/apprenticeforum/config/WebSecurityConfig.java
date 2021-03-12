package ch.cognizant.sh.apprenticeforum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    //authorities
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/register").permitAll()
                .antMatchers("/403.html").permitAll()
                .antMatchers("404.html").permitAll()
                .antMatchers("/verify").permitAll()
                .antMatchers("/resetpassword/**").permitAll()
                .antMatchers("/forum").authenticated()
                .antMatchers("/css/**", "/fragments/**", "/img/**", "/js/**").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login").usernameParameter("email").permitAll()
                .failureUrl("/login-error")
                .defaultSuccessUrl("/", true)
                .and().httpBasic()
                .and().logout().permitAll()
                .and().exceptionHandling().accessDeniedPage("/403.html");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        int strength = 10;
        //secure random generates a salt.
        return new BCryptPasswordEncoder(strength, new SecureRandom());
    }
}
