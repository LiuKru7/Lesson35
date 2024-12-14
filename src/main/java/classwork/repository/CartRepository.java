package classwork.repository;

import classwork.dto.CartDTO;
import classwork.dto.ItemDTO;

import java.sql.*;
import java.util.ArrayList;
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
                            itemPs.addBatch();
                        }
                         itemPs.executeBatch();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

public CartDTO getCart(int cart_id) {
    String sql = """
        SELECT c.cart_id, c.cart_name,
               i.item_id, i.item_name, i.price, i.quantity
        FROM cart c
        LEFT JOIN item i ON c.cart_id = i.cart_id
        WHERE c.cart_id = ?
        ORDER BY i.item_id
    """;

    CartDTO cart = null;

    try (Connection connection = DatabaseRepository.getConnection()) {
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, cart_id);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            if (cart == null) {
                cart = new CartDTO();
                cart.setCartId(rs.getInt("cart_id"));
                cart.setCartName(rs.getString("cart_name"));
                cart.setItems(new ArrayList<>());
            }

            int itemId = rs.getInt("item_id");
            if (!rs.wasNull()) {
                ItemDTO item = new ItemDTO();
                item.setItemId(itemId);
                item.setItemName(rs.getString("item_name"));
                item.setPrice(rs.getDouble("price"));
                item.setQuantity(rs.getInt("quantity"));
                cart.getItems().add(item);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    return cart;
}

    public List<CartDTO> getAllCarts() {
        String sql = """
                SELECT c.cart_id, c.cart_name,
                       i.item_id, i.item_name, i.price, i.quantity
                FROM cart c
                LEFT JOIN item i ON c.cart_id = i.cart_id
                ORDER BY c.cart_id
                """;
        List<CartDTO> carts = new ArrayList<>();
        try (Connection connection = DatabaseRepository.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            CartDTO currentCart = null;
            while (rs.next()) {
                int cartId = rs.getInt("cart_id");
                if (currentCart == null || currentCart.getCartId() != cartId) {
                    currentCart = new CartDTO();
                    currentCart.setCartId(cartId);
                    currentCart.setCartName(rs.getString("cart_name"));
                    currentCart.setItems(new ArrayList<>());
                    carts.add(currentCart);
                }
                int itemId = rs.getInt("item_id");
                if (!rs.wasNull()) {
                    ItemDTO item = new ItemDTO();
                    item.setItemId(itemId);
                    item.setItemName(rs.getString("item_name"));
                    item.setPrice(rs.getDouble("price"));
                    item.setQuantity(rs.getInt("quantity"));
                    currentCart.getItems().add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return carts;
    }
}
