package classwork.service;

import classwork.dto.CartDTO;
import classwork.dto.ItemDTO;

import java.util.*;
import java.util.stream.Collectors;

public class CartService {
    public List<ItemDTO> filterItemsByPrice(List<CartDTO> carts, double priceThreshold) {
        return carts.stream()
                .filter(Objects::nonNull)
                .flatMap(cartDTO -> cartDTO.getItems().stream())
                .filter(itemDTO -> itemDTO.getPrice() > priceThreshold)
                .toList();
    }
    public List<String> getAllItemsNames (List<CartDTO> carts) {
        return carts.stream()
                .filter(Objects::nonNull)
                .flatMap(cartDTO -> cartDTO.getItems().stream())
                .map(ItemDTO::getItemName)
                .toList();
    }
    public double calculateCartTotalCost (CartDTO cart) {
        return cart.getItems().stream()
                .filter(Objects::nonNull)
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
    public Optional<ItemDTO> findMostExpensiveItem(List<CartDTO> carts) {
        return carts.stream()
                .filter(Objects::nonNull)
                .flatMap(cartDTO -> cartDTO.getItems().stream())
                .max(Comparator.comparingDouble(ItemDTO::getPrice));
    }

    public Map<Integer,Long> countItemsPerCart(List<CartDTO> carts) {
        return carts.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        CartDTO::getCartId,
                        cart -> (long) cart.getItems().size()
                ));
    }

    public List<String> getDistinctItemNames (List<CartDTO> carts) {
        return carts.stream()
                .filter(Objects::nonNull)
                .flatMap(cartDTO -> cartDTO.getItems().stream())
                .map(ItemDTO::getItemName)
                .distinct()
                .toList();
    }
}
