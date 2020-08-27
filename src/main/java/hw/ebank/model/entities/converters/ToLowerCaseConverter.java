package hw.ebank.model.entities.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ToLowerCaseConverter implements AttributeConverter<String, String> {

	@Override
	public String convertToDatabaseColumn(String mixedCaseValue) {
		return mixedCaseValue != null ? mixedCaseValue.toLowerCase() : null;
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		return dbData;
	}

}
