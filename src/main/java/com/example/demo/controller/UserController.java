package com.example.demo.controller;

import com.example.demo.dto.SaveUserDto;
import com.example.demo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(description = "swagger test controller")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation =  String.class)))
    @PostMapping("signIn")
    public void signIn(@RequestBody SaveUserDto saveUserDto){
        userService.save(saveUserDto);
    }

    @GetMapping("temporary-numbers/{name}")
    public void temporaryNumbers(@PathVariable String name) {
        userService.temporaryNumbers(name);
    }
}
