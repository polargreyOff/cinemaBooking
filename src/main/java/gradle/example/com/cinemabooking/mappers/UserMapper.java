package gradle.example.com.cinemabooking.mappers;

import gradle.example.com.cinemabooking.generated.model.UserResponse;
import gradle.example.com.cinemabooking.models.UserData;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserData toUserData(UserResponse userResponse) {
        UserData user = new UserData();
        user.setId(userResponse.getId());
        user.setName(userResponse.getName());
        user.setLogin(userResponse.getLogin());
        user.setPhone(userResponse.getPhone());
        return user;
    }

    public UserResponse toUserResponse(UserData userData) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userData.getId());
        userResponse.setName(userData.getName());
        userResponse.setLogin(userData.getLogin());
        userResponse.setPhone(userData.getPhone());
        return userResponse;
    }
}
