package com.example.springbackend.controller;

import com.example.springbackend.dto.update.DriverUpdateDTO;
import com.example.springbackend.model.PreupdateData;
import com.example.springbackend.service.PreupdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/preupdate")
public class PreupdateController {
    @Autowired
    private PreupdateService preupdateService;

    @PostMapping("/sendUpdateRequest")
    public ResponseEntity<Boolean> sendUpdateRequest(@RequestBody DriverUpdateDTO driverUpdateDTO){
        boolean successfulUpdate = preupdateService.saveUpdateRequest(driverUpdateDTO);
        HttpStatus returnStatus = successfulUpdate ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(successfulUpdate, returnStatus);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PreupdateData>> sendUpdateRequest(){
        List<PreupdateData> ret = preupdateService.getAll();
        HttpStatus returnStatus = ret != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(ret, returnStatus);
    }
}
