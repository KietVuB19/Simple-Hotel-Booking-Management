package practice.controller;

import org.springframework.stereotype.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.ui.Model;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

import practice.model.Booking;
import practice.model.Room;
import practice.repository.BookingRepository;
import practice.repository.RoomRepository;

@Controller
@RequestMapping("/manage/booking")
public class ManageBookingController {
	@Autowired
	private BookingRepository bookingRepo;

	@Autowired
	private RoomRepository roomRepo;
	
	@GetMapping("/{id}")
	private String viewBookingList(@PathVariable("id") Long id, Model model) {
		Room room = roomRepo.findById(id).orElse(null);
		List<Booking> bookings = bookingRepo.findAllByRoom(room);
		model.addAttribute("bookings", bookings);
		model.addAttribute("room", room);
		return "manager/booking/manageBookingList";
	}
	
	@GetMapping("/cancel/{id}")
	public String cancelBooking(@PathVariable("id") Long id) {
		Booking booking = bookingRepo.findById(id).orElse(null);
		Room room = roomRepo.findById(booking.getRoom().getId()).orElse(null);
		if (booking != null) {
			booking.setId(id);
			booking.setCancelled(true);
			booking.setProcessed(true);
		}
		bookingRepo.save(booking);
		Long maPhong = room.getId();
		String link = "redirect:/manage/booking/" + maPhong.toString();
		return link;
	}
	
	@GetMapping("/accept/{id}")
	public String acceptBooking(@PathVariable("id") Long id) {
		Booking booking = bookingRepo.findById(id).orElse(null);
		Room room = roomRepo.findById(booking.getRoom().getId()).orElse(null);
		if (booking != null) {
			booking.setProcessed(true);
			booking.setPaid(true);
			booking.setReceive(true);
//			room.setAvailable(false);
			booking.setRoom(room);
		}
		bookingRepo.save(booking);
		Long maPhong = room.getId();
		String link = "redirect:/manage/booking/" + maPhong.toString();
		return link;
	}
	
}
