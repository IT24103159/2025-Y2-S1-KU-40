package org.example.unihelpdesk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
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
            // file එකේ original නම ගන්නවා
            String originalFilename = file.getOriginalFilename();

            // original නම එක්ක unique ID එකක් එකතු කරලා අලුත් නමක් හදනවා (එකම නමින් files ආවොත් overwrite නොවෙන්න)
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            // file එක save කරන්න ඕන සම්පූර්ණ path එක හදාගන්නවා
            Path destinationPath = this.root.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), destinationPath);

            // database එකේ save කරන්න, අලුතින් හදපු unique නම විතරක් return කරනවා
            return uniqueFilename;

        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: ".concat(e.getMessage()));
        }
    }

    /**
     * මේ තමයි අලුතින් එකතු කරපු load() method එක.
     * @param filename - download කරන්න අවශ්‍ය file එකේ නම
     * @return - Resource object එකක්
     */
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}