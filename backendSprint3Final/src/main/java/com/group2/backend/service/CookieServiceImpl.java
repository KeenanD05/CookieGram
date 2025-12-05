package com.group2.backend.service;

import com.group2.backend.exception.ResourceNotFoundException;
import com.group2.backend.model.Cookie;
import com.group2.backend.payloads.CookieDTO;
import com.group2.backend.repository.CookieRepository;
import com.group2.backend.service.CookieService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CookieServiceImpl implements CookieService {

    @Autowired
    private CookieRepository cookieRepository;
    @Autowired
    private ModelMapper modelMapper;



    @Override
    @Transactional(readOnly = true)
    public List<CookieDTO> getAllCookies() {
        return cookieRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CookieDTO getCookieById(Long id) {
        Cookie cookie = cookieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cookie not found with id: " + id));
        return convertToDTO(cookie);
    }

    @Override
    @Transactional
    public CookieDTO createCookie(CookieDTO cookieDTO) {
        Cookie cookie = convertToEntity(cookieDTO);
        Cookie savedCookie = cookieRepository.save(cookie);
        return convertToDTO(savedCookie);
    }

    @Override
    @Transactional
    public CookieDTO updateCookie(Long id, CookieDTO cookieDTO) {
        Cookie existingCookie = cookieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cookie not found with id: " + id));

        // Update fields
        existingCookie.setName(cookieDTO.getName());
        existingCookie.setType(cookieDTO.getType());
        existingCookie.setColor(cookieDTO.getColor());
        existingCookie.setMessage(cookieDTO.getMessage());
        existingCookie.setIcing(cookieDTO.getIcing());
        existingCookie.setDescription(cookieDTO.getDescription());
        existingCookie.setBasePrice(cookieDTO.getBasePrice());
        existingCookie.setDiscount(cookieDTO.getDiscount());
        existingCookie.setAvailable(cookieDTO.isAvailable());
        existingCookie.setImageUrl(cookieDTO.getImageUrl());
        existingCookie.setCustomizable(cookieDTO.isCustomizable());

        // Clear and update ingredients
        existingCookie.getIngredients().clear();
        if (cookieDTO.getIngredients() != null) {
            existingCookie.getIngredients().addAll(cookieDTO.getIngredients());
        }

        Cookie updatedCookie = cookieRepository.save(existingCookie);
        return convertToDTO(updatedCookie);
    }

    @Override
    @Transactional
    public void deleteCookie(Long id) {
        Cookie cookie = cookieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cookie not found with id: " + id));
        cookieRepository.delete(cookie);
    }

    private CookieDTO convertToDTO(Cookie cookie) {
        return modelMapper.map(cookie, CookieDTO.class);
    }

    private Cookie convertToEntity(CookieDTO cookieDTO) {
        return modelMapper.map(cookieDTO, Cookie.class);
    }
}
