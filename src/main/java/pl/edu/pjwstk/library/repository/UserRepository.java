package pl.edu.pjwstk.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.edu.pjwstk.library.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u JOIN u.borrows b WHERE b.returnDate IS NULL")
    List<User> findUsersWithActiveBorrows();

    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.user.id = :userId AND b.returnDate IS NULL")
    long countActiveBorrowsByUserId(@Param("userId") Long userId);
}
