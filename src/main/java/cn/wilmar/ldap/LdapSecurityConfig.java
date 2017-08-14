package cn.wilmar.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 创建 by 殷国伟 于 2017/8/14.
 */
@EnableWebSecurity
@Component
public class LdapSecurityConfig extends WebSecurityConfigurerAdapter {
    // 和 Security 配置类似，formLogin 支持，禁用跨站验证
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and().formLogin()
                .and().csrf().disable();
    }

    // config auth，设置 AD 的 AuthenticationProvider
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider()).userDetailsService(userDetailsService());
    }

    // 定义 AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.asList(activeDirectoryLdapAuthenticationProvider()));
    }

    // 从配置文件读取的配置
    @Value("${ldap.domain}")
    private String domain;
    @Value("${spring.ldap.urls}")
    private String url;
    @Value("${spring.ldap.base}")
    private String rootDn;

    // 定义 AuthenticationProvider
    @Bean
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        // 注意这里要用 ActiveDirectoryLdapAuthenticationProvider，专为 AD 服务
        ActiveDirectoryLdapAuthenticationProvider provider
                = new ActiveDirectoryLdapAuthenticationProvider(domain, url, rootDn);
        provider.setConvertSubErrorCodesToExceptions(true);
//        provider.setUseAuthenticationRequestCredentials(true);
        return provider;
    }
}
