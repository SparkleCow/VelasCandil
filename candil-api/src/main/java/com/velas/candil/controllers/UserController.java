package com.velas.candil.controllers;

import com.velas.candil.entities.user.User;
import com.velas.candil.models.user.AuthResponseDto;
import com.velas.candil.models.user.UserInformationDto;
import com.velas.candil.models.user.UserUpdateDto;
import com.velas.candil.services.file.FileService;
import com.velas.candil.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Operations related to user management and profile")
public class UserController {

    private final UserService userService;
    private final FileService fileService;

    @Operation(
            summary = "Get all users except current user",
            description = "Returns a list of all registered users excluding the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping()
    public ResponseEntity<List<UserInformationDto>> findAllUsersExceptSelf(
            @Parameter(hidden = true) Authentication authentication){
        return ResponseEntity.ok(userService.findAllUsersExceptSelf(authentication));
    }

    @Operation(
            summary = "Get current user information",
            description = "Returns the information of the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping("/self")
    public ResponseEntity<UserInformationDto> findLoggedUsed(
            @Parameter(hidden = true) Authentication authentication){
        return ResponseEntity.ok(userService.getUserInformation(authentication));
    }

    @Operation(
            summary = "Search users by username",
            description = "Returns a list of users whose username contains the given value."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter")
    })
    @GetMapping("/filter")
    public ResponseEntity<List<UserInformationDto>> findAllUsersContaining(
            @Parameter(description = "Username to search", required = true)
            @RequestParam("username") String username){
        return ResponseEntity.ok(userService.findUsersByUsernameContaining(username));
    }

    @Operation(
            summary = "Upload profile image",
            description = "Uploads a profile image to S3 and updates the user's profile image URL."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PutMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "Key/path to store the image in S3", required = true)
            @RequestParam String key,
            @Parameter(description = "Image file to upload", required = true)
            @RequestPart MultipartFile file,
            @Parameter(hidden = true) Authentication authentication) throws IOException {

        String imagePath = fileService.uploadProfileImageToS3(
                file,
                key,
                (User) authentication.getPrincipal()
        );

        return ResponseEntity.ok(
                userService.updateUserImage(authentication, imagePath)
        );
    }

    @Operation(
            summary = "Update username",
            description = "Updates the username of the authenticated user and returns a new JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PutMapping("/update")
    public ResponseEntity<AuthResponseDto> updateUsername(
            @Parameter(description = "New username data", required = true)
            @RequestBody UserUpdateDto userUpdateDto,
            @Parameter(hidden = true) Authentication authentication){

        return ResponseEntity.ok(
                userService.updateUsername(authentication, userUpdateDto)
        );
    }
}