package com.example.demo.libro;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Libro;
import com.example.demo.repo.LibroRepository;

@RestController
public class LibroController {
    private final LibroRepository repo;

    public LibroController(LibroRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/libros")
    public List<Libro> findAllLibros() {
        return repo.findAll();
    }

    @PostMapping("/libros")
    public Libro addLibro(@RequestBody Libro libro) {
        return repo.save(libro);
    }
}
