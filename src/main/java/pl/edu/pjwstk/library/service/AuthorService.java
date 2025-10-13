package pl.edu.pjwstk.library.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import pl.edu.pjwstk.library.model.Author;
import pl.edu.pjwstk.library.repository.AuthorRepository;

@Service
public class AuthorService {

    public AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    public Author create(Author author) {
        return authorRepository.save(author);
    }
}
