package com.example.springbackend.controller;

import com.example.springbackend.config.PayPalService;
import com.example.springbackend.repository.OrderRepository;
import com.example.springbackend.dto.paypal.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.springbackend.dto.paypal.OrderDTO;
import com.example.springbackend.dto.paypal.OrderResponseDTO;
import com.example.springbackend.dto.paypal.PayPalAppContextDTO;
import com.example.springbackend.dto.paypal.PaymentLandingPage;
import com.example.springbackend.model.Order;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/checkout")
public class CheckoutController {

    @Autowired
    private PayPalService payPalService;
    @Autowired
    private OrderRepository orderDAO;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> checkout(@RequestBody OrderDTO orderDTO) throws Exception {
        var appContext = new PayPalAppContextDTO();
        appContext.setReturnUrl("http://localhost:8080/checkout/success");
        appContext.setBrandName("My brand");
        appContext.setLandingPage(PaymentLandingPage.BILLING);
        orderDTO.setApplicationContext(appContext);
        var orderResponse = payPalService.createOrder(orderDTO);

        var entity = new Order();
        entity.setPaypalOrderId(orderResponse.getId());
        entity.setPaypalOrderStatus(orderResponse.getStatus().toString());
        orderDAO.save(entity);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping(value = "/success")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest request){
        var orderId = request.getParameter("token");
        var payerID = request.getParameter("PayerID");
        var out = orderDAO.findByPaypalOrderId(orderId);
        try {
            payPalService.confirmOrder(orderId,payerID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.setPaypalOrderStatus(OrderStatus.APPROVED.toString());
        orderDAO.save(out);
        return ResponseEntity.ok().body("Payment success");
    }

}
