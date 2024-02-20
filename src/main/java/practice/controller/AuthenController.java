package practice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import practice.model.Account;
import practice.repository.AccountRepository;

@Controller
@RequestMapping("/authen")
@SessionAttributes("currentAccount")
public class AuthenController {
	
	@Autowired
	private AccountRepository accountRepo;
	
	@ModelAttribute("currentAccount")
	public Account account() {
		return new Account();
	}
	
	@GetMapping
	public String getUser(
			@ModelAttribute("currentAccount") Account currentAccount
			) {
		String currentUsername = SecurityContextHolder
				.getContext().getAuthentication().getName();
		Account account = accountRepo.findByUsername(currentUsername).orElse(null);
		if(account!=null) {
			currentAccount.setup(account);
		}
		return "redirect:/";
	}
}
