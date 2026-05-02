package com.velas.candil.entities.user;

import com.velas.candil.models.user.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/**
 * The Role entity implements GrantedAuthority to integrate with Spring Security.
 * By implementing GrantedAuthority, each Role represents an authority granted
 * to a user, such as a permission or role name
 * Spring Security uses this authority information during authorization to
 * determine whether an authenticated user has access to specific resources.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)@AllArgsConstructor
@Table(name = "roles")
@Builder
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @Override
    public @Nullable String getAuthority() {
        return "ROLE_"+role.name();
    }
}
