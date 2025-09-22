package com.Weather.Weather_Forecast_App.repository;

import com.Weather.Weather_Forecast_App.entity.User;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
   Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
