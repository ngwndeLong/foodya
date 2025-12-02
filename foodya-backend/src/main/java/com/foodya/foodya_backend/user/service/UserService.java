package com.foodya.foodya_backend.user.service;

import org.springframework.stereotype.Service;

import com.foodya.foodya_backend.user.repository.UserRepository;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
}
