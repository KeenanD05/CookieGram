package com.group2.backend.service;


import com.group2.backend.payloads.CookieDTO;

import java.util.List;

public interface CookieService {
    List<CookieDTO> getAllCookies();
    CookieDTO getCookieById(Long id);
    CookieDTO createCookie(CookieDTO cookieDTO);
    CookieDTO updateCookie(Long id, CookieDTO cookieDTO);
    void deleteCookie(Long id);
}
