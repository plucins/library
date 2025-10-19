package pl.edu.pjwstk.library.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.edu.pjwstk.library.model.Author;
import pl.edu.pjwstk.library.model.Book;
import pl.edu.pjwstk.library.model.Library;
import pl.edu.pjwstk.library.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final LibraryService libraryService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService, LibraryService libraryService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.libraryService = libraryService;
    }

    // CREATE - Create new book
    public Book createBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tytuł książki nie może być pusty");
        }
        
        if (book.getIsbn() != null && bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Książka o ISBN '" + book.getIsbn() + "' już istnieje");
        }
        
        if (book.getTitle() != null && bookRepository.existsByTitle(book.getTitle())) {
            throw new IllegalArgumentException("Książka o tytule '" + book.getTitle() + "' już istnieje");
        }

        if (book.getAuthors() != null) {
            for (Author author : book.getAuthors()) {
                if (author.getId() == null) {
                    authorService.createAuthor(author);
                } else {
                    authorService.findById(author.getId());
                }
            }
        }

        if (book.getLibrary() != null && book.getLibrary().getId() != null) {
            libraryService.findById(book.getLibrary().getId());
        }
        
        return bookRepository.save(book);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono książki o ID: " + id));
    }

    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono książki o ISBN: " + isbn));
    }

    public Book findByTitle(String title) {
        return bookRepository.findByTitle(title)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono książki o tytule: " + title));
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> findByAuthorId(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    public List<Book> findByAuthorName(String firstName, String lastName) {
        return bookRepository.findByAuthorName(firstName, lastName);
    }

    public List<Book> findByLibraryId(Long libraryId) {
        return bookRepository.findByLibraryId(libraryId);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = findById(id);

        if (bookDetails.getTitle() != null && !bookDetails.getTitle().trim().isEmpty()) {
            if (!book.getTitle().equals(bookDetails.getTitle()) &&
                bookRepository.existsByTitle(bookDetails.getTitle())) {
                throw new IllegalArgumentException("Książka o tytule '" + bookDetails.getTitle() + "' już istnieje");
            }
            book.setTitle(bookDetails.getTitle());
        }

        if (bookDetails.getIsbn() != null && !bookDetails.getIsbn().trim().isEmpty()) {
            if (!book.getIsbn().equals(bookDetails.getIsbn()) && 
                bookRepository.existsByIsbn(bookDetails.getIsbn())) {
                throw new IllegalArgumentException("Książka o ISBN '" + bookDetails.getIsbn() + "' już istnieje");
            }
            book.setIsbn(bookDetails.getIsbn());
        }

        if (bookDetails.getAuthors() != null) {
            for (Author author : bookDetails.getAuthors()) {
                if (author.getId() == null) {
                    authorService.createAuthor(author);
                } else {
                    authorService.findById(author.getId());
                }
            }
            book.setAuthors(bookDetails.getAuthors());
        }

        if (bookDetails.getLibrary() != null) {
            if (bookDetails.getLibrary().getId() != null) {
                Library library = libraryService.findById(bookDetails.getLibrary().getId());
                book.setLibrary(library);
            } else {
                book.setLibrary(null);
            }
        }

        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new NoSuchElementException("Nie znaleziono książki o ID: " + id);
        }
        bookRepository.deleteById(id);
    }

    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    public boolean existsByTitle(String title) {
        return bookRepository.existsByTitle(title);
    }

    public boolean existsById(Long id) {
        return bookRepository.existsById(id);
    }
}
