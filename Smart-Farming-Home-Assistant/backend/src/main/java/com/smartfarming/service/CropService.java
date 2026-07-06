package com.smartfarming.service;

import com.smartfarming.entity.Crop;
import com.smartfarming.repository.CropRepository;
import com.smartfarming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CropService {

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Crop> getCropList() {
        Long userId = getCurrentUserId();
        return cropRepository.findByUserId(userId);
    }

    public Crop createCrop(Crop crop) {
        Long userId = getCurrentUserId();
        crop.setUserId(userId);
        return cropRepository.save(crop);
    }

    public Crop updateCrop(Long id, Crop updatedCrop) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("作物不存在"));
        crop.setName(updatedCrop.getName());
        crop.setCategory(updatedCrop.getCategory());
        crop.setInfo(updatedCrop.getInfo());
        crop.setRemark(updatedCrop.getRemark());
        return cropRepository.save(crop);
    }

    public void deleteCrop(Long id) {
        cropRepository.deleteById(id);
    }

    private Long getCurrentUserId() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}
