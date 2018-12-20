package cache;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import cache.entities.UserEntity;
import cache.entities.UserRepository;
import cache.service.UserService;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Test
  public void testQueryCache() {
    UserEntity userEntity = constructUser("user1");

    //1. insert a user
    userEntity = userService.insertUser(userEntity);

    //mock the userRepository to return the userEntity with corresponding user id
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));

    //2. find this user from db
    Optional<UserEntity> returnedUserOptional = userService.findById(userEntity.getId());

    //verify the user can be retrieved in db
    assertTrue("The user cannot be found in db.", returnedUserOptional.isPresent());

    //3. retrieve this user from cache
    verifyNoMoreInteractions(
        userRepository);//verify the user userRepository won't be invoked any more
    returnedUserOptional = userService.findById(userEntity.getId());
    assertTrue("The user should exist in cache.", returnedUserOptional.isPresent());

    //test again
    returnedUserOptional = userService.findById(userEntity.getId());
    assertTrue("The user should exist in cache.", returnedUserOptional.isPresent());
  }

  @Test
  public void testDelete() throws InterruptedException {
    UserEntity userEntity = constructUser("user2");

    //1. insert a user
    userEntity = userService.insertUser(userEntity);
    final long userId = userEntity.getId();

    //2. ensure the user exists in cache
    Optional<UserEntity> returnedUserOptional = userService.findById(userId);

    log.info(
        "-----> no sql executed for querying the user since the user can be retrieved from cach");

    //query cache for multiple times
    IntStream.range(0, 10).forEach(val -> {
      Optional<UserEntity> optionalUserEntity = userService.findById(userId);
      if (!optionalUserEntity.isPresent()) {
        throw new IllegalStateException("The user cannot be retrieved from cache");
      }
    });

    //3. delete the user entity and it wouldn't exist in cache now
    // spring data jpa will firstly find by id directly from db and delete it latter,
    // so you can see a additional select statement prints in console, don't worry, this is normal.
    userService.deleteById(userId);

    //wait for 5 seconds
    TimeUnit.SECONDS.sleep(5);

    log.info(
        "-----> begin to query db directly since the user is deleted and doesn't exist in cache");
    //4. query the user entity from db again
    returnedUserOptional = userService.findById(userEntity.getId());

    //query cache for multiple times
    final long userId2 = userEntity.getId();
    IntStream.range(0, 3).forEach(val -> {
      Optional<UserEntity> optionalUserEntity = userService.findById(userId2);
      if (!optionalUserEntity.isPresent()) {
        log.info("-----> Cannot find the user in db");
      }
    });

  }

  private static Random random = new Random();

  private UserEntity constructUser(String name) {
    UserEntity userEntity = new UserEntity();
    userEntity.setName(name);
    userEntity.setAge(random.nextInt(100));
    userEntity.setDesc("a user:" + name);
    return userEntity;
  }

}
