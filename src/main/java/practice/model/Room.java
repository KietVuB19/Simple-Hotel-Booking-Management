package practice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
@Entity
@Table(name = "Room")
public class Room {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "Nhập số phòng")
	private String name;
	@NotBlank(message = "Chọn loại phòng")
	private String type;
	@NotNull(message = "Nhập giá phòng")
	private Long price;
	@NotBlank(message = "Nhập số giường")
	private String bed;
	private boolean isAvailable;
	private String description;
}
