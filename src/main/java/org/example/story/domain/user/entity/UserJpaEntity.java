package org.example.story.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@Table(name = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "hakburn", length = 4)
    private String hakburn;

    @Column(name = "profile_image", length = 512)
    private String profileImage;

    @ColumnDefault("'무전공'")
    @Column(name = "major")
    private String major;

    @Column(name = "introduce", length = 128)
    private String introduce;

    @Column(name = "role", length = 128)
    private String role;

    public void updateUserInformation(
            String nickname, String hakburn, String profileImage, String major, String introduce
    ) {
        this.nickname = nickname;
        this.hakburn = hakburn;
        this.profileImage = profileImage;
        this.major = major;
        this.introduce = introduce;
        this.role = "VERIFIED";
    }
}