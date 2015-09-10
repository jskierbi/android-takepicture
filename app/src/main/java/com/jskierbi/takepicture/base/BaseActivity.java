package com.jskierbi.takepicture.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.jskierbi.takepicture.d2.ActivityComponent;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created on 08/12/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {

  private ActivityComponent activityComponent;

  private Subscription dialogSubscription = Subscriptions.empty();

  public ActivityComponent component() {
    createComponent();
    return activityComponent;
  }

  protected abstract void inject(ActivityComponent component);

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    createComponent();
    super.onCreate(savedInstanceState);
    component().inject(this);
  }

  @Override
  protected void onDestroy() {
    dialogSubscription.unsubscribe();
    super.onDestroy();
  }

  @Override
  protected void onStart() {
    super.onStart();
    EventBus.register(this);
  }

  @Override
  protected void onStop() {
    EventBus.unregister(this);
    super.onStop();
  }

  private void createComponent() {
    if (activityComponent == null) {
      activityComponent = Application.get(this).activityComponent(this);
    }
  }
}
