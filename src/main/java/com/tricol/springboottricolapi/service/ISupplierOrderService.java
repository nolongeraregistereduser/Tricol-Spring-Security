package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.Request.SupplierOrderRequestDTO;
import com.tricol.springboottricolapi.dto.Response.SupplierOrderResponseDTO;
import com.tricol.springboottricolapi.entity.enums.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public interface ISupplierOrderService {
    List<SupplierOrderResponseDTO> getAllOrders();
    SupplierOrderResponseDTO getOrderById(Long id);
    List<SupplierOrderResponseDTO> getOrdersBySupplierId(Long supplierId);
    List<SupplierOrderResponseDTO> getOrdersByStatus(OrderStatus status);
    List<SupplierOrderResponseDTO> getOrdersByDateRange(LocalDate startDate, LocalDate endDate);
    SupplierOrderResponseDTO createOrder(SupplierOrderRequestDTO requestDTO);
    SupplierOrderResponseDTO updateOrder(Long id, SupplierOrderRequestDTO requestDTO);
    void deleteOrder(Long id);
    SupplierOrderResponseDTO cancelOrder(Long id);
    SupplierOrderResponseDTO validateOrder(Long id);
    SupplierOrderResponseDTO receiveOrder(Long id);
}
