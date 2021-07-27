package hw.ebank.mvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import hw.ebank.controllers.MoneyOperationController;
import hw.ebank.dao.ClientDAO;
import hw.ebank.dao.OperationDAO;
import hw.ebank.dao.SessionDAO;
import hw.ebank.services.AccountService;
import hw.ebank.services.MoneyOperationService;


//@WebMvcTest(controllers = {MoneyOperationController.class})
public class MoneyOprationMVCTest {

	private static final String VALID_TOKEN = "valid_token";
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MoneyOperationService service;
	
	
	@MockBean
	private AccountService accountService;
	
	@MockBean
	private ClientDAO cl;
	
	@MockBean
	private OperationDAO op;
	
	
	@MockBean
	private SessionDAO ss;
	
//	@Autowired
//	private ObjectMapper objectMapper;

//	@MockBean
//	private TestEntityManager em;

//	@BeforeEach
//	private void init() {
//		//when(service.deposit(Mockito.eq(VALID_TOKEN), Mockito.any())).thenReturn(new Balance(new BigDecimal(10)));
//	}

//	@Test
	public void deposit() throws Exception {

		Mockito.verify(service, Mockito.times(1)).deposit(VALID_TOKEN, new BigDecimal(10));

		mockMvc.perform(post("/deposit").header("token", VALID_TOKEN).content("{\"amount\": 10}")).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.balance").value("10"));

	}

}
