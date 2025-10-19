package pl.edu.pjwstk.library.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.edu.pjwstk.library.model.Book;
import pl.edu.pjwstk.library.model.Borrow;
import pl.edu.pjwstk.library.model.User;
import pl.edu.pjwstk.library.repository.BorrowRepository;

@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookService bookService;
    private final UserService userService;

    @Autowired
    public BorrowService(BorrowRepository borrowRepository, BookService bookService, UserService userService) {
        this.borrowRepository = borrowRepository;
        this.bookService = bookService;
        this.userService = userService;
    }

    public Borrow createBorrow(Long userId, Long bookId, LocalDateTime dueDate) {
        User user = userService.findById(userId);
        Book book = bookService.findById(bookId);

        Optional<Borrow> existingBorrow = borrowRepository.findByUserIdAndBookIdAndReturnDateIsNull(userId, bookId);
        if (existingBorrow.isPresent()) {
            throw new IllegalArgumentException("Użytkownik już ma wypożyczoną tę książkę");
        }
        
        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setDueDate(dueDate);
        
        return borrowRepository.save(borrow);
    }

    public Borrow findById(Long id) {
        return borrowRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono wypożyczenia o ID: " + id));
    }

    public List<Borrow> findAll() {
        return borrowRepository.findAll();
    }

    public List<Borrow> findByUserId(Long userId) {
        return borrowRepository.findByUserId(userId);
    }

    public List<Borrow> findByBookId(Long bookId) {
        return borrowRepository.findByBookId(bookId);
    }

    public List<Borrow> findActiveBorrows() {
        return borrowRepository.findByReturnDateIsNull();
    }

    public List<Borrow> findOverdueBorrows() {
        return borrowRepository.findOverdueBorrows(LocalDateTime.now());
    }

    public List<Borrow> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return borrowRepository.findByBorrowDateBetween(startDate, endDate);
    }

    public Borrow returnBook(Long borrowId) {
        Borrow borrow = findById(borrowId);
        
        if (borrow.getReturnDate() != null) {
            throw new IllegalArgumentException("Książka została już zwrócona");
        }
        
        borrow.setReturnDate(LocalDateTime.now());
        return borrowRepository.save(borrow);
    }

    public Borrow updateBorrow(Long id, Borrow borrowDetails) {
        Borrow borrow = findById(id);
        
        if (borrowDetails.getDueDate() != null) {
            borrow.setDueDate(borrowDetails.getDueDate());
        }
        
        if (borrowDetails.getReturnDate() != null) {
            borrow.setReturnDate(borrowDetails.getReturnDate());
        }
        
        return borrowRepository.save(borrow);
    }

    public void deleteBorrow(Long id) {
        if (!borrowRepository.existsById(id)) {
            throw new NoSuchElementException("Nie znaleziono wypożyczenia o ID: " + id);
        }
        borrowRepository.deleteById(id);
    }

    public boolean hasActiveBorrows(Long userId) {
        return borrowRepository.countByUserIdAndReturnDateIsNull(userId) > 0;
    }

    public long countActiveBorrows(Long userId) {
        return borrowRepository.countByUserIdAndReturnDateIsNull(userId);
    }

    public boolean isBookBorrowed(Long bookId) {
        List<Borrow> activeBorrows = borrowRepository.findByBookId(bookId);
        return activeBorrows.stream().anyMatch(borrow -> borrow.getReturnDate() == null);
    }
}
