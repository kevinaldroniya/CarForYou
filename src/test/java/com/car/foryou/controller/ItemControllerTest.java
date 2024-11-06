//package com.car.foryou.controller;
//
//import com.car.foryou.dto.item.ItemResponse;
//import com.car.foryou.dto.user.UserInfoDetails;
//import com.car.foryou.model.Brand;
//import com.car.foryou.model.CarModel;
//import com.car.foryou.model.Variant;
//import com.car.foryou.repository.brand.BrandRepository;
//import com.car.foryou.repository.model.ModelRepository;
//import com.car.foryou.repository.variant.VariantRepository;
//import com.car.foryou.service.auth.JwtService;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.RepeatedTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class ItemControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private BrandRepository brandRepository;
//
//    @Autowired
//    private ModelRepository modelRepository;
//
//    @Autowired
//    private VariantRepository variantRepository;
//
//    @RepeatedTest(10)
//    void testCreateItem_shouldReturnCreatedItem() throws Exception {
//        List<Brand> brands = brandRepository.findAll();
//        Random random = new Random();
//        Brand brand = brands.get(random.nextInt(brands.size()));
//        List<CarModel> carModels = modelRepository.findAllByBrand(brand);
//        if (carModels.isEmpty()) {
//            return;
//        }
//        CarModel carModel = carModels.size() > 1 ? carModels.get(random.nextInt(carModels.size())) : carModels.get(0);
//        List<Variant> variants = variantRepository.findAllByCarModel(carModel);
//        if (variants.isEmpty()) {
//            return;
//        }
//        Variant variant = variants.size() > 1 ? variants.get(random.nextInt(variants.size())) : variants.get(0);
//        char randomChar = (char) (random.nextInt(1,26) + 'a');
//        List<String> fuelTypes = objectMapper.readValue(variant.getFuel(), new TypeReference<List<String>>() {
//        });
//        List<String> engine = objectMapper.readValue(variant.getEngine(), new TypeReference<List<String>>() {
//        });
//        List<String> transmissions = objectMapper.readValue(variant.getTransmission(), new TypeReference<List<String>>() {
//        });
//        List<String> color = List.of("Red", "Blue", "Green", "Yellow", "Black", "White", "Silver", "Grey", "Brown", "Orange", "Purple", "Pink");
//        List<String> grades = List.of("A", "B", "C", "D");
//
//        Map<String, Object> request = new HashMap<>();
//        request.put("title", "Test Item " + brand.getName() + " " + carModel.getName() + " " + variant.getName() + " " + variant.getYear() + " " + randomChar);
//        request.put("licensePlate","B"+random.nextInt(1,1000)+String.valueOf(randomChar).toUpperCase());
//        request.put("brand", brand.getName());
//        request.put("model", carModel.getName());
//        request.put("variant", variant.getName());
//        request.put("year", variant.getYear());
//        request.put("fuelType", fuelTypes.size() > 1 ? fuelTypes.get(random.nextInt(fuelTypes.size())) : fuelTypes.get(0));
//        request.put("engineCapacity", engine.size() > 1 ? engine.get(random.nextInt(engine.size())) : engine.get(0));
//        request.put("transmission", transmissions.size() > 1 ? transmissions.get(random.nextInt(transmissions.size())) : transmissions.get(0));
//        request.put("mileage", random.nextInt(1, 100000));
//        request.put("startingPrice", random.nextInt(9_999_999, 999_999_999));
//        request.put("color", color.get(random.nextInt(0, color.size())));
//        request.put("interiorItemGrade", grades.get(random.nextInt(0, grades.size())));
//        request.put("exteriorItemGrade", grades.get(random.nextInt(0, grades.size())));
//        request.put("chassisItemGrade", grades.get(random.nextInt(0, grades.size())));
//        request.put("engineItemGrade", grades.get(random.nextInt(0, grades.size())));
//
//
//        Set<GrantedAuthority> authorities = new HashSet<>();
//        authorities.add(new SimpleGrantedAuthority("INSPECTOR"));
//
//        UserInfoDetails userInfoDetails = UserInfoDetails.builder()
//                .username("auctioneer1")
//                .password("test")
//                .authorities(authorities)
//                .build();
//
//        String jwtToken = jwtService.generateToken(userInfoDetails, true);
//        mockMvc.perform(post("/api/v1/items")
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andDo(result -> {
//                    ItemResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
//                    });
//                    assertNotNull(response);
//                });
//    }
//
//}