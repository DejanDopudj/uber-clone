package com.example.springbackend.controller;

import com.example.springbackend.config.PayPalService;
import com.example.springbackend.model.Passenger;
import com.example.springbackend.repository.OrderRepository;
import com.example.springbackend.dto.paypal.OrderStatus;
import com.example.springbackend.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.springbackend.dto.paypal.OrderDTO;
import com.example.springbackend.dto.paypal.OrderResponseDTO;
import com.example.springbackend.dto.paypal.PayPalAppContextDTO;
import com.example.springbackend.model.Order;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "api/checkout")
public class CheckoutController {

    @Autowired
    private PayPalService payPalService;
    @Autowired
    private OrderRepository orderDAO;

    @PostMapping
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<OrderResponseDTO> checkout(@RequestBody OrderDTO orderDTO) throws Exception {
        var appContext = new PayPalAppContextDTO();
        appContext.setReturnUrl("http://localhost:8080/api/checkout/success");
        orderDTO.setApplicationContext(appContext);
        var orderResponse = payPalService.createOrder(orderDTO);
        var order = new Order();
        order.setPaypalOrderId(orderResponse.getId());
        order.setPaypalOrderStatus(orderResponse.getStatus().toString());
        order.setBalance(Integer.parseInt(orderDTO.getPurchaseUnits().get(0).getAmount().getValue()));
        order.setUsername((((Passenger) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()));
        orderDAO.save(order);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping(value = "/success")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest request) throws Exception {
        var orderId = request.getParameter("token");
        var payerID = request.getParameter("PayerID");
        var out = orderDAO.findByPaypalOrderId(orderId);
        payPalService.confirmOrder(orderId,out.getUsername());
        out.setPaypalOrderStatus(OrderStatus.APPROVED.toString());
        orderDAO.save(out);
        return ResponseEntity.ok().body("Payment success");
    }

}
