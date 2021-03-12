package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class VerificationHelper
{
    @Getter
    @Setter
    public int vh_user_id;

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
    public String new_password;

    @Getter
    @Setter
    public String confirm_new_password;

    @Getter
    @Setter
    public int verification_code;

    public VerificationHelper() {
        super();
    }

    public VerificationHelper(int vh_user_id) {
        this.vh_user_id = vh_user_id;
    }
}
