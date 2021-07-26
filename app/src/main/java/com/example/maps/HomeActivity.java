package com.example.maps;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.example.maps.ui.dashboard.DashboardFragment;
import com.example.maps.ui.dashboard.DashboardViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_map)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_map) {
                    Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.navigation_home) {
                    navController.navigate(R.id.navigation_home);
                    return true;
                }
                if (item.getItemId() == R.id.navigation_dashboard) {
                    navController.navigate(R.id.navigation_dashboard);
                    return true;
                }
                if (item.getItemId() == R.id.navigation_notifications) {
                    navController.navigate(R.id.navigation_notifications);
                    return true;
                }
//                if (item.getItemId() == R.id.navigation_map) {
//                    navController.navigate(R.id.navigation_map);
//                    return true;
//                }
                return false;
            }
        });
    }

}