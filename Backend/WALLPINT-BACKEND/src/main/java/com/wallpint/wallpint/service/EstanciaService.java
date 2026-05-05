package com.wallpint.wallpint.service;

import com.wallpint.wallpint.model.Estancia;
import com.wallpint.wallpint.repository.EstanciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstanciaService {

    @Autowired
    private EstanciaRepository estanciaRepository;

    public List<Estancia> obtenerTodas() {
        return estanciaRepository.findAll();
    }

    public Optional<Estancia> obtenerPorId(Long id) {
        return estanciaRepository.findById(id);
    }

    public Estancia guardar(Estancia estancia) {
        return estanciaRepository.save(estancia);
    }

    public void eliminar(Long id) {
        estanciaRepository.deleteById(id);
    }
}