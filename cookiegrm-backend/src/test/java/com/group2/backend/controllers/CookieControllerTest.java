package com.group2.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.backend.payloads.CookieDTO;
import com.group2.backend.service.CookieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CookieControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CookieService cookieService;

    @InjectMocks
    private CookieController cookieController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cookieController)
                .setControllerAdvice(new Exception()) // Add global exception handler if any
                .build();
    }

    @Test
    void getAllCookies_ShouldReturnListOfCookies() throws Exception {
        // Arrange
        CookieDTO cookie1 = new CookieDTO();
        cookie1.setId(1L);
        cookie1.setName("Chocolate Chip");

        CookieDTO cookie2 = new CookieDTO();
        cookie2.setId(2L);
        cookie2.setName("Oatmeal Raisin");

        List<CookieDTO> cookies = Arrays.asList(cookie1, cookie2);
        when(cookieService.getAllCookies()).thenReturn(cookies);

        // Act & Assert
        mockMvc.perform(get("/api/public/cookies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Chocolate Chip"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Oatmeal Raisin"));

        verify(cookieService, times(1)).getAllCookies();
        verifyNoMoreInteractions(cookieService);
    }

    @Test
    void getCookieById_WithValidId_ShouldReturnCookie() throws Exception {
        // Arrange
        CookieDTO cookie = new CookieDTO();
        cookie.setId(1L);
        cookie.setName("Chocolate Chip");

        when(cookieService.getCookieById(1L)).thenReturn(cookie);

        // Act & Assert
        mockMvc.perform(get("/api/public/cookies/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Chocolate Chip"));

        verify(cookieService, times(1)).getCookieById(1L);
        verifyNoMoreInteractions(cookieService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCookie_WithValidData_ShouldReturnCreatedCookie() throws Exception {
        // Arrange
        CookieDTO inputCookie = new CookieDTO();
        inputCookie.setName("New Cookie");

        CookieDTO savedCookie = new CookieDTO();
        savedCookie.setId(1L);
        savedCookie.setName("New Cookie");

        when(cookieService.createCookie(any(CookieDTO.class))).thenReturn(savedCookie);

        // Act & Assert
        mockMvc.perform(post("/api/admin/cookies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCookie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Cookie"));

        verify(cookieService, times(1)).createCookie(any(CookieDTO.class));
        verifyNoMoreInteractions(cookieService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void createCookie_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        // Arrange
        CookieDTO inputCookie = new CookieDTO();
        inputCookie.setName("New Cookie");

        // Act & Assert
        mockMvc.perform(post("/api/admin/cookies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCookie)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(cookieService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCookie_WithValidData_ShouldReturnUpdatedCookie() throws Exception {
        // Arrange
        CookieDTO inputCookie = new CookieDTO();
        inputCookie.setName("Updated Cookie");

        CookieDTO updatedCookie = new CookieDTO();
        updatedCookie.setId(1L);
        updatedCookie.setName("Updated Cookie");

        when(cookieService.updateCookie(eq(1L), any(CookieDTO.class))).thenReturn(updatedCookie);

        // Act & Assert
        mockMvc.perform(put("/api/admin/cookies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCookie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Cookie"));

        verify(cookieService, times(1)).updateCookie(eq(1L), any(CookieDTO.class));
        verifyNoMoreInteractions(cookieService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCookie_WithValidId_ShouldReturnDeletedCookie() throws Exception {
        // Arrange
        CookieDTO cookieToDelete = new CookieDTO();
        cookieToDelete.setId(1L);
        cookieToDelete.setName("Cookie to delete");

        when(cookieService.getCookieById(1L)).thenReturn(cookieToDelete);
        doNothing().when(cookieService).deleteCookie(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/admin/cookies/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cookie to delete"));

        verify(cookieService, times(1)).getCookieById(1L);
        verify(cookieService, times(1)).deleteCookie(1L);
        verifyNoMoreInteractions(cookieService);
    }

    @Test
    void getCookieById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(cookieService.getCookieById(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/public/cookies/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(cookieService, times(1)).getCookieById(999L);
        verifyNoMoreInteractions(cookieService);
    }
}