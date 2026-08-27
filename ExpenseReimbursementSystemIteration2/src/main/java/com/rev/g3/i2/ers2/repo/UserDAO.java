package com.rev.g3.i2.ers2.repo;

import com.rev.g3.i2.ers2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    // Persisting is done with the inherited save(); a "register" method here would make
    // Spring Data try (and fail) to derive a query from the method name at startup.
}
