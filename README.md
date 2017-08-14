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
@Entry(objectClasses = {"person", "top"})
public class Person {

    @Id
    private Name dn;

    @Attribute(name = "cn")
    private String cn;

    @Attribute(name = "mail")
    private String email;

    @Attribute(name = "sAMAccountName")
    private String sAMAccountName;

    @Attribute(name = "displayName")
    private String displayName;
}

```

PersonRepository
```
@Repository
public interface PersonRepository extends LdapRepository<Person> {
    Optional<Person> findByEmail(String email);

    Optional<Person> findByCn(String cn);
}

```

配置项

```
spring.ldap.embedded.base-dn=dc=wilmar,dc=cn
```

## 数据：文件 schema.ldif

CLR

```

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

```
spring.ldap.urls=ldap://10.114.0.8:3268
spring.ldap.base=OU=Users,OU=SHH-IT,OU=YiHaiKerryGroup,DC=wilmar,DC=cn
spring.ldap.username=xxx
spring.ldap.password=xxx
```

## 开启登入

```    
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().anyRequest().authenticated()
            .and().formLogin()
            .and().csrf().disable();
}

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider()).userDetailsService(userDetailsService());
}

@Bean
public AuthenticationManager authenticationManager() {
    return new ProviderManager(Arrays.asList(activeDirectoryLdapAuthenticationProvider()));
}

@Value("${ldap.domain}")
private String domain;
@Value("${spring.ldap.urls}")
private String url;
@Value("${spring.ldap.base}")
private String rootDn;

@Bean
public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
    ActiveDirectoryLdapAuthenticationProvider provider
            = new ActiveDirectoryLdapAuthenticationProvider(domain, url, rootDn);
    provider.setConvertSubErrorCodesToExceptions(true);
    return provider;
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