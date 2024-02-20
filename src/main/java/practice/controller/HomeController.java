package practice.controller;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.extern.slf4j.Slf4j;
import practice.model.Account;
import practice.model.Booking;
import practice.repository.BookingRepository;

@Slf4j
@Controller
public class HomeController {

	@Autowired
	private BookingRepository bookingRepo;
	
	@GetMapping("/")
	public String home(Model model, HttpSession session) {
		if (session.getAttribute("currentAccount") != null) {
			Account account = (Account) session.getAttribute("currentAccount");
			model.addAttribute("account", account);

			if (account.getRoles().equalsIgnoreCase("role_admin")) {
				return "/admin/adminHomepage";
			}
			if (account.getRoles().equalsIgnoreCase("role_manager")) {
				Long totalPrice = (long) 0;
				Long count = (long) 0;
				List<Booking> bookingList =(List<Booking>) bookingRepo.findAll();
				
				int currentMonth = LocalDate.now().getMonthValue();
				for (int i = 0; i < bookingList.size(); i++) {
					Booking booking = bookingList.get(i);
					
					LocalDate BookingChin = LocalDate.parse(booking.getCheckin());
					int BookingChinMonth = BookingChin.getMonthValue();
						
					if(BookingChinMonth == currentMonth) {
						totalPrice+= booking.getTotalPrice();
					}
					if(!booking.isProcessed()) {
						count++;
					}
				}
				model.addAttribute("totalPrice", totalPrice);
				model.addAttribute("count", count);
				return "/manager/managerHomepage";
			}else {
				return "homepage";
			}
		}else {
			return "login";
		}
	}

	@GetMapping("/login")
	public String login(HttpSession session) {
		return "login";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		Account account = (Account) session.getAttribute("currentAccount");
		log.info("Log out: " + account);
		return "login";
	}

}
