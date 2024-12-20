package com.egg.biblioteca.repositorios;


import com.egg.biblioteca.entidades.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AutorRepositorio extends JpaRepository<Autor, UUID> {

}
