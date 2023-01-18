package com.example.springbackend.service;

import com.example.springbackend.dto.update.DriverUpdateDTO;
import com.example.springbackend.model.PreupdateData;
import com.example.springbackend.repository.PreupdateDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreupdateService {
    @Autowired
    private PreupdateDataRepository preupdateDataRepository;
    public List<PreupdateData> getAll() {
        return preupdateDataRepository.findAll();
    }

    public boolean saveUpdateRequest(DriverUpdateDTO driverUpdateDTO) {
        try {
            PreupdateData pd = new PreupdateData();
            pd.setName(driverUpdateDTO.getName());
            pd.setCity(driverUpdateDTO.getCity());
            pd.setSurname(driverUpdateDTO.getSurname());
            pd.setUsername(driverUpdateDTO.getUsername());
            pd.setPhoneNumber(driverUpdateDTO.getPhoneNumber());
            pd.setProfilePicture(driverUpdateDTO.getProfilePicture());
            preupdateDataRepository.save(pd);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
