package io.github.aieducationbackend.service;

import io.github.aieducationbackend.dto.UserDto;
import io.github.aieducationbackend.entity.User;
import io.github.aieducationbackend.mapper.UserMapper;
import io.github.aieducationbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public void saveUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userMapper.usersToUsersDto(userRepository.findAll());
    }
}
