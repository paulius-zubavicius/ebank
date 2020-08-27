package hw.ebank.model.entites;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Session {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(columnDefinition = "TIMESTAMP", name = "last_touch")
	private LocalDateTime lastTouch;

	@NotNull
	private String token;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "client_id")
	private Client client;

	public Session() {
	}

	public Session(Long id, LocalDateTime lastTouch, String token, Client client) {
		this.id = id;
		this.lastTouch = lastTouch;
		this.token = token;
		this.client = client;
	}

	public Long getId() {
		return id;
	}

	public LocalDateTime getLastTouch() {
		return lastTouch;
	}

	public void setLastTouch(LocalDateTime lastTouch) {
		this.lastTouch = lastTouch;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
