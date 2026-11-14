package edu.jsp.ecommerce.exeception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.web.error.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edu.jsp.ecommerce.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleResourcesNotFoundException(ResourceNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(ex.getMessage()));

	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse<String>> handleBadRequestException(BadRequestException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException ex) {

		HashMap<String, String> map = new HashMap<String, String>();

		List<FieldError> errors = ex.getFieldErrors();

		for (FieldError f : errors) {
			map.put(f.getField(), f.getDefaultMessage());
		}
		
		ApiResponse<Map<String,String>>   apiResponse = ApiResponse.<Map<String,String>>builder()
													    .data(map)
													    .success(false)
													    .message("Bad  request!!")
													    .build();
													    

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(apiResponse);

	}

}
