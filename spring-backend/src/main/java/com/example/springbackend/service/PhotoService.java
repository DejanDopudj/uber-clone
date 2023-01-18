package com.example.springbackend.service;

import com.example.springbackend.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

@Service
public class PhotoService {
    public String storeImage(MultipartFile multipartFile, Authentication auth) {
        String fileName = ((User) auth.getPrincipal()).getUsername() + "." + multipartFile.getContentType().split("/")[1];
        String uploadDir = "user-photos/" ;
        Path uploadPath = Paths.get(uploadDir);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ioe) {
            ioe.getStackTrace();
            return null;
        }
    }

    public String loadImage(String photoName) {
        String image = "";
        String uploadDir = "user-photos/" + photoName;
        File file = new File(uploadDir);
        String encodeBase64 = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStream.read(bytes);
            encodeBase64 = Base64.getEncoder().encodeToString(bytes);
            image = "data:image/" + photoName.split("\\.")[photoName.split("\\.").length - 1] + ";base64,"+encodeBase64;
            fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }
}
