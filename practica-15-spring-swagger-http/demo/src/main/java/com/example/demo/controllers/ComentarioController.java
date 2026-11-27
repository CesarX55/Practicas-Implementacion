package com.example.demo.controllers;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Comment;
import com.example.demo.model.Producto;
import com.example.demo.repo.comentarioRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Comentarios", description = "Consultar y administrar comentarios de productos")
public class ComentarioController {

    @Autowired
    comentarioRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("comentarios")
    @Operation(summary = "Listar comentarios")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista (puede estar vacía)")
    })
    public ResponseEntity<List<Comment>> findAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("comentario/{productId}")
    @Operation(summary = "Crear un elemento asociado a un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creado; el encabezado Location apunta al nuevo recurso"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Comment> add(@RequestBody Comment elemento, @PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(producto -> {
                    elemento.setProducto(producto);
                    Comment saved = repo.save(elemento);
                    URI location = URI.create("/comentario/" + saved.getId());
                    return ResponseEntity.created(location).body(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("comentarios/{productId}")
    @Operation(summary = "Listar comentarios de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista del producto (puede estar vacía)"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Set<Comment>> getByProduct(@PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(Producto::getComentarios)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("comentario/{id}")
    @Operation(summary = "Buscar por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    public ResponseEntity<Comment> getById(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("comentario/{id}")
    @Operation(summary = "Actualizar un elemento existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado"),
        @ApiResponse(responseCode = "404", description = "No encontrado; no se crea automáticamente")
    })
    public ResponseEntity<Comment> put(@PathVariable String id, @RequestBody Comment elemento) {
        return repo.findById(id)
                .map(actual -> {
                    elemento.setProducto(actual.getProducto());
                    elemento.setId(id);
                    return ResponseEntity.ok(repo.save(elemento));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("comentario/{id}")
    @Operation(summary = "Eliminar un elemento")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminado, sin cuerpo"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
