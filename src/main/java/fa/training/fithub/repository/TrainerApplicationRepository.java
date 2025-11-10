package fa.training.fithub.repository;
import java.util.Optional;

import fa.training.fithub.entity.TrainerApplication;
import fa.training.fithub.entity.User;
import fa.training.fithub.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainerApplicationRepository extends JpaRepository<TrainerApplication, Long> {

    Optional<TrainerApplication> findByUser(User user);

    @Query("SELECT ta FROM TrainerApplication ta WHERE ta.user.id = :userId")
    Optional<TrainerApplication> findByUserId(@Param("userId") Long userId);

    // Check user đã apply chưa
    boolean existsByUser(User user);

//     Lấy tất cả applications với filter theo status
    Page<TrainerApplication> findAllByStatus(ApplicationStatus status, Pageable pageable);

    // Lấy tất cả applications (không filter)
    Page<TrainerApplication> findAll(Pageable pageable);

    // Count applications by status
    long countByStatus(ApplicationStatus status);

}
