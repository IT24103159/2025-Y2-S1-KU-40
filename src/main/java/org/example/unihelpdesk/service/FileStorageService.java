package org.example.unihelpdesk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.root = Paths.get(uploadDir);
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!", e);
        }
    }

    public String save(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();

            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            Path destinationPath = this.root.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), destinationPath);

            return destinationPath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}