package edu.jsp.ecommerce.entity;

import java.util.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(name = "first_name", nullable = false)
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "phone_number", unique = true)
	private String phone;
	@Column(name = "profile_picture_url")
	private String profilePictureUrl;
	@Column(name = "email_verified")
	@Builder.Default
	private boolean emailVerfied  =  false;
	@Column(name = "account_locked")
	@Builder.Default
	private boolean accountLocked  = false;
	@Column(name = "account_enabled")
	@Builder.Default
	private boolean accountEnabled = true;
	
	@Builder.Default
	@ManyToMany(fetch = FetchType.EAGER,  cascade = {CascadeType.PERSIST,CascadeType.MERGE} )
	@JoinTable(name = "users_roles",
			joinColumns =  @JoinColumn(name = "user_id",referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name="role_id",referencedColumnName = "id")
			)
	private Set<RoleEntity>  roles  = new HashSet<RoleEntity>();

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
	private List<Address>  addresses = new ArrayList<Address>();
	
}
