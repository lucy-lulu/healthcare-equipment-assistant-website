package com.unimelbproject.healthcareequipmentassistant.repositories;

import com.unimelbproject.healthcareequipmentassistant.models.Enquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnquiryRepo extends JpaRepository<Enquiry, Long> {
    Page<Enquiry> findByAskerIdOrderByTimestampDesc(String askerId, Pageable pageable);
    Page<Enquiry> findAllByOrderByTimestampDesc(Pageable pageable);
}
