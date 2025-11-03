package pl.edu.pjwstk.library.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import pl.edu.pjwstk.library.model.Borrow;
import pl.edu.pjwstk.library.service.BorrowService;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private final BorrowService borrowService;

    @Autowired
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // CREATE - Create new borrow
    @PostMapping
    public ResponseEntity<Borrow> createBorrow(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {
        try {
            Borrow borrow = borrowService.createBorrow(userId, bookId, dueDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(borrow);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // READ - Get all borrows
    @GetMapping
    public ResponseEntity<List<Borrow>> getAllBorrows() {
        List<Borrow> borrows = borrowService.findAll();
        return ResponseEntity.ok(borrows);
    }

    // READ - Get borrow by ID
    @GetMapping("/{id}")
    public ResponseEntity<Borrow> getBorrowById(@PathVariable Long id) {
        try {
            Borrow borrow = borrowService.findById(id);
            return ResponseEntity.ok(borrow);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // READ - Get borrows by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Borrow>> getBorrowsByUserId(@PathVariable Long userId) {
        List<Borrow> borrows = borrowService.findByUserId(userId);
        return ResponseEntity.ok(borrows);
    }

    // READ - Get borrows by book ID
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Borrow>> getBorrowsByBookId(@PathVariable Long bookId) {
        List<Borrow> borrows = borrowService.findByBookId(bookId);
        return ResponseEntity.ok(borrows);
    }

    // READ - Get active borrows
    @GetMapping("/active")
    public ResponseEntity<List<Borrow>> getActiveBorrows() {
        List<Borrow> borrows = borrowService.findActiveBorrows();
        return ResponseEntity.ok(borrows);
    }

    // READ - Get overdue borrows
    @GetMapping("/overdue")
    public ResponseEntity<List<Borrow>> getOverdueBorrows() {
        List<Borrow> borrows = borrowService.findOverdueBorrows();
        return ResponseEntity.ok(borrows);
    }

    // READ - Get borrows by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<Borrow>> getBorrowsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Borrow> borrows = borrowService.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(borrows);
    }

    // UPDATE - Update borrow
    @PutMapping("/{id}")
    public ResponseEntity<Borrow> updateBorrow(@PathVariable Long id, @RequestBody Borrow borrowDetails) {
        try {
            Borrow updatedBorrow = borrowService.updateBorrow(id, borrowDetails);
            return ResponseEntity.ok(updatedBorrow);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE - Return book
    @PutMapping("/{id}/return")
    public ResponseEntity<Borrow> returnBook(@PathVariable Long id) {
        try {
            Borrow returnedBorrow = borrowService.returnBook(id);
            return ResponseEntity.ok(returnedBorrow);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Delete borrow
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrow(@PathVariable Long id) {
        try {
            borrowService.deleteBorrow(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UTILITY - Check if user has active borrows
    @GetMapping("/user/{userId}/has-active")
    public ResponseEntity<Boolean> hasActiveBorrows(@PathVariable Long userId) {
        boolean hasActive = borrowService.hasActiveBorrows(userId);
        return ResponseEntity.ok(hasActive);
    }

    // UTILITY - Count active borrows for user
    @GetMapping("/user/{userId}/active-count")
    public ResponseEntity<Long> countActiveBorrows(@PathVariable Long userId) {
        long count = borrowService.countActiveBorrows(userId);
        return ResponseEntity.ok(count);
    }

    // UTILITY - Check if book is borrowed
    @GetMapping("/book/{bookId}/is-borrowed")
    public ResponseEntity<Boolean> isBookBorrowed(@PathVariable Long bookId) {
        boolean isBorrowed = borrowService.isBookBorrowed(bookId);
        return ResponseEntity.ok(isBorrowed);
    }
}

