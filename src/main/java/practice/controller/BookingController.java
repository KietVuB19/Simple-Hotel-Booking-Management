package practice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.SessionAttributes;
import java.util.List;
import java.text.ParseException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
//import java.util.Stack;
import java.util.HashMap;

import practice.model.Booking;
import practice.model.Room;
import practice.model.Client;
import practice.model.Account;

import practice.repository.RoomRepository;
import practice.repository.BookingRepository;
import practice.repository.ClientRepository;

@Controller
@RequestMapping("/booking")
@SessionAttributes("currentRoom")
public class BookingController {

	@Autowired
	private RoomRepository roomRepo;

	@Autowired
	private ClientRepository clientRepo;

	@Autowired
	private BookingRepository bookingRepo;

	@ModelAttribute("currentRoom")
	public Room room() {
		return new Room();
	}

	@GetMapping("/{id}")
	public String bookingForm(Model model, @PathVariable("id") Long id, @ModelAttribute("currentRoom") Room room) {
		Booking booking = new Booking();
		Room room2 = roomRepo.findById(id).orElse(null);
		if (room2 != null) {
			room.setId(room2.getId());
			room.setName(room2.getName());
			room.setPrice(room2.getPrice());
			room.setType(room2.getType());
			room.setDescription(room2.getDescription());
		}
		model.addAttribute("room", room2);
		model.addAttribute("booking", booking);
		return "/user/bookingInfo";
	}

	@PostMapping
	public String createBooking(Model model, Booking currentBooking, HttpSession session) throws ParseException {
		Room room = (Room) session.getAttribute("currentRoom");
		Account account = (Account) session.getAttribute("currentAccount");

		LocalDate chin = LocalDate.parse(currentBooking.getCheckin());
		LocalDate chout = LocalDate.parse(currentBooking.getCheckout());
		System.out.println(chin + " "+ chout);
		
		if (chin.isAfter(chout) || chin.isEqual(chout)) {
			model.addAttribute("message", "Ngày trả phải ít nhất 1 ngày sau ngày thuê ");
			model.addAttribute("room", room);
			return "/user/bookingInfo";
		}
		if (chin.isBefore(LocalDate.now()) || chin.isBefore(LocalDate.now())) {
			model.addAttribute("message", "Không chọn ngày trong quá khứ");
			model.addAttribute("room", room);
			return "/user/bookingInfo";
		}

		List<Booking> existingBookingsOfRoom = bookingRepo.findAllByRoom(room);
		Map<LocalDate, Boolean> unavailableDates = new HashMap<>();

		float count = 0;
		for (int i = 0; i < existingBookingsOfRoom.size(); i++) {
			Booking booking = existingBookingsOfRoom.get(i);
			LocalDate BookingChin = LocalDate.parse(booking.getCheckin());
			LocalDate BookingChout = LocalDate.parse(booking.getCheckout());

			LocalDate currentDate = BookingChin;
			while (!currentDate.isAfter(BookingChout)) {
				unavailableDates.put(currentDate, false);
				currentDate = currentDate.plusDays(1);
			}
			if (chout.isAfter(BookingChin) && chin.isBefore(BookingChout)) {
				count++;
			}
		}
		model.addAttribute("unavailableDates", unavailableDates);

		System.out.println("Unavailable Dates:");
		for (LocalDate date : unavailableDates.keySet()) {
			System.out.println(date);
		}

		if (count > 0) {
			model.addAttribute("message", "Phòng đã được đặt trong khoảng thời gian này");
			model.addAttribute("room", room);
			return "/user/bookingInfo";
		}
		long numberOfDays = ChronoUnit.DAYS.between(chin, chout);
		long totalPrice = (numberOfDays + 1) * room.getPrice();
		Client client = clientRepo.findByUser(account.getUser()).orElse(null);

		model.addAttribute("totalPrice", totalPrice);
		currentBooking.setRoom(room);
		currentBooking.setClient(client);
		currentBooking.setReceive(false);
		currentBooking.setTotalPrice(totalPrice);
		currentBooking.setCancelled(false);
		currentBooking.setPaid(false);
		currentBooking.setProcessed(false);
		bookingRepo.save(currentBooking);
		return "redirect:/room";
	}

	@GetMapping("/list")
	public String viewBookingList(Model model, HttpSession session) {
		Account account = (Account) session.getAttribute("currentAccount");
		Client client = clientRepo.findByUser(account.getUser()).orElse(null);
		List<Booking> bookings = bookingRepo.findAllByClient(client);

		model.addAttribute("bookings", bookings);
		return "/user/bookingList";
	}

	@GetMapping("/cancel/{id}")
	public String cancelBooking(@PathVariable("id") Long id) {
		Booking booking = bookingRepo.findById(id).orElse(null);
		if (booking != null) {
			booking.setCancelled(true);
			bookingRepo.deleteById(id);
		}
		return "redirect:/booking/list";
	}
}
