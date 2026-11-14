package edu.jsp.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.request.RegisterRequest;
import edu.jsp.ecommerce.response.ApiResponse;
import edu.jsp.ecommerce.response.UserResponse;
import edu.jsp.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@PostMapping
	public ResponseEntity<ApiResponse<User>>   register(@Valid @RequestBody RegisterRequest  registerRequest){
		
		ApiResponse<User> apiResponse = ApiResponse.success(userService.register(registerRequest),"User data saved!!!");
		
		return  ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/me/{email}")
	public  ResponseEntity<ApiResponse<UserResponse>>     getProfile(@PathVariable String email) {
		ApiResponse<UserResponse>  apiResponse =  ApiResponse.success(userService.getProfile(email), "User data received!!");
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/{userId}")
	public  ResponseEntity<ApiResponse<UserResponse>>     getUserById(@PathVariable Long userId) {
		ApiResponse<UserResponse>  apiResponse =  ApiResponse.success(userService.getUserById(userId), "User data received!!");
		return ResponseEntity.ok(apiResponse);
	}
	

}
