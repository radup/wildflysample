package movie.resource;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import common.json.JsonReader;
import model.Movie;

@ApplicationScoped
public class MovieJsonConverter {

	public Movie convertFrom(final String json) {
		final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

		final Movie movie = new Movie();
		movie.setName(JsonReader.getStringOrNull(jsonObject, "name"));

		return movie;
	}

	public JsonElement convertToJsonElement(final Movie movie) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", movie.getId());
		jsonObject.addProperty("name", movie.getName());
		return jsonObject;
	}

	public JsonElement convertToJsonElement(final List<Movie> movies) {
		final JsonArray jsonArray = new JsonArray();

		for (final Movie category : movies) {
			jsonArray.add(convertToJsonElement(category));
		}

		return jsonArray;
	}
}
