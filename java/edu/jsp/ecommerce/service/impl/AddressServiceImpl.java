package edu.jsp.ecommerce.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.jsp.ecommerce.entity.Address;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.exeception.BadRequestException;
import edu.jsp.ecommerce.exeception.ResourceNotFoundException;
import edu.jsp.ecommerce.repository.AddressRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import edu.jsp.ecommerce.request.AddressRequest;
import edu.jsp.ecommerce.response.AddressResponse;
import edu.jsp.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;

	@Override
	public List<AddressResponse> getMyAddresses(String email) {
		User user = findUser(email);

		return addressRepository.findByUserId(user.getId())
				.stream()
				.map(a -> mapToResponse(a))
				.toList();

	}

	@Override
	public AddressResponse getAddressById(Long id, String email) {
		return mapToResponse(findOwnedAddress(id, email));
	}

	@Override
	public AddressResponse addAddress(String email, AddressRequest request) {

		User user = findUser(email);
		Address address = buildFromRequest(request);
		address.setUser(user);

		List<Address> existsingAddresses = addressRepository.findByUserId(user.getId());

		if (existsingAddresses.isEmpty()) {
			address.setDefaultAddress(true);
		} else if (request.isDefaultAddress()) {
			existsingAddresses.forEach(a -> a.setDefaultAddress(false));

			addressRepository.saveAll(existsingAddresses);
		}

		return mapToResponse(addressRepository.save(address));
	}

	@Override
	public AddressResponse updateAddress(Long id, String email, AddressRequest request) {
		
		User user = findUser(email);
        Address address = findOwnedAddress(id, email);

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry() != null ? request.getCountry() : "India");
        address.setType(request.getType());
        
        if(request.isDefaultAddress() && !address.isDefaultAddress()) {
    		List<Address> existsingAddresses = addressRepository.findByUserId(user.getId());
    		existsingAddresses.forEach(a ->  a.setDefaultAddress(false));
    		addressRepository.saveAll(existsingAddresses);
        	address.setDefaultAddress(true);
        }
		
		return mapToResponse(addressRepository.save(address));
	}

	@Override
	public void deleteAddress(Long id, String email) {

		User user = findUser(email);
		Address address = findOwnedAddress(id, email);

		boolean isdefault = address.isDefaultAddress();

		addressRepository.delete(address);

		if (isdefault) {
			List<Address> existsingAddresses = addressRepository.findByUserId(user.getId());
			if (!existsingAddresses.isEmpty()) {
				Address defads = existsingAddresses.get(0);
				defads.setDefaultAddress(true);
				addressRepository.save(defads);
			}
		}

	}

	@Override
	public AddressResponse setDefaultAddress(Long id, String email) {

		User user = findUser(email);
		Address address = findOwnedAddress(id, email);

		List<Address> existsingAddresses = addressRepository.findByUserId(user.getId());

		existsingAddresses.
		forEach(a -> a.setDefaultAddress(a.getId().equals(id)));

		addressRepository.saveAll(existsingAddresses);

		return mapToResponse(address);
	}

	private User findUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(email + "  not found!!!"));
	}

	private Address findOwnedAddress(Long id, String email) {
		User user = findUser(email);

		Address address = addressRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(id + "  not found!!!"));

		if (!(address.getUser().getId() == user.getId())) {
			throw new BadRequestException("Access  denied!!");
		}

		return address;

	}

	private Address buildFromRequest(AddressRequest request) {
		return Address.builder()
				.fullName(request.getFullName())
				.phone(request.getPhone())
				.addressLine1(request.getAddressLine1())
				.addressLine2(request.getAddressLine2())
				.city(request.getCity())
				.state(request.getState())
				.postalCode(request.getPostalCode())
				.country(request.getCountry() != null ? request.getCountry() : "India")
				.type(request.getType())
				.defaultAddress(request.isDefaultAddress()).build();
	}

	private AddressResponse mapToResponse(Address a) {
		return AddressResponse.
				builder().
				id(a.getId()).fullName(a.getFullName()).phone(a.getPhone())
				.addressLine1(a.getAddressLine1()).addressLine2(a.getAddressLine2()).city(a.getCity())
				.state(a.getState()).postalCode(a.getPostalCode()).country(a.getCountry()).type(a.getType())
				.defaultAddress(a.isDefaultAddress()).build();
	}
}
