package pl.edu.pjwstk.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.edu.pjwstk.library.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
