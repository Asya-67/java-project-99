package hexlet.code.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "labels")
public class Label implements BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	@Size(min = 3, max = 1000)
	private String name;

	@CreatedDate
	private LocalDate createdAt;

	@ManyToMany(mappedBy = "labels")
	private Set<Task> tasks = new HashSet<>();

	public Label(String name) {
		this.name = name;
	}
}
