package pl.edu.pjwstk.library.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.edu.pjwstk.library.model.Borrow;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    List<Borrow> findByUserId(Long userId);

    List<Borrow> findByBookId(Long bookId);

    List<Borrow> findByReturnDateIsNull();

    @Query("SELECT b FROM Borrow b WHERE b.returnDate IS NULL AND b.dueDate < :currentDate")
    List<Borrow> findOverdueBorrows(@Param("currentDate") LocalDateTime currentDate);

    List<Borrow> findByBorrowDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    Optional<Borrow> findByUserIdAndBookIdAndReturnDateIsNull(Long userId, Long bookId);

    long countByUserIdAndReturnDateIsNull(Long userId);
}
