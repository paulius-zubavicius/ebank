package hw.ebank.configs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = { "hw.ebank" })
class AppConfig implements WebMvcConfigurer {
	private static final String CHARACTER_ENCODING = "UTF-8";

	@Bean(name = "messageSource")
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:messages", "classpath:ValidationMessages");
		messageSource.setDefaultEncoding(CHARACTER_ENCODING);
		return messageSource;
	}

}
