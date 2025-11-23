package pl.edu.pjwstk.library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.edu.pjwstk.library.exception.BookException;
import pl.edu.pjwstk.library.exception.BusinessException;
import pl.edu.pjwstk.library.model.Author;
import pl.edu.pjwstk.library.model.Book;
import pl.edu.pjwstk.library.model.Library;
import pl.edu.pjwstk.library.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Testy Jednostkowe")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() throws Exception {
        // Given
        Author testAuthor = new Author();
        setId(testAuthor, 1L);
        testAuthor.setFirstName("Jan");
        testAuthor.setLastName("Kowalski");

        Library testLibrary = new Library();
        setId(testLibrary, 1L);
        testLibrary.setName("Biblioteka Główna");

        testBook = new Book();
        setId(testBook, 1L);
        testBook.setTitle("Test Book");
        testBook.setIsbn("978-0-123456-78-9");
        testBook.setAuthors(List.of(testAuthor));
        testBook.setLibrary(testLibrary);
    }

    private void setId(Object obj, Long id) throws Exception {
        Field idField = obj.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(obj, id);
    }

    // ========== CREATE BOOK TESTS ==========

    @Test
    @DisplayName("createBook - powinien utworzyć książkę gdy wszystkie dane są poprawne")
    void createBook_ShouldCreateBook_WhenAllDataIsValid() throws BusinessException, BookException {
        // Given
        Book newBook = new Book();
        newBook.setTitle("Nowa Książka");
        newBook.setIsbn("978-0-987654-32-1");

        when(bookRepository.existsByIsbn(newBook.getIsbn())).thenReturn(false);
        when(bookRepository.existsByTitle(newBook.getTitle())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        // When
        Book result = bookService.createBook(newBook);

        // Then
        assertNotNull(result);
        assertEquals("Nowa Książka", result.getTitle());
        verify(bookRepository).existsByIsbn(newBook.getIsbn());
        verify(bookRepository).existsByTitle(newBook.getTitle());
        verify(bookRepository).save(newBook);
    }

    @Test
    @DisplayName("createBook - powinien rzucić BookException gdy tytuł jest pusty")
    void createBook_ShouldThrowBookException_WhenTitleIsEmpty() {
        // Given
        Book bookWithEmptyTitle = new Book();
        bookWithEmptyTitle.setTitle("   ");

        // When & Then
        BookException exception = assertThrows(BookException.class, 
            () -> bookService.createBook(bookWithEmptyTitle));
        
        assertEquals("Tytuł książki nie może być pusty", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("createBook - powinien rzucić BookException gdy tytuł jest null")
    void createBook_ShouldThrowBookException_WhenTitleIsNull() {
        // Given
        Book bookWithNullTitle = new Book();
        bookWithNullTitle.setTitle(null);

        // When & Then
        BookException exception = assertThrows(BookException.class, 
            () -> bookService.createBook(bookWithNullTitle));
        
        assertEquals("Tytuł książki nie może być pusty", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("createBook - powinien rzucić BookException gdy ISBN już istnieje")
    void createBook_ShouldThrowBookException_WhenIsbnAlreadyExists() {
        // Given
        Book bookWithExistingIsbn = new Book();
        bookWithExistingIsbn.setTitle("Nowa Książka");
        bookWithExistingIsbn.setIsbn("978-0-123456-78-9");

        when(bookRepository.existsByIsbn(bookWithExistingIsbn.getIsbn())).thenReturn(true);

        // When & Then
        BookException exception = assertThrows(BookException.class, 
            () -> bookService.createBook(bookWithExistingIsbn));
        
        assertEquals("Książka o ISBN '978-0-123456-78-9' już istnieje", exception.getMessage());
        verify(bookRepository).existsByIsbn(bookWithExistingIsbn.getIsbn());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("createBook - powinien rzucić IllegalArgumentException gdy tytuł już istnieje")
    void createBook_ShouldThrowIllegalArgumentException_WhenTitleAlreadyExists() {
        // Given
        Book bookWithExistingTitle = new Book();
        bookWithExistingTitle.setTitle("Istniejący Tytuł");
        bookWithExistingTitle.setIsbn("978-0-987654-32-1");

        when(bookRepository.existsByIsbn(bookWithExistingTitle.getIsbn())).thenReturn(false);
        when(bookRepository.existsByTitle(bookWithExistingTitle.getTitle())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.createBook(bookWithExistingTitle));
        
        assertEquals("Książka o tytule 'Istniejący Tytuł' już istnieje", exception.getMessage());
        verify(bookRepository).existsByTitle(bookWithExistingTitle.getTitle());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("createBook - powinien utworzyć autora gdy autor nie ma ID")
    void createBook_ShouldCreateAuthor_WhenAuthorHasNoId() throws BusinessException, BookException {
        // Given
        Author newAuthor = new Author();
        newAuthor.setFirstName("Anna");
        newAuthor.setLastName("Nowak");
        // newAuthor nie ma ID (null)

        Book bookWithNewAuthor = new Book();
        bookWithNewAuthor.setTitle("Książka z Nowym Autorem");
        bookWithNewAuthor.setAuthors(Arrays.asList(newAuthor));
        // książka nie ma ISBN, więc existsByIsbn nie będzie wywołane

        when(bookRepository.existsByTitle(bookWithNewAuthor.getTitle())).thenReturn(false);
        when(authorService.createAuthor(newAuthor)).thenReturn(newAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(bookWithNewAuthor);

        // When
        Book result = bookService.createBook(bookWithNewAuthor);

        // Then
        assertNotNull(result);
        verify(authorService).createAuthor(newAuthor);
        verify(bookRepository).save(bookWithNewAuthor);
    }

    @Test
    @DisplayName("createBook - powinien zweryfikować autora gdy autor ma ID")
    void createBook_ShouldVerifyAuthor_WhenAuthorHasId() throws BusinessException, BookException {
        // Given
        Author existingAuthor = new Author();
        try {
            setId(existingAuthor, 1L);
        } catch (Exception e) {
            fail("Nie udało się ustawić ID dla autora");
        }

        Book bookWithExistingAuthor = new Book();
        bookWithExistingAuthor.setTitle("Książka z Istniejącym Autorem");
        bookWithExistingAuthor.setAuthors(Arrays.asList(existingAuthor));
        // książka nie ma ISBN, więc existsByIsbn nie będzie wywołane

        when(bookRepository.existsByTitle(bookWithExistingAuthor.getTitle())).thenReturn(false);
        when(authorService.findById(1L)).thenReturn(existingAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(bookWithExistingAuthor);

        // When
        Book result = bookService.createBook(bookWithExistingAuthor);

        // Then
        assertNotNull(result);
        verify(authorService).findById(1L);
        verify(authorService, never()).createAuthor(any(Author.class));
        verify(bookRepository).save(bookWithExistingAuthor);
    }

    @Test
    @DisplayName("createBook - powinien zweryfikować bibliotekę gdy biblioteka ma ID")
    void createBook_ShouldVerifyLibrary_WhenLibraryHasId() throws BusinessException, BookException {
        // Given
        Library existingLibrary = new Library();
        try {
            setId(existingLibrary, 1L);
        } catch (Exception e) {
            fail("Nie udało się ustawić ID dla biblioteki");
        }

        Book bookWithLibrary = new Book();
        bookWithLibrary.setTitle("Książka z Biblioteką");
        bookWithLibrary.setLibrary(existingLibrary);
        // książka nie ma ISBN, więc existsByIsbn nie będzie wywołane

        when(bookRepository.existsByTitle(bookWithLibrary.getTitle())).thenReturn(false);
        when(libraryService.findById(1L)).thenReturn(existingLibrary);
        when(bookRepository.save(any(Book.class))).thenReturn(bookWithLibrary);

        // When
        Book result = bookService.createBook(bookWithLibrary);

        // Then
        assertNotNull(result);
        verify(libraryService).findById(1L);
        verify(bookRepository).save(bookWithLibrary);
    }

    // ========== FIND BY ID TESTS ==========

    @Test
    @DisplayName("findById - powinien zwrócić książkę gdy ID istnieje")
    void findById_ShouldReturnBook_WhenIdExists() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // When
        Book result = bookService.findById(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).findById(bookId);
    }

    @Test
    @DisplayName("findById - powinien rzucić NoSuchElementException gdy ID nie istnieje")
    void findById_ShouldThrowNoSuchElementException_WhenIdDoesNotExist() {
        // Given
        Long nonExistentId = 999L;
        when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
            () -> bookService.findById(nonExistentId));
        
        assertEquals("Nie znaleziono książki o ID: 999", exception.getMessage());
        verify(bookRepository).findById(nonExistentId);
    }

    // ========== FIND BY ISBN TESTS ==========

    @Test
    @DisplayName("findByIsbn - powinien zwrócić książkę gdy ISBN istnieje")
    void findByIsbn_ShouldReturnBook_WhenIsbnExists() {
        // Given
        String isbn = "978-0-123456-78-9";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(testBook));

        // When
        Book result = bookService.findByIsbn(isbn);

        // Then
        assertNotNull(result);
        assertEquals(isbn, result.getIsbn());
        verify(bookRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("findByIsbn - powinien rzucić NoSuchElementException gdy ISBN nie istnieje")
    void findByIsbn_ShouldThrowNoSuchElementException_WhenIsbnDoesNotExist() {
        // Given
        String nonExistentIsbn = "978-0-000000-00-0";
        when(bookRepository.findByIsbn(nonExistentIsbn)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
            () -> bookService.findByIsbn(nonExistentIsbn));
        
        assertEquals("Nie znaleziono książki o ISBN: 978-0-000000-00-0", exception.getMessage());
        verify(bookRepository).findByIsbn(nonExistentIsbn);
    }

    // ========== FIND BY TITLE TESTS ==========

    @Test
    @DisplayName("findByTitle - powinien zwrócić książkę gdy tytuł istnieje")
    void findByTitle_ShouldReturnBook_WhenTitleExists() {
        // Given
        String title = "Test Book";
        when(bookRepository.findByTitle(title)).thenReturn(Optional.of(testBook));

        // When
        Book result = bookService.findByTitle(title);

        // Then
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        verify(bookRepository).findByTitle(title);
    }

    @Test
    @DisplayName("findByTitle - powinien rzucić NoSuchElementException gdy tytuł nie istnieje")
    void findByTitle_ShouldThrowNoSuchElementException_WhenTitleDoesNotExist() {
        // Given
        String nonExistentTitle = "Nieistniejąca Książka";
        when(bookRepository.findByTitle(nonExistentTitle)).thenReturn(Optional.empty());

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
            () -> bookService.findByTitle(nonExistentTitle));
        
        assertEquals("Nie znaleziono książki o tytule: Nieistniejąca Książka", exception.getMessage());
        verify(bookRepository).findByTitle(nonExistentTitle);
    }

    // ========== FIND ALL TESTS ==========

    @Test
    @DisplayName("findAll - powinien zwrócić listę wszystkich książek")
    void findAll_ShouldReturnAllBooks() {
        // Given
        List<Book> books = Arrays.asList(testBook, new Book());
        when(bookRepository.findAll()).thenReturn(books);

        // When
        List<Book> result = bookService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("findAll - powinien zwrócić pustą listę gdy brak książek")
    void findAll_ShouldReturnEmptyList_WhenNoBooksExist() {
        // Given
        when(bookRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<Book> result = bookService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookRepository).findAll();
    }

    // ========== FIND BY AUTHOR ID TESTS ==========

    @Test
    @DisplayName("findByAuthorId - powinien zwrócić listę książek dla danego autora")
    void findByAuthorId_ShouldReturnBooks_WhenAuthorExists() {
        // Given
        Long authorId = 1L;
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByAuthorId(authorId)).thenReturn(books);

        // When
        List<Book> result = bookService.findByAuthorId(authorId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository).findByAuthorId(authorId);
    }

    // ========== FIND BY AUTHOR NAME TESTS ==========

    @Test
    @DisplayName("findByAuthorName - powinien zwrócić listę książek dla danego autora")
    void findByAuthorName_ShouldReturnBooks_WhenAuthorExists() {
        // Given
        String firstName = "Jan";
        String lastName = "Kowalski";
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByAuthorName(firstName, lastName)).thenReturn(books);

        // When
        List<Book> result = bookService.findByAuthorName(firstName, lastName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository).findByAuthorName(firstName, lastName);
    }

    // ========== FIND BY LIBRARY ID TESTS ==========

    @Test
    @DisplayName("findByLibraryId - powinien zwrócić listę książek dla danej biblioteki")
    void findByLibraryId_ShouldReturnBooks_WhenLibraryExists() {
        // Given
        Long libraryId = 1L;
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByLibraryId(libraryId)).thenReturn(books);

        // When
        List<Book> result = bookService.findByLibraryId(libraryId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository).findByLibraryId(libraryId);
    }

    // ========== UPDATE BOOK TESTS ==========

    @Test
    @DisplayName("updateBook - powinien zaktualizować książkę gdy wszystkie dane są poprawne")
    void updateBook_ShouldUpdateBook_WhenAllDataIsValid() throws BusinessException {
        // Given
        Long bookId = 1L;
        Book bookDetails = new Book();
        bookDetails.setTitle("Zaktualizowany Tytuł");
        bookDetails.setIsbn("978-0-999999-99-9");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(bookRepository.existsByTitle(bookDetails.getTitle())).thenReturn(false);
        when(bookRepository.existsByIsbn(bookDetails.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = bookService.updateBook(bookId, bookDetails);

        // Then
        assertNotNull(result);
        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook - powinien rzucić BusinessException gdy nowy tytuł już istnieje")
    void updateBook_ShouldThrowBusinessException_WhenNewTitleAlreadyExists() {
        // Given
        Long bookId = 1L;
        Book bookDetails = new Book();
        bookDetails.setTitle("Istniejący Tytuł");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(bookRepository.existsByTitle(bookDetails.getTitle())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> bookService.updateBook(bookId, bookDetails));
        
        assertEquals("Książka o tytule 'Istniejący Tytuł' już istnieje", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook - powinien rzucić IllegalArgumentException gdy nowy ISBN już istnieje")
    void updateBook_ShouldThrowIllegalArgumentException_WhenNewIsbnAlreadyExists() {
        // Given
        Long bookId = 1L;
        Book bookDetails = new Book();
        String existingIsbn = "978-0-999999-99-9";
        bookDetails.setIsbn(existingIsbn);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(bookRepository.existsByIsbn(existingIsbn)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> bookService.updateBook(bookId, bookDetails));
        
        assertEquals("Książka o ISBN '978-0-999999-99-9' już istnieje", exception.getMessage());
        verify(bookRepository).existsByIsbn(existingIsbn);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook - powinien zaktualizować autorów gdy autorzy są podani")
    void updateBook_ShouldUpdateAuthors_WhenAuthorsProvided() throws BusinessException {
        // Given
        Long bookId = 1L;
        Author newAuthor = new Author();
        try {
            setId(newAuthor, 2L);
        } catch (Exception e) {
            fail("Nie udało się ustawić ID dla autora");
        }

        Book bookDetails = new Book();
        bookDetails.setAuthors(Arrays.asList(newAuthor));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(authorService.findById(2L)).thenReturn(newAuthor);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = bookService.updateBook(bookId, bookDetails);

        // Then
        assertNotNull(result);
        verify(authorService).findById(2L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook - powinien zaktualizować bibliotekę gdy biblioteka jest podana")
    void updateBook_ShouldUpdateLibrary_WhenLibraryProvided() throws BusinessException {
        // Given
        Long bookId = 1L;
        Library newLibrary = new Library();
        try {
            setId(newLibrary, 2L);
        } catch (Exception e) {
            fail("Nie udało się ustawić ID dla biblioteki");
        }

        Book bookDetails = new Book();
        bookDetails.setLibrary(newLibrary);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(libraryService.findById(2L)).thenReturn(newLibrary);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = bookService.updateBook(bookId, bookDetails);

        // Then
        assertNotNull(result);
        verify(libraryService).findById(2L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook - powinien ustawić bibliotekę na null gdy biblioteka ma null ID")
    void updateBook_ShouldSetLibraryToNull_WhenLibraryHasNullId() throws BusinessException {
        // Given
        Long bookId = 1L;
        Library libraryWithNullId = new Library();
        // libraryWithNullId ma null ID (domyślnie)

        Book bookDetails = new Book();
        bookDetails.setLibrary(libraryWithNullId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = bookService.updateBook(bookId, bookDetails);

        // Then
        assertNotNull(result);
        verify(libraryService, never()).findById(anyLong());
        verify(bookRepository).save(any(Book.class));
    }

    // ========== DELETE BOOK TESTS ==========

    @Test
    @DisplayName("deleteBook - powinien usunąć książkę gdy ID istnieje")
    void deleteBook_ShouldDeleteBook_WhenIdExists() {
        // Given
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(bookId);

        // When
        bookService.deleteBook(bookId);

        // Then
        verify(bookRepository).existsById(bookId);
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    @DisplayName("deleteBook - powinien rzucić NoSuchElementException gdy ID nie istnieje")
    void deleteBook_ShouldThrowNoSuchElementException_WhenIdDoesNotExist() {
        // Given
        Long nonExistentId = 999L;
        when(bookRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
            () -> bookService.deleteBook(nonExistentId));
        
        assertEquals("Nie znaleziono książki o ID: 999", exception.getMessage());
        verify(bookRepository).existsById(nonExistentId);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    // ========== EXISTS BY ISBN TESTS ==========

    @Test
    @DisplayName("existsByIsbn - powinien zwrócić true gdy ISBN istnieje")
    void existsByIsbn_ShouldReturnTrue_WhenIsbnExists() {
        // Given
        String isbn = "978-0-123456-78-9";
        when(bookRepository.existsByIsbn(isbn)).thenReturn(true);

        // When
        boolean result = bookService.existsByIsbn(isbn);

        // Then
        assertTrue(result);
        verify(bookRepository).existsByIsbn(isbn);
    }

    @Test
    @DisplayName("existsByIsbn - powinien zwrócić false gdy ISBN nie istnieje")
    void existsByIsbn_ShouldReturnFalse_WhenIsbnDoesNotExist() {
        // Given
        String isbn = "978-0-000000-00-0";
        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);

        // When
        boolean result = bookService.existsByIsbn(isbn);

        // Then
        assertFalse(result);
        verify(bookRepository).existsByIsbn(isbn);
    }

    // ========== EXISTS BY TITLE TESTS ==========

    @Test
    @DisplayName("existsByTitle - powinien zwrócić true gdy tytuł istnieje")
    void existsByTitle_ShouldReturnTrue_WhenTitleExists() {
        // Given
        String title = "Test Book";
        when(bookRepository.existsByTitle(title)).thenReturn(true);

        // When
        boolean result = bookService.existsByTitle(title);

        // Then
        assertTrue(result);
        verify(bookRepository).existsByTitle(title);
    }

    @Test
    @DisplayName("existsByTitle - powinien zwrócić false gdy tytuł nie istnieje")
    void existsByTitle_ShouldReturnFalse_WhenTitleDoesNotExist() {
        // Given
        String title = "Nieistniejąca Książka";
        when(bookRepository.existsByTitle(title)).thenReturn(false);

        // When
        boolean result = bookService.existsByTitle(title);

        // Then
        assertFalse(result);
        verify(bookRepository).existsByTitle(title);
    }

    // ========== EXISTS BY ID TESTS ==========

    @Test
    @DisplayName("existsById - powinien zwrócić true gdy ID istnieje")
    void existsById_ShouldReturnTrue_WhenIdExists() {
        // Given
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);

        // When
        boolean result = bookService.existsById(bookId);

        // Then
        assertTrue(result);
        verify(bookRepository).existsById(bookId);
    }

    @Test
    @DisplayName("existsById - powinien zwrócić false gdy ID nie istnieje")
    void existsById_ShouldReturnFalse_WhenIdDoesNotExist() {
        // Given
        Long bookId = 999L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // When
        boolean result = bookService.existsById(bookId);

        // Then
        assertFalse(result);
        verify(bookRepository).existsById(bookId);
    }
}
