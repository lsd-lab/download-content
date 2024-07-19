package br.edson.download_content.controllers;

import br.edson.download_content.configs.FileStorageProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("download")
public class DownloadFile {
    @Value("${relative.path}")
    private String RELATIVE_PATH;

    @Value("${script.path.py}")
    private String SCRIPT_PATH;

    private final Path fileStorageLocation;

    public DownloadFile(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
    }

    @GetMapping
    public ResponseEntity downloadFile(HttpServletRequest request) {
        try {
            var url = request.getParameter("url");

            String path = RELATIVE_PATH+SCRIPT_PATH;

            ProcessBuilder processBuilder = new ProcessBuilder("python3", path, url);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Boolean findFilename = false;
            String filename = "";

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (!findFilename) {
                    if (line.contains("uploads/")) {
                        filename = line.split("uploads/")[1];
                        if (filename.contains(" has already been downloaded")) {
                            filename = filename.split(" has already been downloaded")[0];
                        }

                        findFilename = true;
                    }
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

            // Verifica se o arquivo foi criado
            Path filePath = fileStorageLocation.resolve(filename).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
          return ResponseEntity.internalServerError().build();
        }
    }
}
