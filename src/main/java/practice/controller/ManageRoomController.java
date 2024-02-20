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

import practice.model.Account;
import practice.model.Room;
import practice.repository.RoomRepository;

@Controller
@RequestMapping("/manage/room")
@SessionAttributes("alteredRoom")
public class ManageRoomController {
	@Autowired
	private RoomRepository roomRepo;
	
	@ModelAttribute("alteredRoom")
		public Room room() {
			return new Room();
		}
	
	@GetMapping
	public String manageRoomFrm(Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("currentAccount");
		model.addAttribute("account", account);
		List<Room> rooms = new ArrayList<>();
		model.addAttribute("rooms", rooms);
		return "/manager/room/manageRoomList";
	}

	@GetMapping("/list")
	public String viewAccounts(Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("currentAccount");
		model.addAttribute("account", account);
		List<Room> rooms = (List<Room>) roomRepo.findAll();
		model.addAttribute("rooms", rooms);
		return "/manager/room/manageRoomList";
	}
	
	@GetMapping("/add")
	public String addRoom(Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("currentAccount");
		model.addAttribute("account", account);
		model.addAttribute("room", new Room());
		return "/manager/room/manageAddRoom";
	}

	@PostMapping("/add")
	public String saveRoom(@Valid Room room, Errors errors) {
		if (errors.hasErrors()) {
			return "/manager/room/manageAddRoom";
		} else {
			room.setAvailable(true);
			roomRepo.save(room);
			return "redirect:/manage/room";
		}
	}
	
	@GetMapping("/details/{id}")
	public String manageRoomDetails(Model model, HttpSession session, @PathVariable("id") Long id) {
		Account account = (Account) session.getAttribute("currentAccount");
		model.addAttribute("account", account);
		Room room = roomRepo.findById(id).orElse(null);
		model.addAttribute("room", room);
		return "/manager/room/manageRoomDetails";
	}
	
	@GetMapping("/change/{id}")
	public String changeRoomInfo(Model model, HttpSession session, @PathVariable("id") Long id) {
		Account account = (Account) session.getAttribute("currentAccount");
		model.addAttribute("account", account);
		Room room = roomRepo.findById(id).orElse(null);
		if (room != null) {
			Room alteredroom = (Room) session.getAttribute("alteredRoom");
			if (alteredroom == null) {
				alteredroom = new Room();
				session.setAttribute("alteredRoom", alteredroom);
			}
			alteredroom.setId(room.getId());
			alteredroom.setName(room.getName());
			alteredroom.setType(room.getType());
		}
		model.addAttribute("room", room);
		return "/manager/room/changeRoomDetails";
	}

	@PostMapping("/change/{id}")
	public String confirmChange(Room room, @SessionAttribute("alteredRoom") Room alteredroom,
			@PathVariable("id") Long id) {
		alteredroom.setId(room.getId());
		alteredroom.setName(room.getName());
		alteredroom.setType(room.getType());
		alteredroom.setPrice(room.getPrice());
		alteredroom.setBed(room.getBed());
		alteredroom.setDescription(room.getDescription());
		roomRepo.save(alteredroom);
		return "redirect:/manage/room";
	}

	@GetMapping("/deletebyId/{id}")
	public String deleteRoombyId(@PathVariable("id") Long id) {
		Room room = roomRepo.findById(id).orElse(null);
		if (room != null) {
			roomRepo.deleteById(id);
		}
		return "redirect:/manage/room";
	}
	
	@GetMapping("/search")
	public String search(String keyword, Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("currentAccount");
		model.addAttribute("account", account);
		if (!keyword.isBlank()) {
			List<Room> listResult = roomRepo.search(keyword);
			model.addAttribute("listResult", listResult);
			model.addAttribute("keyword", keyword);
		} else {
			return "redirect:/manage/room";
		}
		return "/manager/room/manageRoomSearch";
	}
}
