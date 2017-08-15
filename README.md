# Spring LDAP Demo

讲解 Spring Security 如何和 LDAP（AD）集成

## 增加依赖

```xml
<thymeleaf.version>3.0.7.RELEASE</thymeleaf.version>
<thymeleaf-layout-dialect.version>2.2.2</thymeleaf-layout-dialect.version>

<dependency>
	<groupId>com.unboundid</groupId>
	<artifactId>unboundid-ldapsdk</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-ldap</artifactId>
</dependency>

```

## 创建 Person 对象

```java
@ToString
// 注意这个是 Entry， 不是 Entity
@Entry(objectClasses = {"person", "top"})
public class Person {

    // BaseDN，类型是 Name
    @Id
    private Name dn;

    @Attribute(name = "cn")
    private String cn;

    @Attribute(name = "mail")
    private String email;

    // 这个是我们的登录名
    @Attribute(name = "sAMAccountName")
    private String sAMAccountName;

    @Attribute(name = "displayName")
    private String displayName;
}
```

PersonRepository
```java
@Repository
public interface PersonRepository extends LdapRepository<Person> {
    Optional<Person> findByEmail(String email);

    Optional<Person> findByCn(String cn);
}

```

配置项

```properties
spring.ldap.embedded.base-dn=dc=wilmar,dc=cn
```

## 数据：文件 schema.ldif

CLR

```java
final AtomicInteger count = new AtomicInteger();
repository.findAll().forEach(person -> System.out.println(count.incrementAndGet() + ": " + person));
System.out.println("一共：" + count.get());

System.out.println("查找 Email：yinguowei@cn.wilmar-intl.com");
repository.findByEmail("yinguowei@cn.wilmar-intl.com").ifPresent(System.out::println);

System.out.println("查找 CN：YinGuoWei");
repository.findByCn("yinguowei").ifPresent(System.out::println);
```

## 改从服务器上查询

配置

```properties
# 请在这里输入 ldap 地址和账号，urls 格式：ldap://10.114.0.8:3268
spring.ldap.urls=ldap://10.114.0.8:3268
spring.ldap.base=OU=Users,OU=SHH-IT,OU=YiHaiKerryGroup,DC=wilmar,DC=cn
spring.ldap.username=xxx
spring.ldap.password=xxx

# 自动拆分成dc=
ldap.domain=wilmar.cn
```

## 开启登入

```java
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
        return provider;
    }
}

```
好了，就这么简单

## 项目地址

http://git.wilmartest.cn/yinguowei/spring-ldap-demo-v2.git

## 参考

主要参考这篇

[Setup Spring Security with Active Directory LDAP in Spring Boot Web Application](https://raymondhlee.wordpress.com/2014/09/20/setup-spring-security-with-active-directory-ldap-in-spring-boot-web-application/)

[LDAP Authentication](https://docs.spring.io/spring-security/site/docs/3.1.x/reference/ldap.html)

[利用Spring进行LDAP验证登录遇到的问题及其解决方式](http://blog.csdn.net/t894690230/article/details/52928369)

[Spring Security LDAP test](https://github.com/spring-projects/spring-security/blob/master/ldap/src/test/java/org/springframework/security/ldap/authentication/ad/ActiveDirectoryLdapAuthenticationProviderTests.java)

[pac4j/spring-security-pac4j](https://github.com/pac4j/spring-security-pac4j)

[pac4j LDAP doc](http://www.pac4j.org/docs/authenticators/ldap.html)

[Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)

[cas/support/cas-server-support-ldap-core](https://github.com/apereo/cas/blob/master/support/cas-server-support-ldap-core/src/main/java/org/apereo/cas/authorization/LdapUserGroupsToRolesAuthorizationGenerator.java)