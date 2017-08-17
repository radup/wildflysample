package service;

import java.util.List;

import javax.ejb.Local;

import common.FieldNotValidException;
import exception.MovieExistentException;
import exception.MovieNotFoundException;
import model.Movie;

@Local
public interface MovieService {

	Movie add(Movie movie) throws FieldNotValidException, MovieExistentException;

	void update(Movie movie) throws FieldNotValidException, MovieNotFoundException;

	Movie findById(Long id) throws MovieNotFoundException;

	List<Movie> findAll();
}
