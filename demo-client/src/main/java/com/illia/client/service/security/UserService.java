package com.illia.client.service.security;

import com.illia.client.persistence.security.entity.User;
import com.illia.client.persistence.security.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;

  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

}
