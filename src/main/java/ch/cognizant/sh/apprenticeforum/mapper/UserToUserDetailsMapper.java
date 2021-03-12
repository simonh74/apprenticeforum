package ch.cognizant.sh.apprenticeforum.mapper;

import ch.cognizant.sh.apprenticeforum.model.Role;
import ch.cognizant.sh.apprenticeforum.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

public class UserToUserDetailsMapper
{
    static public UserDetails toUserDetails(User user) {
        org.springframework.security.core.userdetails.User new_user = null;

        if(user != null) {
            java.util.Collection<UserGrantedAuthority> authorities = new ArrayList<>();
            for(Role role : user.getUserroles()) {
                authorities.add(new UserGrantedAuthority(role.getDescription()));
            }

            new_user = new org.springframework.security.core.userdetails.User(user.getEmail()
                    , user.getPassword()
                    ,true
                    , true
                    , true
                    , true
                    , authorities
            );
        }
        return new_user;
    }
}
