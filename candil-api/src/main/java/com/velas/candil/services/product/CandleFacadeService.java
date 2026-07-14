package com.velas.candil.services.product;

import com.velas.candil.entities.user.User;
import com.velas.candil.models.candle.CandleRequestDto;
import com.velas.candil.models.candle.CandleResponseDto;
import com.velas.candil.models.candle.CandleUpdateDto;
import com.velas.candil.services.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandleFacadeService {

    private final CandleService candleService;
    private final FileService fileService;

    public CandleResponseDto create(
            CandleRequestDto data,
            MultipartFile principalImage,
            List<MultipartFile> images,
            User user
    ) throws IOException {

        if (principalImage == null || principalImage.isEmpty()) {
            throw new IllegalArgumentException("Principal image is required");
        }

        String baseKey = UUID.randomUUID().toString();

        String principalKey = fileService.uploadSingleFile(principalImage, baseKey);

        List<String> imageKeys = (images != null && !images.isEmpty())
                ? fileService.uploadMultipleFiles(images, baseKey)
                : List.of();

        CandleRequestDto finalDto = new CandleRequestDto(
                data.name(),
                data.description(),
                principalKey,
                data.stock(),
                data.materialEnums(),
                data.featureEnums(),
                data.categories(),
                imageKeys,
                data.ingredients()
        );

        log.info("Creating candle '{}' by user '{}'", data.name(), user.getUsername());
        return candleService.create(finalDto);
    }

    public CandleResponseDto update(
            Long id,
            CandleUpdateDto data,
            MultipartFile principalImage,
            List<MultipartFile> images,
            User user
    ) throws IOException {

        CandleResponseDto current = candleService.findById(id);

        String principalKey = null;
        List<String> imageKeys = null;

        String baseKey = current.principalImage()
                .substring(
                        "velas/".length(),
                        current.principalImage().indexOf('/', "velas/".length())
                );

        if (principalImage != null && !principalImage.isEmpty()) {
            principalKey = fileService.uploadSingleFile(principalImage, baseKey);
        }

        if (images != null && !images.isEmpty()) {
            imageKeys = fileService.uploadMultipleFiles(images, baseKey);
        }

        CandleUpdateDto finalDto = new CandleUpdateDto(
                data.name(),
                data.description(),
                data.materialEnums(),
                data.featureEnums(),
                data.categories(),
                principalKey,
                imageKeys,
                data.ingredients()
        );

        CandleResponseDto updated = candleService.update(
                finalDto,
                id
        );

        if (principalKey != null) {
            fileService.deleteFile(current.principalImage());
        }

        if (imageKeys != null && !imageKeys.isEmpty()) {
            fileService.deleteFiles(current.images());
        }

        log.info("Updating candle '{}' by user '{}'", updated.name(), user.getUsername());

        return updated;
    }
}
