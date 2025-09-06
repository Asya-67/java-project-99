package hexlet.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements BaseEntity, UserDetails {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    @Email
    @ToString.Include
    private String email;

    @Column(nullable = false)
    @ToString.Exclude
    private String passwordDigest;

    public void setPassword(String rawPassword) {
        this.passwordDigest = ENCODER.encode(rawPassword);
    }

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "assignee")
    @ToString.Exclude
    private Set<Task> tasks = new HashSet<>();

    @Override
    public String getPassword() {
        return passwordDigest;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
