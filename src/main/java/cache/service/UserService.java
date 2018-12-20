package cache.service;

import cache.entities.UserEntity;
import cache.entities.UserRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "cacheSample")
public class UserService {

  private UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public UserEntity insertUser(UserEntity userEntity) {
    return userRepository.save(userEntity);
  }

  /**
   * Find a user by user id, and cache this user by the key like this: "spring-cache-sample:user:1"
   *
   * @param id user id
   * @return user
   */
  @Cacheable(key = "'user:'+ #id")
  public Optional<UserEntity> findById(long id) {
    return userRepository.findById(id);
  }

  /**
   * Update a user by user id and the result will be added or updated in cache corresponding to the
   * key for given cache name.
   *
   * @param userEntity user
   * @return the updated user
   */
  @CachePut(key = "'user:'+ #userEntity.id")
  public UserEntity updateUser(UserEntity userEntity) {
    return userRepository.save(userEntity);
  }

  @CacheEvict(key = "'user:' + #id")
  public void deleteById(long id) {
    userRepository.deleteById(id);
  }


}
