package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.persistence.Lob;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.sql.Date;

public class RegisterUser
{
    @Getter
    @Setter
    public int registerUser_id;

    @Getter
    @Setter
    @NotEmpty(message = "firstname cannot be empty")
    @Size(min=2, max=100, message = "Length of your firstname must be between 2 and 100 characters.")
    public String firstname;

    @Getter
    @Setter
    @NotEmpty(message = "lastname cannot be empty")
    @Size(min=2, max=100, message = "Length of your lastname must be between 2 and 100 characters.")
    public String lastname;

    @Getter
    @Setter
    @NotEmpty(message = "cognizant ID cannot be empty")
    @Size(min=2, max=6, message = "Length of your cognizant id cannot be longer than 6 digits")
    public int congizantid;

    @Getter
    @Setter
    @NotEmpty
    @Email(message = "your email does not match with the email format (must contain '@')")
    @Pattern(message = "it must be a cognizant email"
            , regexp = "^[A-Za-z0-9._%+-]+@cognizant.com$")
    public String email;

    @Getter
    @Setter
    @Valid
    @Pattern(message = "Password must be minimum eight characters, one uppercase, one lowercase letter, one number and one special character (e.g *,!,%,$...)"
            , regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%.*?&])[A-Za-z\\d@$!%*.?&]{8,}$", flags = {Pattern.Flag.CASE_INSENSITIVE})
    public String password;

    @Getter
    @Setter
    public int verification_code;

    @Getter
    @Setter
    public String confirmpassword;


    public RegisterUser() {

    }

    public RegisterUser(int registerUser_id, String firstname, String lastname, String email, String password, String confirmpassword, int verification_code) {
        this.registerUser_id = registerUser_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.confirmpassword = confirmpassword;
        this.verification_code = verification_code;
    }
}
