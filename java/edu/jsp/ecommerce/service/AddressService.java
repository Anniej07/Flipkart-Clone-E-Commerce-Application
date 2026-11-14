package edu.jsp.ecommerce.service;

import java.util.List;

import edu.jsp.ecommerce.request.AddressRequest;
import edu.jsp.ecommerce.response.AddressResponse;

public interface AddressService {

	List<AddressResponse> getMyAddresses(String email);

	AddressResponse getAddressById(Long id, String email);

	AddressResponse addAddress(String email, AddressRequest request);

	AddressResponse updateAddress(Long id, String email, AddressRequest request);

	void deleteAddress(Long id, String email);

	AddressResponse setDefaultAddress(Long id, String email);

}
