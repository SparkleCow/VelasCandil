package com.velas.candil.controllers;

import com.velas.candil.entities.user.User;
import com.velas.candil.models.candle.CategoryEnum;
import com.velas.candil.models.candle.FeatureEnum;
import com.velas.candil.models.candle.MaterialEnum;
import com.velas.candil.models.candle.CandleRequestDto;
import com.velas.candil.models.candle.CandleResponseDto;
import com.velas.candil.models.candle.CandleUpdateDto;
import com.velas.candil.services.product.CandleFacadeService;
import com.velas.candil.services.product.CandleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/candles")
@Tag(name = "Candles", description = "Operations related with candles CRUD")
public class CandleController {

    private final CandleService candleService;
    private final CandleFacadeService candleFacadeService;

    @Operation(summary = "Create a new candle with images")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Candle created successfully",
                    content = @Content(schema = @Schema(implementation = CandleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CandleResponseDto> create(
            @RequestPart("data") CandleRequestDto requestDto,
            @RequestPart("principalImage") MultipartFile principalImage,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal User user
    ) throws IOException {

        CandleResponseDto response = candleFacadeService.create(
                requestDto,
                principalImage,
                images,
                user
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all candles with pagination")
    @ApiResponse(responseCode = "200", description = "Candles retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<CandleResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(candleService.findAll(pageable));
    }

    @Operation(summary = "Get candle by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candle found"),
            @ApiResponse(responseCode = "404", description = "Candle not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CandleResponseDto> findById(
            @Parameter(description = "Candle identifier")
            @PathVariable Long id) {

        return ResponseEntity.ok(candleService.findById(id));
    }

    @Operation(summary = "Update an existing candle")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candle updated successfully"),
            @ApiResponse(responseCode = "404", description = "Candle not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid update data", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CandleResponseDto> update(
            @PathVariable Long id,
            @RequestPart("data") CandleUpdateDto updateDto,
            @RequestPart(value = "principalImage", required = false) MultipartFile principalImage,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal User user
    ) throws IOException {

        CandleResponseDto response = candleFacadeService.update(
                id,
                updateDto,
                principalImage,
                images,
                user
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete candle by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Candle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Candle not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        candleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Find candles by category")
    @ApiResponse(responseCode = "200", description = "Candles filtered by category")
    @GetMapping("/category")
    public ResponseEntity<Page<CandleResponseDto>> findByCategory(
            Pageable pageable,
            @RequestParam CategoryEnum categoryEnum) {

        return ResponseEntity.ok(
                candleService.findByCategory(pageable, categoryEnum));
    }

    @Operation(summary = "Find candles by material")
    @ApiResponse(responseCode = "200", description = "Candles filtered by material")
    @GetMapping("/material")
    public ResponseEntity<Page<CandleResponseDto>> findByMaterial(
            Pageable pageable,
            @RequestParam MaterialEnum materialEnum) {

        return ResponseEntity.ok(
                candleService.findByMaterial(pageable, materialEnum));
    }

    @Operation(summary = "Find candles by feature")
    @ApiResponse(responseCode = "200", description = "Candles filtered by feature")
    @GetMapping("/feature")
    public ResponseEntity<Page<CandleResponseDto>> findByFeature(
            Pageable pageable,
            @RequestParam FeatureEnum featureEnum) {

        return ResponseEntity.ok(
                candleService.findByFeature(pageable, featureEnum));
    }

    @Operation(summary = "Find candles by creation date")
    @ApiResponse(responseCode = "200", description = "Candles filtered by date")
    @GetMapping("/date")
    public ResponseEntity<Page<CandleResponseDto>> findByDate(
            Pageable pageable,
            @RequestParam Date date) {

        return ResponseEntity.ok(
                candleService.findByDate(pageable, date));
    }

    @Operation(summary = "Search candles by name")
    @ApiResponse(responseCode = "200", description = "Candles filtered by name")
    @GetMapping("/search")
    public ResponseEntity<Page<CandleResponseDto>> findByNameContaining(
            Pageable pageable,
            @RequestParam String name) {

        return ResponseEntity.ok(
                candleService.findByNameContaining(pageable, name));
    }
}