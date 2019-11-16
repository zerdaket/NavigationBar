package com.zerdaket.navigationbar;

import android.os.Bundle;

import com.zerdaket.navigation.NavigationBar;
import com.zerdaket.navigation.NavigationManager;
import com.zerdaket.navigation.NormalItem;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private NavigationBar mNavigationBar;
    private NormalItem mHomeItem;
    private NormalItem mAlbumItem;
    private NormalItem mEmailItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationBar = findViewById(R.id.nb_main);
        mHomeItem = findViewById(R.id.ni_home);
        mAlbumItem = findViewById(R.id.ni_album);
        mEmailItem = findViewById(R.id.ni_email);

        NavigationManager navigationManager =
                new NavigationManager(mNavigationBar, getSupportFragmentManager(), R.id.fl_container);

        navigationManager.bindFragment(R.id.ni_home, HomeFragment.class, HomeFragment.class.getSimpleName());
        navigationManager.bindFragment(R.id.ni_album, AlbumFragment.class, AlbumFragment.class.getSimpleName());
        navigationManager.bindFragment(R.id.ni_email, EmailFragment.class, EmailFragment.class.getSimpleName());

    }

}
