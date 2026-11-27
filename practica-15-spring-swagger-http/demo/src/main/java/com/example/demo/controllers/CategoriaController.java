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

import com.example.demo.model.Department;
import com.example.demo.model.Producto;
import com.example.demo.repo.categoriaRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Categorías", description = "Consultar y administrar categorías y su relación con productos")
public class CategoriaController {

    @Autowired
    categoriaRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("categorias")
    @Operation(summary = "Listar categorías")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de categorías (puede estar vacía)")
    })
    public ResponseEntity<List<Department>> findAllcategoria() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("categoria")
    @Operation(summary = "Crear una categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría creada; el encabezado Location apunta a /categoria/{id}")
    })
    public ResponseEntity<Department> addcategoria(@RequestBody Department categoria) {
        Department saved = repo.save(categoria);
        URI location = URI.create("/categoria/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("addProductoCategoria/{productId}/{categoriaId}")
    @Operation(summary = "Asociar un producto existente con una categoría existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relación creada"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto o la categoría")
    })
    public ResponseEntity<Department> addProductoCategoria(@PathVariable String productId,
            @PathVariable String categoriaId) {
        Producto producto = repoProducto.findById(productId).orElse(null);
        Department categoria = repo.findById(categoriaId).orElse(null);
        if (producto == null || categoria == null) {
            return ResponseEntity.notFound().build();
        }
        categoria.getProductos().add(producto);
        return ResponseEntity.ok(repo.save(categoria));
    }

    @GetMapping("categoria/producto/{productId}")
    @Operation(summary = "Listar las categorías de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categorías del producto (puede estar vacía)"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Set<Department>> getDepartmentsByProduct(@PathVariable String productId) {
        return repoProducto.findById(productId)
                .map(Producto::getDeparments)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("categoria/{id}")
    @Operation(summary = "Buscar una categoría por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la categoría")
    })
    public ResponseEntity<Department> getById(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("categoria/{id}")
    @Operation(summary = "Actualizar una categoría existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la categoría; no se crea automáticamente")
    })
    public ResponseEntity<Department> putDepartment(@PathVariable String id, @RequestBody Department categoria) {
        return repo.findById(id)
                .map(actual -> {
                    categoria.setId(id);
                    // Se conservan los productos asociados: Department es el lado propietario
                    categoria.setProductos(actual.getProductos());
                    return ResponseEntity.ok(repo.save(categoria));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("categoria/{id}")
    @Operation(summary = "Eliminar una categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada, sin cuerpo"),
        @ApiResponse(responseCode = "404", description = "No se encontró la categoría")
    })
    public ResponseEntity<Void> deleteDeparment(@PathVariable String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
