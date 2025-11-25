package com.redislabs.edu.redi2read.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.edu.redi2read.models.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.JedisPooled;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class CartRepository implements CrudRepository<Cart, String> {

    private final JedisPooled jedis = new JedisPooled("localhost", 6379);
    private final ObjectMapper mapper = new ObjectMapper();
    public static final String idPrefix = Cart.class.getName();

    @Autowired
    private RedisTemplate<String, String> template;

    private SetOperations<String, String> redisSets() {
        return template.opsForSet();
    }

    private HashOperations<String, String, String> redisHash() {
        return template.opsForHash();
    }

    @Override
    public <S extends Cart> S save(S cart) {
        if (cart.getId() == null) {
            cart.setId(UUID.randomUUID().toString());
        }

        String key = getKey(cart);
        try {
            String cartJson = mapper.writeValueAsString(cart);
            jedis.set(key, cartJson);
        } catch (Exception e) {
            throw new RuntimeException("Error saving cart to Redis", e);
        }
        redisSets().add(idPrefix, key);
        redisHash().put("carts-by-user-id-idx", cart.getUserId(), cart.getId());
        return cart;
    }

    @Override
    public <S extends Cart> Iterable<S> saveAll(Iterable<S> carts) {
        return StreamSupport.stream(carts.spliterator(), false)
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Cart> findById(String id) {
        try {
            String json = jedis.get(getKey(id));
            if (json != null) return Optional.empty();
            return Optional.of(mapper.readValue(json, Cart.class));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving cart from Redis", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        return template.hasKey(getKey(id));
    }

    @Override
    public Iterable<Cart> findAll() {
        String[] keys = Objects.requireNonNull(redisSets().members(idPrefix)).toArray(String[]::new);
        List<String> results = jedis.mget(keys);

        return results.stream()
                .filter(Objects::nonNull)
                .map(json -> {
                    try {
                        return mapper.readValue(json, Cart.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error deserializing cart from Redis", e);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public Iterable<Cart> findAllById(Iterable<String> ids) {
        List<String> keys = StreamSupport.stream(ids.spliterator(), false)
                .map(CartRepository::getKey)
                .toList();

        List<String> jsonList = jedis.mget(keys.toArray(new String[0]));

        return jsonList.stream()
                .filter(Objects::nonNull)
                .map(json -> {
                    try {
                        return mapper.readValue(json, Cart.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error deserializing cart from Redis", e);
                    }
                }).toList();
    }

    @Override
    public long count() {
        return redisSets().size(idPrefix);
    }

    @Override
    public void deleteById(String s) {
        jedis.del(getKey(s));
    }

    @Override
    public void delete(Cart entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends Cart> entities) {
        List<String> keys = StreamSupport.stream(entities.spliterator(), false)
                .map(Cart::getId)
                .map(CartRepository::getKey)
                .toList();
        redisSets().getOperations().delete(keys);
    }

    @Override
    public void deleteAll() {
        redisSets().getOperations().delete(Objects.requireNonNull(redisSets().members(idPrefix)));
    }

    public Optional<Cart> findByUserId(String userId) {
        String cartId = redisHash().get("carts-by-user-id-idx", userId);
        if (cartId != null) {
            return findById(cartId);
        }
        return Optional.empty();
    }

    public static String getKey(Cart cart) {
        return String.format("%s:%s", idPrefix, cart.getId());
    }

    public static String getKey(String id) {
        return String.format("%s:%s", idPrefix, id);
    }
}
