package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.dto.EnquiryCreateRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.EnquiryReplyRequest;
import com.unimelbproject.healthcareequipmentassistant.models.Enquiry;
import com.unimelbproject.healthcareequipmentassistant.repositories.EnquiryRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EnquiryService {
    private final EnquiryRepo repo;

    public EnquiryService(EnquiryRepo repo) { this.repo = repo; }

    public Page<Enquiry> listAll(int cursor, int size) {
        return repo.findAllByOrderByTimestampDesc(PageRequest.of(cursor, size));
    }

    public Page<Enquiry> listMine(String askerId, int cursor, int size) {
        return repo.findByAskerIdOrderByTimestampDesc(askerId, PageRequest.of(cursor, size));
    }

    public Enquiry create(String askerId, EnquiryCreateRequest req) {
        Enquiry e = new Enquiry();
        e.setAskerId(askerId);
        e.setQuestion(req.getQuestion());
        e.setStatus("pending");
        return repo.save(e);
    }

    public Optional<Enquiry> reply(Long id, String responderId, EnquiryReplyRequest req) {
        return repo.findById(id).map(e -> {
            e.setResponderId(responderId);
            e.setAnswer(req.getAnswer());
            e.setStatus("answered");
            return repo.save(e);
        });
    }

    /**
     * Gets an enquiry by its ID.
     * @param id The enquiry ID.
     * @return The enquiry if found, empty otherwise.
     */
    public Optional<Enquiry> getEnquiryById(Long id) {
        return repo.findById(id);
    }
}
