package forum.latam.alura.presentation.controller;

import forum.latam.alura.domain.entity.BaseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

@CrossOrigin(origins = "*")
public interface BaseApiController<T extends BaseEntity, ID extends Serializable> {


    @GetMapping
    ResponseEntity<?> getAll();

    @GetMapping("/{id}")
    ResponseEntity<?> getOne(@PathVariable ID id);

    @PostMapping
    ResponseEntity<?> save(@RequestBody T entity);

    @PutMapping("/{id}")
    ResponseEntity<?> update(@PathVariable ID id, @RequestBody T entity);

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable ID id);


}



