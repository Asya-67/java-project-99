package hexlet.code.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "labels")
@EntityListeners(AuditingEntityListener.class)
public class Label implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    @CreatedDate
    private LocalDate createdAt;

    @ManyToMany(mappedBy = "labels")
    private Set<Task> tasks = new HashSet<>();
}
