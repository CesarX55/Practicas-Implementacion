package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.Comment;
import com.example.demo.model.Producto;
import com.example.demo.repo.comentarioRepo;
import com.example.demo.repo.productoRepo;

@RestController
public class ComentarioController {

    @Autowired
    comentarioRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("comentarios")
    public List<Comment> findAll() {
        return repo.findAll();
    }

    @PostMapping("comentario/{productId}")
    public Comment add(@RequestBody Comment elemento, @PathVariable String productId) {
        Producto producto = repoProducto.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        elemento.setProducto(producto);
        return repo.save(elemento);
    }

    @GetMapping("comentarios/{productId}")
    public Set<Comment> getByProduct(@PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(Producto::getComentarios)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @GetMapping("comentario/{id}")
    public Optional<Comment> getById(@PathVariable String id) {
        return repo.findById(id);
    }

    @PutMapping("comentario/{id}")
    public Comment put(@PathVariable String id, @RequestBody Comment elemento) {
        repo.findById(id).ifPresent(actual -> elemento.setProducto(actual.getProducto()));
        elemento.setId(id);
        return repo.save(elemento);
    }

    @DeleteMapping("comentario/{id}")
    public String delete(@PathVariable String id) {
        repo.deleteById(id);
        return "{\"success\":true}";
    }
}
