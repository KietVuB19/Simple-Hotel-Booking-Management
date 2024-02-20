package practice.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import practice.model.Room;
import practice.model.Account;
import practice.repository.RoomRepository;

@Controller
@RequestMapping("/room")
public class RoomController {

	@Autowired
	private RoomRepository roomRepo;

	@GetMapping()
	public String viewList(Model model, HttpSession session) {
		List<Room> rooms = new ArrayList<>();
		model.addAttribute("rooms", rooms);
		return "/user/roomList";
	}

	@GetMapping("/details/{id}")
	public String viewDetails(Model model, @PathVariable("id") Long id) {
		Room room = roomRepo.findById(id).orElse(null);
		model.addAttribute("room", room);
		return "/user/roomDetails";
	}

	@GetMapping("/search")
	public String searchPrice(Long minPrice, Long maxPrice, Long bed, Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("currentAccount");
		model.addAttribute("account", account);
		if (minPrice == null && maxPrice == null) {
			return "redirect:/room";
		} else if (bed == null) {
			if (minPrice == null) {
				minPrice = (long) 0;
			} else if (maxPrice == null) {
				maxPrice = (long) 99999999;
			}
			List<Room> listResult = roomRepo.searchPrice(minPrice, maxPrice);
			model.addAttribute("listResult", listResult);
			model.addAttribute("minPrice", minPrice);
			model.addAttribute("maxPrice", maxPrice);
		} else {
			if (minPrice == null) {
				minPrice = (long) 0;
			} 
			else if (maxPrice == null) {
				maxPrice = (long) 99999999;
			}
			List<Room> listResult = roomRepo.searchAllCond(bed, minPrice, maxPrice);
			model.addAttribute("listResult", listResult);
			model.addAttribute("minPrice", minPrice);
			model.addAttribute("maxPrice", maxPrice);
			model.addAttribute("bed", bed);
		}
		return "/user/roomListSearch";
	}
}
