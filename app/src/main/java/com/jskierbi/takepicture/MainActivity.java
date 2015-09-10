package com.jskierbi.takepicture;

import android.os.Bundle;
import com.jskierbi.takepicture.base.BaseActivity;
import com.jskierbi.takepicture.d2.ActivityComponent;

public class MainActivity extends BaseActivity {

  @Override
  protected void inject(ActivityComponent component) {
    component.inject(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

}
