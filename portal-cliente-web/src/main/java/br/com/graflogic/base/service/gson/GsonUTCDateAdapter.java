package br.com.graflogic.base.service.gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * 
 * @author gmazz
 *
 */
public class GsonUTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

	private final SimpleDateFormat dateTimeFormat;

	private final SimpleDateFormat dateFormat;

	public GsonUTCDateAdapter() {
		dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
	}

	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		String valor = json.getAsString();

		if (StringUtils.isNotEmpty(valor)) {
			if (NumberUtils.isNumber(valor)) {
				return new Date(Long.parseLong(valor));

			} else {
				try {
					return dateTimeFormat.parse(valor);

				} catch (ParseException e) {
					try {
						return dateFormat.parse(valor);

					} catch (ParseException e1) {
						throw new JsonParseException(e);
					}
				}
			}

		} else {
			return null;
		}
	}

	@Override
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(dateTimeFormat.format(src));
	}
}