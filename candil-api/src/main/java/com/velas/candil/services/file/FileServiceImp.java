package com.velas.candil.services.file;

import com.velas.candil.config.aws.AwsProperties;
import com.velas.candil.entities.user.User;
import com.velas.candil.exceptions.infra.FileCompressionException;
import com.velas.candil.services.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
@RequiredArgsConstructor
public class FileServiceImp implements FileService{

    private final S3Service s3Service;
    private final AwsProperties awsProperties;

    @Value("${spring.destination.folder}")
    private String destinationFolder;

    public byte[] compressProfileImage(byte[] inputBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);
        try (InputStream test = new ByteArrayInputStream(inputBytes)) {
            System.out.println("Readers available: " +
                    ImageIO.getImageReaders(ImageIO.createImageInputStream(test)).hasNext()
            );
        }
        BufferedImage originalImage = ImageIO.read(bais);

        if (originalImage == null) {
            throw new IOException("No se pudo leer la imagen de entrada");
        }

        BufferedImage rgbImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = rgbImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, java.awt.Color.WHITE, null); // Fills the background to white if it was png

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.3f); // quality
            }

            writer.write(null, new javax.imageio.IIOImage(rgbImage, null, null), param);
            writer.dispose();
        }

        return baos.toByteArray();
    }

    @Override
    public byte[] compressData(byte[] data){
        if (data == null || data.length == 0) return data;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(baos)) {

            gzip.write(data);
            gzip.close();
            return baos.toByteArray();

        } catch (IOException e) {
            throw new FileCompressionException("Error compressing data " + e.getMessage());
        }
    }

    @Override
    public byte[] decompress(byte[] compressedData) {
        if (compressedData == null || compressedData.length == 0) return compressedData;

        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzip = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzip.read(buffer)) > 0) {
                baos.write(buffer);
            }
            return baos.toByteArray();

        } catch (IOException e) {
            throw new FileCompressionException("Error decompressing data "+ e);
        }
    }

    @Override
    public String uploadSingleFile(MultipartFile file, String baseKey) throws IOException {

        Path path = Paths.get(destinationFolder);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String originalName = Objects.requireNonNull(file.getOriginalFilename());

        // Sanitizer
        String safeName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        // Unique name
        String uniqueName = System.currentTimeMillis() + "_" + safeName;

        Path filePath = path.resolve(uniqueName);

        try {
            Files.write(filePath, file.getBytes());

            String key = "velas/" + baseKey + "/principal_" + uniqueName;

            if (Boolean.TRUE.equals(s3Service.uploadFile(key, filePath))) {
                return key;
            }

            return null;

        } finally {
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {}
        }
    }

    @Override
    public List<String> uploadMultipleFiles(List<MultipartFile> files, String baseKey) throws IOException {

        List<String> uploadedKeys = new ArrayList<>();

        Path path = Paths.get(destinationFolder);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        for (MultipartFile file : files) {

            String originalName = Objects.requireNonNull(file.getOriginalFilename());

            // Sanitizer
            String safeName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            String uniqueName = UUID.randomUUID() + "_" + safeName;

            Path filePath = path.resolve(uniqueName);

            try {
                Files.write(filePath, file.getBytes());

                String key = "velas/" + baseKey + "/" + uniqueName;

                if (Boolean.TRUE.equals(s3Service.uploadFile(key, filePath))) {
                    uploadedKeys.add(key);
                }

            } finally {
                // Siempre intenta borrar el archivo temporal
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException ignored) {}
            }
        }

        return uploadedKeys;
    }

    @Override
    public String uploadProfileImageToS3(MultipartFile data, String key, User user) throws IOException {

        byte[] bytes = data.getBytes();
        System.out.println("BYTES LENGTH: " + bytes.length);
        System.out.println(Arrays.toString(Arrays.copyOf(bytes, 10)));

        Path path = Paths.get(destinationFolder);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        byte[] compressedBytes = compressProfileImage(data.getBytes());

        Path filePath = path.resolve(Objects.requireNonNull(data.getOriginalFilename()));
        Path finalPath = Files.write(filePath, compressedBytes);

        key = user.getUsername() + "/" + key;

        if (Boolean.TRUE.equals(s3Service.uploadFile(key, finalPath))) {
            Files.delete(filePath);
            return key;
        }

        return null;
    }

    @Override
    public byte[] downloadFromS3(String fileUrl) {
        return new byte[0];
    }
}