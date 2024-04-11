package io.github.aieducationbackend.mapper;

import io.github.aieducationbackend.dto.UserDto;
import io.github.aieducationbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    List<UserDto> usersToUsersDto(List<User> users);

    @Mapping(target = "password", source = "password", ignore = true)
    User userDtoToUser(UserDto userDto);
}
