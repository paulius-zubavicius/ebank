package hw.ebank;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import hw.ebank.services.AccountServiceTest;
import hw.ebank.services.MoneyOperationServiceTest;
import hw.ebank.units.validators.PasswordPolicyValidatorTest;

@RunWith(Suite.class)
@SuiteClasses({ AccountServiceTest.class, MoneyOperationServiceTest.class, PasswordPolicyValidatorTest.class })
public class AllTests {

}
