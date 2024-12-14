package classwork;

import classwork.dto.CartDTO;
import classwork.json_service.JsonService;
import classwork.repository.CartRepository;
import classwork.service.CartService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<CartDTO> carts = new ArrayList<>();
        List<CartDTO> cartsFromDb = new ArrayList<>();

        JsonService jsonService = new JsonService();
        carts = jsonService.generateCartData();

        CartService cartService = new CartService();
        CartRepository cartRepository = new CartRepository();

        cartRepository.dropItemTable();
        cartRepository.dropCartTable();
        cartRepository.createCartTable();
        cartRepository.createItemTable();
        cartRepository.insertCarts(carts);
        cartsFromDb = cartRepository.getAllCarts();

        cartService.filterItemsByPrice(cartsFromDb, 100.0).forEach(System.out::println);
        System.out.println("All items names:");
        cartService.getAllItemsNames(cartsFromDb).forEach(System.out::println);

        System.out.println("Cart total cost:  " + cartService.calculateCartTotalCost(cartRepository.getCart(3)));

        System.out.println("Count item per cart: ");
        System.out.println( cartService.countItemsPerCart(cartsFromDb));

        System.out.println("Expensive item: ");
        System.out.println(cartService.findMostExpensiveItem(carts));

        System.out.println("Items names: ");
        System.out.println(cartService.getDistinctItemNames(carts));
    }
}
