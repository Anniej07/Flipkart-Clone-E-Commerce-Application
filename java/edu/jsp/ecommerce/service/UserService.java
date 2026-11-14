package edu.jsp.ecommerce.service;

import java.util.List;

import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.enums.Role;
import edu.jsp.ecommerce.request.RegisterRequest;
import edu.jsp.ecommerce.response.UserResponse;

public interface UserService {

	public User register(RegisterRequest request);

	UserResponse getProfile(String email);

	UserResponse getUserById(Long id);

	List<UserResponse> getAllUsers();

	void addRole(Long userId, Role role);

	void removeRole(Long userId, Role role);

	void toggleUserEnabled(Long userId);

}
