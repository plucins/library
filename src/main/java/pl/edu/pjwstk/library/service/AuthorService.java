package pl.edu.pjwstk.library.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.edu.pjwstk.library.model.Author;
import pl.edu.pjwstk.library.repository.AuthorRepository;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author createAuthor(Author author) {
        if (author.getFirstName() == null || author.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Imię autora nie może być puste");
        }
        
        if (author.getLastName() == null || author.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwisko autora nie może być puste");
        }
        
        return authorRepository.save(author);
    }

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono autora o ID: " + id));
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author findByFirstNameAndLastName(String firstName, String lastName) {
        return authorRepository.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono autora: " + firstName + " " + lastName));
    }

    public List<Author> findByBirthYearBetween(Integer startYear, Integer endYear) {
        return authorRepository.findByBirthYearBetween(startYear, endYear);
    }

    // READ - Find authors without books
    public List<Author> findAuthorsWithoutBooks() {
        return authorRepository.findAuthorsWithoutBooks();
    }

    // UPDATE - Update author
    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = findById(id);
        
        if (authorDetails.getFirstName() != null && !authorDetails.getFirstName().trim().isEmpty()) {
            author.setFirstName(authorDetails.getFirstName());
        }
        
        if (authorDetails.getLastName() != null && !authorDetails.getLastName().trim().isEmpty()) {
            author.setLastName(authorDetails.getLastName());
        }
        
        if (authorDetails.getBirthYear() != null) {
            author.setBirthYear(authorDetails.getBirthYear());
        }
        
        return authorRepository.save(author);
    }

    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new NoSuchElementException("Nie znaleziono autora o ID: " + id);
        }
        authorRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return authorRepository.existsById(id);
    }
}
