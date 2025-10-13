package pl.edu.pjwstk.library.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.edu.pjwstk.library.model.Author;
import pl.edu.pjwstk.library.model.Book;
import pl.edu.pjwstk.library.repository.BookRepository;

@Service
public class BookService {

    public BookRepository bookRepository;
    public AuthorService authorService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public Book updateBook(Long bookIdToUpdate, Book book) {
        Book bookToUpdate = findBookById(bookIdToUpdate);

        if (book.getTitle() != null) {
            bookToUpdate.setTitle(book.getTitle());
        }

        if (book.getIsbn() != null) {
            bookToUpdate.setIsbn(book.getIsbn());
        }

        if (book.getAuthors() != null) {
            for (Author author : book.getAuthors()) {
                if (authorService.findById(author.getId()).isEmpty()) {
                    authorService.create(author);
                }
            }

            bookToUpdate.setAuthors(book.getAuthors());
        }

        return bookRepository.save(bookToUpdate);
    }

    public Book findBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return book.get();
        }
        throw new NoSuchElementException();
    }

    public Book createBook(Book book) {
        if (findByIsbn(book.getIsbn()).getId() == null) {
            bookRepository.save(book);
        }
        throw new IllegalArgumentException("Książka jest już utworzona");
    }

    public Book findByIsbn(String isbn) {
        Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);
        Book book;

        if (optionalBook.isPresent()) {
            book = optionalBook.get();
        }

        throw new NoSuchElementException("Nie znalazlem ksiazki o ISBN " + isbn);
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }
}
