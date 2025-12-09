package com.unimelbproject.healthcareequipmentassistant.repositories;

import com.unimelbproject.healthcareequipmentassistant.models.OrderProduct;
import com.unimelbproject.healthcareequipmentassistant.models.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderProductRepo extends JpaRepository<OrderProduct, OrderProductId> {

    /**
     * Finds all order products associated with a specific order ID.
     * @param orderId The ID of the order.
     * @return A list of OrderProduct entities for the given order.
     */
    List<OrderProduct> findById_OrderId(Long orderId);
}