package pl.edu.pjwstk.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import pl.edu.pjwstk.library.dto.BookFilterDto;
import pl.edu.pjwstk.library.model.Author;
import pl.edu.pjwstk.library.model.Book;
import pl.edu.pjwstk.library.repository.BookRepository;

@Service
public class BookSpecificationService {

    private final BookRepository bookRepository;

    @Autowired
    public BookSpecificationService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> searchForBooks(BookFilterDto dto) {

        if (dto == null) {
            return bookRepository.findAll();
        }
        Specification<Book> spec = createSpecification(dto);
        return bookRepository.findAll(spec);
    }

    public Specification<Book> createSpecification(BookFilterDto dto) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
                predicate = cb.and(cb.like(cb.lower(root.get("title")),
                        "%" + dto.getTitle().toLowerCase() + "%"));
            }

            if (dto.getIsbn() != null && !dto.getIsbn().trim().isEmpty()) {
                predicate = cb.and(cb.equal(root.get("isbn"), dto.getIsbn()));
            }

            if (dto.getAuthorFirstName() != null || dto.getAuthorLastName() != null) {
                Join<Book, Author> authorJoin = root.join("authors", JoinType.INNER);



                if (dto.getAuthorFirstName() != null && !dto.getAuthorFirstName().trim().isEmpty()) {
                    predicate = cb.and(predicate,
                            cb.like(cb.lower(authorJoin.get("firstName")),
                                    "%" + dto.getAuthorFirstName().toLowerCase() + "%"));
                }

                if (dto.getAuthorLastName() != null && !dto.getAuthorLastName().trim().isEmpty()) {
                    predicate = cb.and(predicate,
                            cb.equal(cb.lower(authorJoin.get("lastName")),
                                    dto.getAuthorLastName().toLowerCase()));
                }
            }

            if(dto.getLibraryName() != null && !dto.getLibraryName().trim().isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("library").get("name"), dto.getLibraryName()));
            }

            return predicate;
        };
    }
}
