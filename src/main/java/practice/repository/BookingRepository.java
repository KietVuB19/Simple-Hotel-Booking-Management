package practice.repository;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import practice.model.Booking;
import practice.model.Client;
import practice.model.Room;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Long>{
	List<Booking> findAllByClient(Client client);
	List<Booking> findAllByRoom(Room room);
}
