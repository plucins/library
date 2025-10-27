package pl.edu.pjwstk.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.edu.pjwstk.library.dto.BookFilterDto;
import pl.edu.pjwstk.library.model.Book;
import pl.edu.pjwstk.library.service.BookService;
import pl.edu.pjwstk.library.service.BookSpecificationService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final BookSpecificationService bookSpecificationService;

    @Autowired
    public BookController(BookService bookService, BookSpecificationService bookSpecificationService) {
        this.bookService = bookService;
        this.bookSpecificationService = bookSpecificationService;
    }

    // CREATE - Create new book
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        try {
            Book createdBook = bookService.createBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        try {
            Book book = bookService.findById(id);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        try {
            Book book = bookService.findByIsbn(isbn);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Book> getBookByTitle(@PathVariable String title) {
        try {
            Book book = bookService.findByTitle(title);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthorId(@PathVariable Long authorId) {
        List<Book> books = bookService.findByAuthorId(authorId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/author")
    public ResponseEntity<List<Book>> getBooksByAuthorName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        List<Book> books = bookService.findByAuthorName(firstName, lastName);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/library/{libraryId}")
    public ResponseEntity<List<Book>> getBooksByLibraryId(@PathVariable Long libraryId) {
        List<Book> books = bookService.findByLibraryId(libraryId);
        return ResponseEntity.ok(books);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        try {
            Book updatedBook = bookService.updateBook(id, bookDetails);
            return ResponseEntity.ok(updatedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<Book>> searchForBooks(@RequestBody BookFilterDto dto) {
        List<Book> books = bookSpecificationService.searchForBooks(dto);
        return ResponseEntity.ok(books);
    }

}
