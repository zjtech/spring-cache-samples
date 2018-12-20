package cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisServiceTest {

  @Autowired
  RedisTemplate redisTemplate;


  @Test
  public void testQueryCache() {
    RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
    RedisSerializer keySerializer = redisTemplate.getKeySerializer();

    assert valueSerializer != null;
  }

}
