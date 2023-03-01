package com.backend.jwt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    //  @Column(nullable = false), @Column(length = 100)인 경우 SQL 쪽에서 예외처리를 뱉어낸다.
    //  @NotBlank , @Size 경우에는 SQL 문 전에 예외처리를 뱉어낸다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 2,max = 50)
    @Column(unique = true)
    private String username;
    @NotBlank
    private String password;
    private String email;

    @Enumerated(value = EnumType.STRING)
    private ERole eRole;

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;

    @CreationTimestamp
    private LocalDateTime createDate;


//    @PrePersist // DB 에 INSERT 되기 직전에 실행
//    public void createDate() {
//        this.createDate = LocalDateTime.now();
//    }
}
