package practice.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity

@Table(name = "Account")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Size(min=5, message = "Tên đăng nhập ít nhất 5 kí tự")
	private String username;
	@Size(min=5, message = "Mật khẩu ít nhất 5 kí tự")
	private String password;
	private boolean active;
	private String roles;

	private Date createdAt;
	
	@ManyToOne(targetEntity = User.class, cascade = CascadeType.MERGE)
	public User user;

	public void setup(Account account2) {
		this.id = account2.id;
		this.username = account2.username;
		this.password = account2.password;
		this.active = account2.active;
		this.roles = account2.roles;
		this.createdAt = account2.getCreatedAt();
		this.user = account2.getUser();
	}

	@PrePersist
	private void createdAt() {
		this.createdAt = new Date();
	}
}
