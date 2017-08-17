package service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import common.FieldNotValidException;
import exception.MovieExistentException;
import exception.MovieNotFoundException;
import model.Movie;
import repository.MovieRepository;
import service.MovieService;

public class MovieServiceImpl implements MovieService {

	@Inject
	Validator validator;

	@Inject
	MovieRepository movieRepository;

	@Override
	public Movie add(Movie movie) throws FieldNotValidException, MovieExistentException {
		final Set<ConstraintViolation<Movie>> errors = validator.validate(movie);
		final Iterator<ConstraintViolation<Movie>> itErrors = errors.iterator();
		if (itErrors.hasNext()) {
			final ConstraintViolation<Movie> violation = itErrors.next();
			throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
		}

		if (movieRepository.alreadyExists(movie)) {
			throw new MovieExistentException();
		}

		return movieRepository.add(movie);
	}

	@Override
	public void update(Movie movie) throws FieldNotValidException, MovieNotFoundException {
		validateCategory(movie);

		if (!movieRepository.existsById(movie.getId())) {
			throw new MovieNotFoundException();
		}

		movieRepository.update(movie);
	}

	@Override
	public Movie findById(Long id) throws MovieNotFoundException {
		final Movie movie = movieRepository.findById(id);
		if (movie == null) {
			throw new MovieNotFoundException();
		}
		return movie;
	}

	@Override
	public List<Movie> findAll() {
		return movieRepository.findAll("name");
	}

	private void validateCategory(final Movie movie) {
		validateCategoryFields(movie);

		if (movieRepository.alreadyExists(movie)) {
			throw new MovieExistentException();
		}
	}

	private void validateCategoryFields(final Movie movie) {
		final Set<ConstraintViolation<Movie>> errors = validator.validate(movie);
		final Iterator<ConstraintViolation<Movie>> itErrors = errors.iterator();
		if (itErrors.hasNext()) {
			final ConstraintViolation<Movie> violation = itErrors.next();
			throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
		}
	}
}
