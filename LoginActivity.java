package com.example.friendtracker.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.friendtracker.Fragment.LoginFragment;
import com.example.friendtracker.R;

public class LoginActivity extends AppCompatActivity {
    public TextView currentScreenName;
    FragmentManager manager;
    FragmentTransaction ft;
    Toolbar toolbar;
    private ImageView actionBarBackBtn;
    View.OnClickListener actionBarBackBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);
            findAllIds();
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            pushFragments(new LoginFragment(), false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findAllIds() {
        try {
            toolbar = findViewById(R.id.toolbar);
            actionBarBackBtn = findViewById(R.id.actionBarBackBtn);
            currentScreenName = findViewById(R.id.currentScreenName);
            actionBarBackBtn.setOnClickListener(actionBarBackBtnListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pushFragments(Fragment fragment, boolean shouldAdd, String tag) {
        try {
            manager = getSupportFragmentManager();
            ft = manager.beginTransaction();
            ft.replace(R.id.contentMain, fragment, tag);
         /*   if (manager.getBackStackEntryCount() >= 1) {
              //  actionBarBackBtn.setVisibility(View.VISIBLE);
            }*/
            if (shouldAdd) {
                actionBarBackBtn.setVisibility(View.VISIBLE);
                ft.addToBackStack("FragmentStack").commit();
            } else {
                actionBarBackBtn.setVisibility(View.INVISIBLE);
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ft.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.contentMain);
            if (fragment instanceof LoginFragment) {
                actionBarBackBtn.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
