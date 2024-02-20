package practice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import practice.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{

}
