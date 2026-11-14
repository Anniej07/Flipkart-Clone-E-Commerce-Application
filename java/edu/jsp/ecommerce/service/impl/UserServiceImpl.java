package edu.jsp.ecommerce.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import edu.jsp.ecommerce.entity.RoleEntity;
import edu.jsp.ecommerce.entity.Seller;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.enums.Role;
import edu.jsp.ecommerce.exeception.BadRequestException;
import edu.jsp.ecommerce.exeception.ResourceNotFoundException;
import edu.jsp.ecommerce.repository.RoleEntityRepository;
import edu.jsp.ecommerce.repository.SellerRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import edu.jsp.ecommerce.request.RegisterRequest;
import edu.jsp.ecommerce.response.UserResponse;
import edu.jsp.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleEntityRepository roleEntityRepository;
	private final SellerRepository sellerRepository;

	public User register(RegisterRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BadRequestException("Email Already exist!!!s");
		}

		HashSet<RoleEntity> roles = new HashSet<RoleEntity>();

		if (request.getRoles() != null) {
			request.getRoles().stream().filter(r -> r != Role.ROLE_ADMIN)
					.forEach(r -> roleEntityRepository.findByName(r).ifPresent(roles::add));
		}

		if (roles.isEmpty()) {
			roleEntityRepository.findByName(Role.ROLE_CUSTOMER).ifPresent(roles::add);
		}

		User user = User.builder().email(request.getEmail()).password(request.getPassword())
				.firstName(request.getFirstName()).lastName(request.getLastName()).phone(request.getPhoneNumber())
				.roles(roles).build();

		user = userRepository.save(user);

		Boolean isSeller = roles.stream().anyMatch(r -> r.getName() == Role.ROLE_SELLER);

		if (isSeller) {

			Seller seller = Seller.builder().user(user).businessName(user.getFirstName() + " store!!!").approved(true)
					.build();
			sellerRepository.save(seller);

		}

		return user;

	}

	@Override
	public UserResponse getProfile(String email) {

		User u = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(email + "  not found!!!"));

		return mapToResponse(u);
	}

	@Override
	public UserResponse getUserById(Long id) {
		return mapToResponse(findbyId(id));
	}

	@Override
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(u -> mapToResponse(u)).toList();

	}

	@Override
	public void addRole(Long userId, Role role) {

		User u = findbyId(userId);

		RoleEntity re = roleEntityRepository.findByName(role)
				.orElseThrow(() -> new ResourceNotFoundException(role + "  not found!!!"));
		u.getRoles().add(re);

		userRepository.save(u);
	}

	@Override
	public void removeRole(Long userId, Role role) {
		User u = findbyId(userId);

		if (u.getRoles().size() <= 1) {
			throw new BadRequestException("User should have atleast one role");
		}

		RoleEntity re = roleEntityRepository.findByName(role).orElseThrow();
		u.getRoles().removeIf(r -> r.getName() == re.getName());

		userRepository.save(u);

	}

	@Override
	public void toggleUserEnabled(Long userId) {
		User u = findbyId(userId);
		u.setAccountEnabled(!u.isAccountEnabled());
		userRepository.save(u);
	}

	private User findbyId(long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id + "  not found!!!"));
	}

	public UserResponse mapToResponse(User u) {

		HashSet<Role> roles = new HashSet<Role>();

		Set<RoleEntity> re = u.getRoles();

		for (RoleEntity r : re) {
			roles.add(r.getName());
		}

		return UserResponse.builder().id(u.getId()).email(u.getEmail()).firstName(u.getFirstName())
				.lastName(u.getLastName()).phoneNumber(u.getPhone()).profilePictureUrl(u.getProfilePictureUrl())
				.emailVerified(u.isEmailVerfied()).accountEnabled(u.isAccountEnabled()).roles(roles)
				.createdAt(u.getCreatedAt()).build();

	}

}
