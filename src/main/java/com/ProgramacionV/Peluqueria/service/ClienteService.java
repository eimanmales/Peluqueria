package com.ProgramacionV.Peluqueria.service;

import com.ProgramacionV.Peluqueria.entity.Cliente;
import com.ProgramacionV.Peluqueria.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Retorna todos los clientes ordenados por apellido
    public List<Cliente> listarTodos() {
        return clienteRepository.findByOrderByApellidoAsc();
    }

    // Busca clientes por nombre o apellido
    public List<Cliente> buscarPorNombre(String texto) {
        return clienteRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(texto, texto);
    }

    // Busca un cliente por su id
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    // Guarda un cliente nuevo o actualiza uno existente
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Elimina un cliente por su id
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    // Incrementa el contador de cortes acumulados del cliente
    public void sumarCorte(Long clienteId) {
        clienteRepository.findById(clienteId).ifPresent(c -> {
            c.setCortesAcumulados(c.getCortesAcumulados() + 1);
            clienteRepository.save(c);
        });
    }
}
