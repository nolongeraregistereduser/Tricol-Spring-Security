package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.Request.DeliveryNoteRequestDTO;
import com.tricol.springboottricolapi.dto.Response.DeliveryNoteResponseDTO;

import java.util.List;

public interface IDeliveryNoteService {
    DeliveryNoteResponseDTO createDeliveryNote(DeliveryNoteRequestDTO requestDTO);
    DeliveryNoteResponseDTO updateDeliveryNote(Long id, DeliveryNoteRequestDTO requestDTO);
    void deleteDeliveryNote(Long id);
    List<DeliveryNoteResponseDTO> getAllDeliveryNotes();
    DeliveryNoteResponseDTO getDeliveryNoteById(Long id);
    List<DeliveryNoteResponseDTO> getDeliveryNotesByWorkshop(String workshop);
    DeliveryNoteResponseDTO validateDeliveryNote(Long id);
    DeliveryNoteResponseDTO cancelDeliveryNote(Long id);
}
