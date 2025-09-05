package hexlet.code.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import hexlet.code.model.Label;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

	boolean existsByLabelsContains(Label label);
}
