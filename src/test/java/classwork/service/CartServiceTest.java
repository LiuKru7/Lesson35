package classwork.service;

import classwork.dto.CartDTO;
import classwork.dto.ItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {
    private List<CartDTO> testCarts =new ArrayList<>();
    CartService cartService = new CartService();


    @BeforeEach
    public void setUp() {
        List<ItemDTO> itemsCart1 = Arrays.asList(
                new ItemDTO(1, "Laptop", 1000.0, 1),
                new ItemDTO(2, "Smartphone", 700.0, 2)
        );

        List<ItemDTO> itemsCart2 = Arrays.asList(
                new ItemDTO(3, "Toaster", 30.0, 1)
        );

        List<ItemDTO> itemsCart3 = Arrays.asList(
                new ItemDTO(4, "Toaster", 80.0, 1),
                new ItemDTO(5, "T-shirt", 20.0, 3),
                new ItemDTO(6, "Jeans", 40.0, 2)
        );

        CartDTO cart1 = new CartDTO(1, "Electronics Cart", itemsCart1);
        CartDTO cart2 = new CartDTO(2, "Home Appliances Cart", itemsCart2);
        CartDTO cart3 = new CartDTO(3, "Clothing Cart", itemsCart3);

        testCarts = Arrays.asList(cart1, cart2, cart3);
    }

    @Test
    void testFilterItemsByPrice() {
        List<ItemDTO> items = cartService.filterItemsByPrice(testCarts,70);
        assertEquals(3, items.size());
    }

    @Test
    void testGetAllItemsNames() {
        List<String> itemsNames = cartService.getAllItemsNames(testCarts);
        assertEquals(6, itemsNames.size());
    }

    @Test
    void testCalculateCartTotalCost() {
        double totalCost = cartService.calculateCartTotalCost(testCarts.get(0));
        assertEquals(1700.0, totalCost);

    }

    @Test
    void testFindMostExpensiveItem() {
        Optional<ItemDTO> expensiveItem = cartService.findMostExpensiveItem(testCarts);
        assertEquals(1000, expensiveItem.get().getPrice());
    }

    @Test
    void testCountItemsPerCart() {
        Map<Integer, Long> countPerCart = cartService.countItemsPerCart(testCarts);
        assertEquals(2, countPerCart.get(1).doubleValue());
        assertEquals(1, countPerCart.get(2).doubleValue());
        assertEquals(3, countPerCart.get(3).doubleValue());
    }

    @Test
    void testGetDistinctItemNames() {
        List<String> distinctItemsNames = cartService.getDistinctItemNames(testCarts);
        assertEquals(5, distinctItemsNames.size());
    }
}