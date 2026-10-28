package com.example.fastdelivery.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "delivery")
public class Delivery {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "client_name")
    private String clientName;

    private String phono;

    private String address;

    private double latitude;

    private double longitude;

    @ColumnInfo(name = "amount_to_pay")
    private double amountToPay;

    @ColumnInfo(name = "delivery_status")
    private String deliveryStatus = DeliveryStatus.PENDIENTE.name();

    private String observation;

    public Delivery() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPhono() {
        return phono;
    }

    public void setPhono(String phono) {
        this.phono = phono;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(double amountToPay) {
        this.amountToPay = amountToPay;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public DeliveryStatus getDeliveryStatusEnum() {
        return deliveryStatus == null ? null : DeliveryStatus.valueOf(deliveryStatus);
    }

    public void setDeliveryStatusEnum(DeliveryStatus status) {
        this.deliveryStatus = status == null ? null : status.name();
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}