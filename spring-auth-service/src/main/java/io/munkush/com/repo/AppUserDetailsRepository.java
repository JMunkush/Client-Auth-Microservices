package io.munkush.com.repo;

import io.munkush.com.entity.AppUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserDetailsRepository extends JpaRepository<AppUserDetails, Long> {
    AppUserDetails findByEmail(String email);
    AppUserDetails findByUsername(String username);
}
