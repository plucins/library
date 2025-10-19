package pl.edu.pjwstk.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.edu.pjwstk.library.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByTitle(String title);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.firstName = :firstName AND a.lastName = :lastName")
    List<Book> findByAuthorName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    List<Book> findByLibraryId(Long libraryId);

    boolean existsByIsbn(String isbn);

    boolean existsByTitle(String title);
}
