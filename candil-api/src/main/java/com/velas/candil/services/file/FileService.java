package com.velas.candil.services.file;

import com.velas.candil.entities.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService{

    /**
     * Compress the provided file data using ImageIO.
     *
     * @param data Raw file bytes to compress.
     * @return Compressed file bytes.
     */
    byte[] compressProfileImage(byte[] data) throws IOException;

    /**
     * Compress the provided file data using GZIP  algorithm.
     *
     * @param data Raw file bytes to compress.
     * @return Compressed file bytes.
     */
    byte[] compressData(byte[] data) throws IOException;

    /**
     * Decompress previously compressed file data.
     *
     * @param compressedData Compressed file bytes.
     * @return Original (decompressed) file bytes.
     */
    byte[] decompress(byte[] compressedData);


    /**
     * Upload file bytes to AWS S3.
     *
     * @param data     File bytes to upload (maybe compressed/encrypted).
     * @return Public URL or key of the uploaded file.
     */
    String uploadSingleFile(MultipartFile data, String key) throws IOException;

    List<String> uploadMultipleFiles(List<MultipartFile> files, String baseKey) throws IOException;

    /**
     * Upload profile image to AWS S3.
     *
     * @param data     File bytes to upload (compressed).
     * @return Public URL or key of the uploaded file.
     */
    String uploadProfileImageToS3(MultipartFile data, String key, User user) throws IOException;

    /**
     * Download file bytes from AWS S3.
     *
     * @param fileUrl Public URL or key of the file in S3.
     * @return Raw file bytes (exactly as stored).
     */
    byte[] downloadFromS3(String fileUrl);

    void deleteFile(String key);

    void deleteFiles(List<String> keys);
}