package practice.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import practice.model.Client;
import practice.model.User;
import practice.model.Account;
import practice.repository.AccountRepository;
import practice.repository.ClientRepository;
import practice.repository.UserRepository;

@Controller
@RequestMapping("/account")
@SessionAttributes({ "addedUser", "addedClient", "alteredAccount" })
public class AccountController {

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ClientRepository clientRepo;

	@ModelAttribute("addedUser")
	public User addedUser() {
		return new User();
	}

	@ModelAttribute("addedClient")
	public Client addedClient() {
		return new Client();
	}

	@ModelAttribute("addedAccount")
	public Account addedAccount() {
		return new Account();
	}
	
	@ModelAttribute("alteredAccount")
	public Account account() {
		return new Account();
	}

	@GetMapping
	public String account(Model model, HttpSession session) {
		if (session.getAttribute("currentAccount") != null) {
			Account account = (Account) session.getAttribute("currentAccount");
			model.addAttribute("account", account);
		}
		return "account";
	}

	@GetMapping("/submitInfo")
	public String submitInfo(Model model) {
		model.addAttribute("user", new User());
		return "submitInfo";
	}

	@PostMapping("/submitInfo")
	public String submitClientInfo(Model model, @Valid User user, Errors errors,
			@SessionAttribute("addedUser") User addedUser) {
		if (errors.hasErrors()) {
			return "submitInfo";
		}
		addedUser.setFullname(user.getFullname());
		addedUser.setIdCard(user.getIdCard());
		addedUser.setPhoneNumber(user.getPhoneNumber());
		addedUser.setAddress(user.getAddress());
		addedUser.setEmail(user.getEmail());
		model.addAttribute("client", new Client());
		return "submitClient";
	}

	@PostMapping("/submitClient")
	public String createAccount(Model model, @Valid Client client, Errors errors,
			@SessionAttribute("addedClient") Client addedClient) {
		if (errors.hasErrors()) {
			return "submitClient";
		}
		addedClient.setBankAccount(client.getBankAccount());
		addedClient.setNote(client.getNote());
		model.addAttribute("account", new Account());
		return "createAccount";
	}

	@PostMapping("/create")
	public String registerAccount(@Valid Account account, Errors errors, HttpSession session) {
		if (errors.hasErrors()) {
			return "createAccount";
		}
		User addedUser = (User) session.getAttribute("addedUser");
		Client addedClient = (Client) session.getAttribute("addedClient");
		userRepo.save(addedUser);
		account.setActive(true);
		account.setRoles("ROLE_USER");
		account.setUser(addedUser);
		accountRepo.save(account);
		addedClient.setUser(addedUser);
		clientRepo.save(addedClient);
		return "redirect:/";
	}

	@GetMapping("/list")
	public String viewAccounts(Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("currentAccount");
		model.addAttribute("account", account);
		List<Account> accounts = filterByRole("ROLE_ADMIN", (List<Account>) accountRepo.findAll());
//		List<Account> accounts = new ArrayList<>();
		model.addAttribute("accounts", accounts);
		return "admin/accountList";
	}

	private List<Account> filterByRole(String role, List<Account> accounts) {
		List<Account> list = new ArrayList<>();
		for (Account account : accounts) {
			if (!account.getRoles().equals(role)) {
				list.add(account);
			}
		}
		return list;
	}

	@GetMapping("/list/searching")
	public String search(String keyword, Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("currentAccount");
		model.addAttribute("account", account);
		if (!keyword.isBlank()) {
			List<Account> listResult = filterByRole("ROLE_ADMIN", (List<Account>) accountRepo.search(keyword));
//			List<Account> listResult = accountRepo.search(keyword);
			model.addAttribute("listResult", listResult);
			model.addAttribute("keyword", keyword);
		} else {
			return "redirect:/account/list";
		}
		return "admin/accountListSearch";
	}
	
	@GetMapping("/disable/{id}")
	public String disableAccount(@PathVariable("id") Long id) {
		Account account = accountRepo.findById(id).orElse(null);
		if (account.isActive()) {
			account.setActive(false);
			accountRepo.save(account);
		}
		return "redirect:/account/list";
	}

	@GetMapping("/enable/{id}")
	public String enableAccount(@PathVariable("id") Long id) {
		Account account = accountRepo.findById(id).orElse(null);
		if (!account.isActive()) {
			account.setActive(true);
			accountRepo.save(account);
		}
		return "redirect:/account/list";
	}
	
	@GetMapping("/admin/change/{id}")
	public String updateAccountRole(Model model, HttpSession session, @PathVariable("id") Long id,
			@SessionAttribute("alteredAccount") Account alteredaccount) {
		Account account = (Account) session.getAttribute("currentAccount");
		model.addAttribute("account", account);

		Account account2 = accountRepo.findById(id).orElse(null);
		User user = userRepo.findById(account2.getUser().getId()).orElse(null);

		if (account2 != null) {
			alteredaccount.setId(account2.getId());
			alteredaccount.setUsername(account2.getUsername());
			alteredaccount.setRoles(account2.getRoles());
			alteredaccount.setCreatedAt(account2.getCreatedAt());
			alteredaccount.setPassword(account2.getPassword());
			alteredaccount.setUser(user);
			
			if (account2.isActive()) {
				alteredaccount.setActive(true);
			} else {
				alteredaccount.setActive(false);
			}
		}
		model.addAttribute("account2", account2);
		return "admin/adminAccountDetails";
	}
	@PostMapping("/admin/change/{id}")
	public String confirmRoleChange(Account account, @SessionAttribute("alteredAccount") Account alteredaccount,
			@PathVariable("id") Long id) {
		alteredaccount.setRoles(account.getRoles());
		accountRepo.save(alteredaccount);
		return "redirect:/account/list";
	}
}
