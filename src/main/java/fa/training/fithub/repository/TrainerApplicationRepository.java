package fa.training.fithub.repository;

import fa.training.fithub.entity.TrainerApplication;
import fa.training.fithub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerApplicationRepository extends JpaRepository<TrainerApplication, Long> {

    Optional<TrainerApplication> findByUser(User user);
}
