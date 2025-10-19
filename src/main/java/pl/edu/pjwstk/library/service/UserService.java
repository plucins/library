package pl.edu.pjwstk.library.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.edu.pjwstk.library.model.User;
import pl.edu.pjwstk.library.model.UserType;
import pl.edu.pjwstk.library.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email nie może być pusty");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Użytkownik o emailu '" + user.getEmail() + "' już istnieje");
        }
        
        if (user.getType() == null) {
            user.setType(UserType.USER);
        }
        
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono użytkownika o ID: " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono użytkownika o emailu: " + email));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findUsersWithActiveBorrows() {
        return userRepository.findUsersWithActiveBorrows();
    }

    public User updateUser(Long id, User userDetails) {
        User user = findById(id);
        
        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }
        
        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }
        
        if (userDetails.getEmail() != null && !userDetails.getEmail().trim().isEmpty()) {
            if (!user.getEmail().equals(userDetails.getEmail()) && 
                userRepository.existsByEmail(userDetails.getEmail())) {
                throw new IllegalArgumentException("Użytkownik o emailu '" + userDetails.getEmail() + "' już istnieje");
            }
            user.setEmail(userDetails.getEmail());
        }
        
        if (userDetails.getType() != null) {
            user.setType(userDetails.getType());
        }
        
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("Nie znaleziono użytkownika o ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    // UTILITY - Count active borrows for user
    public long countActiveBorrows(Long userId) {
        return userRepository.countActiveBorrowsByUserId(userId);
    }
}
