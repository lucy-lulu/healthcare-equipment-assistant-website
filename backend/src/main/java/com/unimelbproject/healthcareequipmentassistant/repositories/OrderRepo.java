package com.unimelbproject.healthcareequipmentassistant.repositories;

import com.unimelbproject.healthcareequipmentassistant.models.Order;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {

    /**
     * Finds all orders placed by a specific user.
     * @param userId The ID of the user.
     * @return A list of orders belonging to the user.
     */
    List<Order> findByUserId(String userId);

    /**
     * Finds all orders placed by business partners with a specific username.
     * @param username The username of the business partner.
     * @return A list of orders placed by business partners with the given username.
     */
    @Query("SELECT o FROM Order o JOIN User u ON o.userId = u.id " +
           "WHERE u.username = :username AND u.role = :role")
    List<Order> findOrdersByPartnerUsername(@Param("username") String username, 
                                          @Param("role") User.UserRole role);
}