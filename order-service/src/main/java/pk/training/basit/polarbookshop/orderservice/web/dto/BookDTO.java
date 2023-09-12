package pk.training.basit.polarbookshop.orderservice.web.dto;

public record BookDTO(
	String isbn,
	String title,
	String author,
	Double price
){}
