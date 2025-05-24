package com.example.wogprideanalog.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FuelViewModel extends ViewModel {
    private MutableLiveData<Integer> balance = new MutableLiveData<>();
    private MutableLiveData<Float> fuelAmount = new MutableLiveData<>();
    private MutableLiveData<Integer> coffeeAmount = new MutableLiveData<>();

    public FuelViewModel() {
        // Імітація даних
        balance.setValue(947);
        fuelAmount.setValue(40.0f);
        coffeeAmount.setValue(2);
    }

    public LiveData<Integer> getBalance() {
        return balance;
    }

    public LiveData<Float> getFuelAmount() {
        return fuelAmount;
    }

    public LiveData<Integer> getCoffeeAmount() {
        return coffeeAmount;
    }
}