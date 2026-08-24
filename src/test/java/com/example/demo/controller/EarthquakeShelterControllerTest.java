package com.example.demo.controller;

import com.example.demo.repository.EarthquakeShelterRepository;
import com.example.demo.service.EarthquakeShelterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EarthquakeShelterController.class)
class EarthquakeShelterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EarthquakeShelterService earthquakeShelterService;

    @MockBean
    private EarthquakeShelterRepository earthquakeShelterRepository;

    @Test
    void testEarthquake() throws Exception {
        mockMvc.perform(get("/earthquake"))
                .andExpect(status().isOk());
    }
}