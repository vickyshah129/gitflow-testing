package com.opsalert.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@ComponentScan
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*http
		.authorizeRequests()
		.antMatchers("/managers")
		.hasRole("MANAGERS")
		.antMatchers("/employees")
		.hasRole("EMPLOYEES")
		.anyRequest()
		.fullyAuthenticated()
		.and()
		.formLogin();*/
		
		http.authorizeRequests()
        .antMatchers("/managers").hasAnyRole("MANAGERS")
        .antMatchers("/Users").hasRole("Users")
        .and()
        .formLogin().loginPage("/login").failureUrl("/error")
        .usernameParameter("username").passwordParameter("password")
        .and()
        .csrf().disable();
		
		
		/*http.authorizeRequests()
        .anyRequest()//allow all urls
        .authenticated()//all URLs are allowed by any authenticated user, no role restrictions.
        .and()
        .formLogin()//enable form based authentication
        .loginPage("/login")//use a custom login URI
        .permitAll(true)//login URI can be accessed by anyone
        .and()
        .logout()//default logout handling
        .logoutSuccessUrl("/login?logout")//our new logout success url, we are not replacing other defaults.
        .permitAll();//allow all as it will be accessed when user is not logged in anymore
*/	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
		.ldapAuthentication()
		.userSearchBase("ou=Users")
		.userSearchFilter("(uid={0})")
		.groupSearchBase("ou=Users")
		.groupSearchFilter("member={0}")
		.contextSource()
		.url("ldap://34.242.69.69:389/dc=opsalerts,dc=com")
		.root("dc=opsalerts,dc=com");
	}
	
	@Bean
	WebMvcConfigurer myWebMvcConfigurer() {
		return new WebMvcConfigurerAdapter() {

			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				ViewControllerRegistration r = registry.addViewController("/login");
				r.setViewName("login");
			}
		};
	}
	
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}
	
}