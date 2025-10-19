package pl.edu.pjwstk.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.edu.pjwstk.library.model.Library;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

    Optional<Library> findByName(String name);

    boolean existsByName(String name);
}
