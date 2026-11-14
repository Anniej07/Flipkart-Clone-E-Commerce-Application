package edu.jsp.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.request.AddressRequest;
import edu.jsp.ecommerce.response.AddressResponse;
import edu.jsp.ecommerce.response.ApiResponse;
import edu.jsp.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

	private final AddressService addressService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses(@RequestParam String email) {

		return ResponseEntity.ok(ApiResponse.success(addressService.getMyAddresses(email), "Addresses fetched"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<AddressResponse>> getAddress(@PathVariable Long id, @RequestParam String email) {

		return ResponseEntity.ok(ApiResponse.success(addressService.getAddressById(id, email), "Address fetched"));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<AddressResponse>> addAddress(@RequestParam String email,
			@Valid @RequestBody AddressRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(addressService.addAddress(email, request), "Address added"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(@PathVariable Long id, @RequestParam String email,
			@Valid @RequestBody AddressRequest request) {

		return ResponseEntity
				.ok(ApiResponse.success(addressService.updateAddress(id, email, request), "Address updated"));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id, @RequestParam String email) {

		addressService.deleteAddress(id, email);

		return ResponseEntity.ok(ApiResponse.success(null, "Address deleted"));
	}

	@PatchMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable Long id,
            @RequestParam String email) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        addressService.setDefaultAddress(id, email) ,"Default address updated"
                )
        );
    }
}