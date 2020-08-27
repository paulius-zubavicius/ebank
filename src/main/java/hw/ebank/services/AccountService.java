package hw.ebank.services;

import hw.ebank.model.api.request.Credentials;
import hw.ebank.model.api.response.Token;

public interface AccountService {

	Token signupNew(Credentials singup);

	Token login(Credentials singup);

}
