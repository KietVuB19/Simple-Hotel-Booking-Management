package practice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import practice.model.MyUserDetails;
import practice.model.Account;
import practice.repository.AccountRepository;
@Service
public class MyUserDetailsService implements UserDetailsService{
	
	@Autowired
	private AccountRepository accountRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Account> account = accountRepo.findByUsername(username);
		account.orElseThrow(() -> new UsernameNotFoundException(username + " not found"));	
		return account.map(MyUserDetails::new).get();
	}
}