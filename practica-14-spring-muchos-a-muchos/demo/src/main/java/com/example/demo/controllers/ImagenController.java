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

import com.example.demo.model.Image;
import com.example.demo.model.Producto;
import com.example.demo.repo.imagenRepo;
import com.example.demo.repo.productoRepo;

@RestController
public class ImagenController {

    @Autowired
    imagenRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("imagenes")
    public List<Image> findAll() {
        return repo.findAll();
    }

    @PostMapping("imagen/{productId}")
    public Image add(@RequestBody Image elemento, @PathVariable String productId) {
        Producto producto = repoProducto.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        elemento.setProducto(producto);
        return repo.save(elemento);
    }

    @GetMapping("imagenes/producto/{productId}")
    public Set<Image> getByProduct(@PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(Producto::getImagenes)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @GetMapping("imagen/{id}")
    public Optional<Image> getById(@PathVariable String id) {
        return repo.findById(id);
    }

    @PutMapping("imagen/{id}")
    public Image put(@PathVariable String id, @RequestBody Image elemento) {
        repo.findById(id).ifPresent(actual -> elemento.setProducto(actual.getProducto()));
        elemento.setId(id);
        return repo.save(elemento);
    }

    @DeleteMapping("imagen/{id}")
    public String delete(@PathVariable String id) {
        repo.deleteById(id);
        return "{\"success\":true}";
    }
}
