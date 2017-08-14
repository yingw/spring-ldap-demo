package cn.wilmar.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class SpringLdapDemoApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(SpringLdapDemoApplication.class, args);
	}

	@Autowired
	PersonRepository repository;

	@Override
	public void run(String... strings) throws Exception {
		// 循环查找，注意AtomicIntegery 用于在 Lambda 内增加计数器功能
		final AtomicInteger count = new AtomicInteger();
		repository.findAll().forEach(person -> System.out.println(count.incrementAndGet() + ": " + person));
		System.out.println("一共：" + count.get());

		System.out.println("查找 Email：yinguowei@cn.wilmar-intl.com");
		repository.findByEmail("yinguowei@cn.wilmar-intl.com").ifPresent(System.out::println);

		System.out.println("查找 CN：YinGuoWei");
		repository.findByCn("yinguowei").ifPresent(System.out::println);
	}

	@Controller
	class IndexController {
		@GetMapping("/")
		public String index() {
			return "index";
		}
	}
}
