package classwork.json_service;

import classwork.dto.CartDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonService {
    public List<CartDTO> generateCartData() {
        List<CartDTO> carts = new ArrayList<>();
        var filePath = "src/main/resources/cartJson.json";
        ObjectMapper mapper = new ObjectMapper();
        try {
            carts = mapper.readValue(new File(filePath), new TypeReference<>() {
            });
            return carts;
        } catch (IOException e) {
            System.out.println("Error");
        }
        return  null;
    }
}

