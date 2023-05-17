package metaverse.backend.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import metaverse.backend.dto.AuthDto;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "test_user")
public class User {

    @Id
    @Column(name = "uid")
    private String uid;

    private String email; // Principal

    private String password; // Credential

    @Enumerated(EnumType.STRING)
    private Role role; // 사용자 권한

    // == 생성 메서드 == //
    public static User registerUser(AuthDto.SignupDto signupDto) {
        User user = new User();
        user.uid = UUID.randomUUID().toString();
        user.email = signupDto.getEmail();
        user.password = signupDto.getPassword();
        user.role = Role.USER;

        return user;
    }
}