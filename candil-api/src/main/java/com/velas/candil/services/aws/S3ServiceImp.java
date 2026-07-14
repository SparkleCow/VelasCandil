package com.velas.candil.services.aws;

import com.velas.candil.config.aws.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3ServiceImp implements S3Service{

    private final S3Client s3Client;
    private final AwsProperties awsProperties;
    private final S3Presigner s3Presigner;

    @Value("${spring.destination.folder}")
    private String destinationFolder;

    @Override
    public Boolean uploadFile(String key, Path fileLocation) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .build();

        PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, fileLocation);
        return putObjectResponse.sdkHttpResponse().isSuccessful();
    }

    @Override
    public void downloadFile(String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> responseResponseBytes = s3Client.getObjectAsBytes(getObjectRequest);

        String fileName;
        if(key.contains("/")){
            fileName = key.substring(key.lastIndexOf("/"));
        }else{
            fileName = key;
        }
        String filePath = Paths.get(destinationFolder, fileName).toString();

        File file = new File(filePath);
        file.getParentFile().mkdir();

        try(FileOutputStream fos = new FileOutputStream(file)){
            fos.write(responseResponseBytes.asByteArray());
        }catch (IOException exception){
            throw new IOException("Error downloading file "+exception.getMessage());
        }
    }

    @Override
    public String generatePresignedUploadUrl(String key, Duration duration) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(duration)
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }

    @Override
    public String generatePresignedDownloadUrl(String key, Duration duration) {

        if(key == null){
            return null;
        }
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest =
                s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }

    @Override
    public void deleteFile(String key) {

        if (key == null || key.isBlank()) {
            return;
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    @Override
    public void deleteFiles(List<String> keys) {

        if (keys == null || keys.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> objects = keys.stream()
                .map(key -> ObjectIdentifier.builder()
                        .key(key)
                        .build())
                .toList();

        Delete delete = Delete.builder()
                .objects(objects)
                .build();

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(awsProperties.getBucket())
                .delete(delete)
                .build();

        s3Client.deleteObjects(request);
    }
}