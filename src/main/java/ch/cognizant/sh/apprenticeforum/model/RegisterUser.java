package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;

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
    @NotNull(message = "cognizant ID cannot be empty")
    @Min(0)
    @Max(999999)
    public int cognizantid;

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

    @Getter
    @Setter
    public String oldpassword;

    public RegisterUser() {
        super();
    }

    public RegisterUser(int registerUser_id, String firstname, String lastname, int cognizantid,
                        String email, String password, int verification_code, String confirmpassword) {
        this.registerUser_id = registerUser_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.cognizantid = cognizantid;
        this.email = email;
        this.password = password;
        this.verification_code = verification_code;
        this.confirmpassword = confirmpassword;
    }
}
