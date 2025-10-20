package pl.edu.pjwstk.library.mapper;

import org.springframework.stereotype.Service;

import pl.edu.pjwstk.library.dto.UserDto;
import pl.edu.pjwstk.library.model.User;

@Service
public class UserMapper {

    public UserDto toUserDto(User user){
        return new UserDto(user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public User toUser(UserDto user){
        return new User(user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
