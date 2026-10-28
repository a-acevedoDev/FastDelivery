package com.example.fastdelivery.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fastdelivery.model.Delivery;

import java.util.List;

@Dao
public interface DeliveryDao {

    @Insert
    long insert(Delivery delivery);

    @Update
    void update(Delivery delivery);

    @Delete
    void delete(Delivery delivery);

    @Query("SELECT * FROM delivery ORDER BY id DESC")
    List<Delivery> getAllDeliveries();

    @Query("SELECT * FROM delivery WHERE delivery_status = :status ORDER BY id DESC")
    List<Delivery> getDeliveriesByStatus(String status);

    @Query("SELECT * FROM delivery WHERE id = :id LIMIT 1")
    Delivery getDeliveryById(long id);

    @Query("UPDATE delivery SET delivery_status = :newStatus WHERE id = :id")
    void updateStatus(long id, String newStatus);
}