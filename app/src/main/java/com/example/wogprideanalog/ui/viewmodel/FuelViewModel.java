package com.example.wogprideanalog.ui.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.wogprideanalog.data.DatabaseHelper;
import com.example.wogprideanalog.data.model.User;

import static android.content.Context.MODE_PRIVATE;

public class FuelViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> balance = new MutableLiveData<>();
    private final MutableLiveData<Integer> fuelAmount = new MutableLiveData<>();
    private final MutableLiveData<Integer> coffeeAmount = new MutableLiveData<>();

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    public FuelViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DatabaseHelper(application);
        sharedPreferences = application.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        loadUserData();
    }

    private void loadUserData() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            balance.setValue(500);
            fuelAmount.setValue(25);
            coffeeAmount.setValue(3);
        } else {
            String email = sharedPreferences.getString("userEmail", "");
            String password = sharedPreferences.getString("userPassword", "");
            User user = dbHelper.getUser(email, password);
            if (user != null) {
                balance.setValue(user.accountBalance);
                fuelAmount.setValue(user.fuelAmount);
                coffeeAmount.setValue(user.coffeeCups);
            } else {
                balance.setValue(0);
                fuelAmount.setValue(0);
                coffeeAmount.setValue(0);
            }
        }
    }

    public void refreshData() {
        loadUserData();
    }

    public LiveData<Integer> getBalance() {
        return balance;
    }

    public LiveData<Integer> getFuelAmount() {
        return fuelAmount;
    }

    public LiveData<Integer> getCoffeeAmount() {
        return coffeeAmount;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}