package com.redislabs.edu.redi2read.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Cart;
import com.redislabs.edu.redi2read.models.CartItem;
import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CartRepository;
import com.redislabs.edu.redi2read.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.json.Path2;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.LongStream;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private final JedisPooled jedis = new JedisPooled("localhost", 6379);
    private final ObjectMapper mapper = new ObjectMapper();

    Path2 cartItemPath = Path2.of(".cartItems");

    public Cart get(String id) {
        return cartRepository.findById(id).orElse(null);
    }

    public void addCart(String id, CartItem item) {
        Optional<Book> book = bookRepository.findById(item.getIsbn());
        if (book.isPresent()) {
            String cartKey = CartRepository.getKey(id);
            item.setPrice(book.get().getPrice());
            jedis.jsonArrAppend(cartKey, cartItemPath, item);
        }
    }

    public void removeFromCart(String id, String isbn) {
        Optional<Cart> cart = cartRepository.findById(id);
        if (cart.isPresent()) {
            Cart existingCart = cart.get();
            String cartKey = CartRepository.getKey(existingCart.getId());
            List<CartItem> cartItems = new ArrayList<>(existingCart.getCartItems());
            OptionalLong cartItemIndex = LongStream.range(0, cartItems.size()).filter(i -> cartItems.get((int) i).getIsbn().equals(isbn)).findFirst();
            if (cartItemIndex.isPresent()) {
                jedis.jsonArrPop(cartKey, cartItemPath, (int) cartItemIndex.getAsLong());
            }
        }
    }

    public void checkout(String id) {
        Cart cart = cartRepository.findById(id).orElse(null);
        User user = userRepository.findById(Objects.requireNonNull(cart).getUserId()).orElse(null);
        cart.getCartItems().forEach(cartItem -> {
            Book book = bookRepository.findById(cartItem.getIsbn()).orElse(null);
            Objects.requireNonNull(user).addBook(book);
        });
        userRepository.save(Objects.requireNonNull(user));
    }
}
