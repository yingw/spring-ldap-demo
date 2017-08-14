package cn.wilmar.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 创建 by 殷国伟 于 2017/8/14.
 */
@Repository
public interface PersonRepository extends LdapRepository<Person> {
    Optional<Person> findByEmail(String email);

    Optional<Person> findByCn(String cn);
}
