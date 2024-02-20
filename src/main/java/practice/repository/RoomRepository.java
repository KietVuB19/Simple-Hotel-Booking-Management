package practice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import practice.model.Room;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long>{
	
	@Query(value = "SELECT * FROM room WHERE is_available = 1 AND price between ? and ?", nativeQuery = true) 
	public List<Room> searchPrice(Long minPrice, Long maxPrice);
	
	@Query(value = "SELECT * FROM room WHERE bed LIKE ?% AND price between ? and ?", nativeQuery = true) 
	public List<Room> searchAllCond( Long bed, Long minPrice, Long maxPrice);

	@Query(value = "SELECT * FROM room WHERE MATCH (name,description,bed) AGAINST (?)", nativeQuery = true)       
	 public List<Room> search(String keyword);
	
}