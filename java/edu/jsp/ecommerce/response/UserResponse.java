package edu.jsp.ecommerce.response;

import java.time.LocalDateTime;
import java.util.Set;

import edu.jsp.ecommerce.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String profilePictureUrl;
	private boolean emailVerified;
	private boolean accountEnabled;
	private Set<Role> roles;
	private LocalDateTime createdAt;
}
