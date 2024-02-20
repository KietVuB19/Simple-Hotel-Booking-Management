package practice.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import practice.model.Client;
import practice.model.User;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long>{
	Optional<Client> findByUser(User user);
}
