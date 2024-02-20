package practice.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import practice.model.Account;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
	Optional<Account> findByUsername(String username);
	
	@Query(value = "SELECT * FROM account WHERE MATCH(username,roles) AGAINST (?1)", nativeQuery = true)       
	public List<Account> search(String keyword);
}
