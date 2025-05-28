package com.example.wogprideanalog.ui.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.model.User;

import static android.content.Context.MODE_PRIVATE;

public class FuelViewModel extends ViewModel {
    private final MutableLiveData<Integer> balance = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalFuelAmount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalCoffeeAmount = new MutableLiveData<>(0);
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public LiveData<Integer> getBalance() {
        return balance;
    }

    public LiveData<Integer> getFuelAmount() {
        return totalFuelAmount;
    }

    public LiveData<Integer> getCoffeeAmount() {
        return totalCoffeeAmount;
    }

    public void refreshData() {
        if (context == null) return;
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("userEmail", "");
        String password = sharedPreferences.getString("userPassword", "");
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        User user = dbHelper.getUser(email, password);
        if (user != null) {
            setBalance(user.accountBalance);
            setFuelAmount(user.getTotalFuelLiters());
            setCoffeeAmount(user.getTotalCoffeeCups());
        }
    }

    public void setBalance(int balance) {
        this.balance.setValue(balance);
    }

    public void setFuelAmount(int fuelAmount) {
        this.totalFuelAmount.setValue(fuelAmount);
    }

    public void setCoffeeAmount(int coffeeAmount) {
        this.totalCoffeeAmount.setValue(coffeeAmount);
    }
}