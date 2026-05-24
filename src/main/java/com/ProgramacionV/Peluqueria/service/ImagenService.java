package com.ProgramacionV.Peluqueria.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImagenService {

    // Carpeta donde se guardan las imágenes (se crea automáticamente)
    private static final String CARPETA = "uploads/servicios/";

    /**
     * Guarda la imagen en disco y retorna la ruta relativa para guardar en BD.
     * Ej: /uploads/servicios/abc123-corte.jpg
     */
    public String guardarImagen(MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) return null;

        // Crear la carpeta si no existe
        Path carpetaPath = Paths.get(CARPETA);
        if (!Files.exists(carpetaPath)) {
            Files.createDirectories(carpetaPath);
        }

        // Generar nombre único para evitar colisiones
        String extension   = obtenerExtension(archivo.getOriginalFilename());
        String nombreUnico = UUID.randomUUID() + extension;

        // Guardar el archivo en disco
        Path destino = carpetaPath.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return "/" + CARPETA + nombreUnico;  // ruta para usar en <img src="...">
    }

    /** Elimina una imagen del disco dado su URL relativa. */
    public void eliminarImagen(String imagenUrl) {
        if (imagenUrl == null || imagenUrl.isBlank()) return;
        try {
            Path archivo = Paths.get(imagenUrl.substring(1)); // quita el "/" inicial
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            // Si falla al borrar, no interrumpimos la operación principal
            System.err.println("No se pudo eliminar imagen: " + imagenUrl);
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) return ".jpg";
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".")).toLowerCase();
    }
}
