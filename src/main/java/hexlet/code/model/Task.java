package hexlet.code.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Setter
@Getter
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    private Integer index;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    private User assignee;

    @JoinColumn(nullable = false)
    @ManyToOne
    private TaskStatus taskStatus;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Label> labels = new HashSet<>();

    @CreatedDate
    private LocalDate createdAt;
}
