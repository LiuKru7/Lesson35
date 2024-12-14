package classwork.repository;

import classwork.dto.CartDTO;
import classwork.dto.ItemDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CartRepository {

    public void createCartTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS cart(
                cart_id SERIAL PRIMARY KEY,
                cart_name VARCHAR(255)
                )
                """;
        try (Connection connection = DatabaseRepository.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Table CART was created successfully");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createItemTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS item(
                item_id SERIAL PRIMARY KEY,
                item_name VARCHAR(255),
                price DECIMAL (10,2),
                quantity INT,
                cart_id INT,
                FOREIGN KEY (cart_id) REFERENCES cart(cart_id)
                );
                """;
        try (Connection connection = DatabaseRepository.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Table ITEM was created successfully");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropCartTable() {
        String sql = "DROP TABLE IF EXISTS cart;";
        try (Connection connection = DatabaseRepository.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Table Cart was deleted");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropItemTable() {
        String sql = "DROP TABLE IF EXISTS item;";
        try (Connection connection = DatabaseRepository.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Table item was deleted");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertCarts(List<CartDTO> carts) {
        String cartSql = "INSERT INTO cart (cart_name) VALUES (?) RETURNING cart_id;";
        String itemSql = "INSERT INTO item (item_name, price, quantity, cart_id) VALUES (?,?,?,?)";

        try (Connection connection = DatabaseRepository.getConnection()) {
            PreparedStatement cartPs = connection.prepareStatement(cartSql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement itemPs = connection.prepareStatement(itemSql);
            for (CartDTO cart : carts) {
                cartPs.setString(1, cart.getCartName());
                cartPs.executeUpdate();
                try (ResultSet rs = cartPs.getGeneratedKeys()) {
                    if (rs.next()) {
                        int cartId = rs.getInt(1);
                        for (ItemDTO item : cart.getItems()) {
                            itemPs.setString(1, item.getItemName());
                            itemPs.setDouble(2, item.getPrice());
                            itemPs.setInt(3, item.getQuantity());
                            itemPs.setInt(4, cartId);
                            // itemPs.addBatch();
                            itemPs.executeUpdate();
                        }
                        // itemPs.executeBatch();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CartDTO getCart(int cart_id) {
        String cartSql = "SELECT * FROM cart WHERE cart_id = ?";
        String itemSql = "SELECT * FROM item WHERE cart_id = ?";

        try (Connection connection = DatabaseRepository.getConnection()) {
            PreparedStatement cartPs = connection.prepareStatement(cartSql);
            cartPs.setInt(1, cart_id);
            ResultSet cartRs = cartPs.executeQuery();

            if (cartRs.next()) {
                CartDTO cartDTO = new CartDTO();
                cartDTO.setCartName(cartRs.getString("cart_name"));
                cartDTO.setCartId(cartRs.getInt("cart_id"));

                PreparedStatement itemPs = connection.prepareStatement(itemSql);
                itemPs.setInt(1, cart_id);
                ResultSet itemRs = itemPs.executeQuery();

                List<ItemDTO> items = new ArrayList<>();

                while (itemRs.next()) {
                    ItemDTO item = new ItemDTO();
                    item.setItemId(itemRs.getInt("item_id"));
                    item.setItemName(itemRs.getString("item_name"));
                    item.setPrice(itemRs.getDouble("price"));
                    item.setQuantity(itemRs.getInt("quantity"));

                    items.add(item);
                }

                cartDTO.setItems(items);

                return cartDTO;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<CartDTO> getAllCarts() {
        List<CartDTO> cartList = new ArrayList<>();
        String cartSql = "SELECT * FROM cart";
        String itemSql = "SELECT * FROM item WHERE cart_id = ?";

        try (Connection connection = DatabaseRepository.getConnection()) {
            Statement cartStatement = connection.createStatement();
            ResultSet cartRs = cartStatement.executeQuery(cartSql);
            while (cartRs.next()) {
                CartDTO cartDTO = new CartDTO();
                cartDTO.setCartName(cartRs.getString("cart_name"));
                cartDTO.setCartId(cartRs.getInt("cart_id"));
                cartList.add(cartDTO);
            }
            PreparedStatement itemPs = connection.prepareStatement(itemSql);
            for (CartDTO cart : cartList) {
                itemPs.setInt(1, cart.getCartId());
                ResultSet itemRs = itemPs.executeQuery();

                List<ItemDTO> items = new ArrayList<>();
                while (itemRs.next()) {
                    ItemDTO item = new ItemDTO();
                    item.setItemId(itemRs.getInt("item_id"));
                    item.setItemName(itemRs.getString("item_name"));
                    item.setPrice(itemRs.getDouble("price"));
                    item.setQuantity(itemRs.getInt("quantity"));
                    items.add(item);
                }
                cart.setItems(items);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cartList; // This is now placed after the loop.
    }


}
