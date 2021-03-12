package ch.cognizant.sh.apprenticeforum.mapper;

import org.springframework.security.core.GrantedAuthority;

public class UserGrantedAuthority implements GrantedAuthority {
    private static final  long serialVersionUID = 8835903531623403993L;
    private String authority;

    public UserGrantedAuthority(String authority) {
        super();
        this.authority = authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public String toString() {
        return "UserGrantedAuthority [authority= " + authority + "]";
    }
}
