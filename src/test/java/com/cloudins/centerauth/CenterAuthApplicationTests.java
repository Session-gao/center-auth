package com.cloudins.centerauth;

import com.cloudins.centerauth.entity.User;
import com.cloudins.centerauth.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CenterAuthApplicationTests {


	@Autowired
	private UserRepository applicationUserRepository;
	//	@Autowired
//	 private RedisTemplate redisTemplate;
	@Test
	public void contextLoads() {
//		User user1 = applicationUserRepository.findByUsername(user.getUsername());
		User user = new User();
		user.setName("tom");
		user.setPassword("tom");
		user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword()).getBytes()));
		applicationUserRepository.save(user);
	}
}
