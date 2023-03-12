package com.yohbmeetup.yohb.services;


import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
  @Override
  public void onTokenRefresh() {
    super.onTokenRefresh();
  }
}
