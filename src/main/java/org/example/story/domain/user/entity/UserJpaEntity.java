package org.example.story.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "user")
public class UserJpaEntity {
    @Id
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

    @ColumnDefault("'VERIFIED'")
    @Column(name = "role", nullable = false, length = 128)
    private String role;

}