package com.velas.candil.services.aws;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public interface S3Service {

    Boolean uploadFile(String key, Path fileLocation);

    void downloadFile(String key) throws IOException;

    String generatePresignedUploadUrl(String key, Duration duration);

    String generatePresignedDownloadUrl(String key, Duration duration);

    void deleteFile(String key);

    void deleteFiles(List<String> keys);
}
