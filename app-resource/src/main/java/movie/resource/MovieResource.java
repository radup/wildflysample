package movie.resource;

import static common.model.StandardsOperationResults.getOperationResultExistent;
import static common.model.StandardsOperationResults.getOperationResultInvalidField;
import static common.model.StandardsOperationResults.getOperationResultNotFound;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import common.FieldNotValidException;
import common.json.JsonUtils;
import common.json.JsonWriter;
import common.json.OperationResultJsonWriter;
import common.model.HttpCode;
import common.model.OperationResult;
import common.model.ResourceMessage;
import exception.MovieExistentException;
import exception.MovieNotFoundException;
import model.Movie;
import service.MovieService;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("movie");

	@Inject
	MovieService movieServices;

	@Inject
	MovieJsonConverter movieJsonConverter;

	@POST
	public Response add(final String body) {
		logger.debug("Adding a new Movie with body {}", body);
		Movie movie = movieJsonConverter.convertFrom(body);

		HttpCode httpCode = HttpCode.CREATED;
		OperationResult result;
		try {
			movie = movieServices.add(movie);
			result = OperationResult.success(JsonUtils.getJsonElementWithId(movie.getId()));
		} catch (final FieldNotValidException e) {
			logger.error("One of the fields of the Movie is not valid", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final MovieExistentException e) {
			logger.error("There's already a Movie for the given name", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
		}

		logger.debug("Returning the operation result after adding movie: {}", result);
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") final Long id, final String body) {
		logger.debug("Updating the Movie {} with body {}", id, body);
		final Movie movie = movieJsonConverter.convertFrom(body);
		movie.setId(id);

		HttpCode httpCode = HttpCode.OK;
		OperationResult result;
		try {
			movieServices.update(movie);
			result = OperationResult.success();
		} catch (final FieldNotValidException e) {
			logger.error("One of the field of the Movie is not valid", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final MovieExistentException e) {
			logger.error("There is already a Movie for the given name", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
		} catch (final MovieNotFoundException e) {
			logger.error("No Movie found for the given id", e);
			httpCode = HttpCode.NOT_FOUND;
			result = getOperationResultNotFound(RESOURCE_MESSAGE);
		}

		logger.debug("Returning the operation result after updating movie: {}", result);
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
	}

	@GET
	@Path("/{id}")
	public Response findById(@PathParam("id") final Long id) {
		logger.debug("Find movie: {}", id);
		ResponseBuilder responseBuilder;
		try {
			final Movie movie = movieServices.findById(id);
			final OperationResult result = OperationResult.success(movieJsonConverter.convertToJsonElement(movie));
			responseBuilder = Response.status(HttpCode.OK.getCode()).entity(OperationResultJsonWriter.toJson(result));
			logger.debug("movie found: {}", movie);
		} catch (final MovieNotFoundException e) {
			logger.error("No movie found for id", id);
			responseBuilder = Response.status(HttpCode.NOT_FOUND.getCode());
		}

		return responseBuilder.build();
	}

	@GET
	public Response findAll() {
		logger.debug("Find all movies");

		final List<Movie> movies = movieServices.findAll();

		logger.debug("Found {} movies", movies.size());

		final JsonElement jsonWithPagingAndEntries = getJsonElementWithPagingAndEntries(movies);

		return Response.status(HttpCode.OK.getCode()).entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
				.build();
	}

	private JsonElement getJsonElementWithPagingAndEntries(final List<Movie> movies) {
		final JsonObject jsonWithEntriesAndPaging = new JsonObject();

		final JsonObject jsonPaging = new JsonObject();
		jsonPaging.addProperty("totalRecords", movies.size());

		jsonWithEntriesAndPaging.add("paging", jsonPaging);
		jsonWithEntriesAndPaging.add("entries", movieJsonConverter.convertToJsonElement(movies));

		return jsonWithEntriesAndPaging;
	}
}
