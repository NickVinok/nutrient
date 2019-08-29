package com.nutrient.nutrientSpring.Repos.User;

import com.nutrient.nutrientSpring.Model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

}