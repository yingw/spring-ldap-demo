package cn.wilmar.ldap;

import lombok.ToString;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

/**
 * 和 Ldap 的 person 对象映射
 * 创建 by 殷国伟 于 2017/8/14.
 */
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

