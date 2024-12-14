package classwork.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CartDTO {
    private int cartId;
    private String cartName;
    private List<ItemDTO> items;
}
