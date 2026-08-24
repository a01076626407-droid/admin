package com.example.demo.controller;

import com.example.demo.repository.AirRaidShelterRepository;
import com.example.demo.service.AirRaidShelterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AirRaidShelterController.class)
class AirRaidShelterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AirRaidShelterService airRaidShelterService;

    @MockBean
    private AirRaidShelterRepository airRaidShelterRepository;

    @Test
    void testShelterPage() throws Exception {
        mockMvc.perform(get("/shelter"))
                .andExpect(status().isOk())
                .andExpect(view().name("shelter"));
    }
}