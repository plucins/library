package pl.edu.pjwstk.library.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.edu.pjwstk.library.model.Library;
import pl.edu.pjwstk.library.repository.LibraryRepository;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;

    @Autowired
    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    public Library createLibrary(Library library) {
        if (library.getName() == null || library.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa biblioteki nie może być pusta");
        }
        
        if (libraryRepository.existsByName(library.getName())) {
            throw new IllegalArgumentException("Biblioteka o nazwie '" + library.getName() + "' już istnieje");
        }
        
        return libraryRepository.save(library);
    }

    public Library findById(Long id) {
        return libraryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono biblioteki o ID: " + id));
    }

    public Library findByName(String name) {
        return libraryRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono biblioteki o nazwie: " + name));
    }

    public List<Library> findAll() {
        return libraryRepository.findAll();
    }

    public Library updateLibrary(Long id, Library libraryDetails) {
        Library library = findById(id);
        
        if (libraryDetails.getName() != null && !libraryDetails.getName().trim().isEmpty()) {
            if (!library.getName().equals(libraryDetails.getName()) && 
                libraryRepository.existsByName(libraryDetails.getName())) {
                throw new IllegalArgumentException("Biblioteka o nazwie '" + libraryDetails.getName() + "' już istnieje");
            }
            library.setName(libraryDetails.getName());
        }
        
        return libraryRepository.save(library);
    }

    public void deleteLibrary(Long id) {
        if (!libraryRepository.existsById(id)) {
            throw new NoSuchElementException("Nie znaleziono biblioteki o ID: " + id);
        }
        libraryRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return libraryRepository.existsByName(name);
    }

    public boolean existsById(Long id) {
        return libraryRepository.existsById(id);
    }
}
