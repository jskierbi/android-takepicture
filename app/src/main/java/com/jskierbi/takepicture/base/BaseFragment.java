package com.jskierbi.takepicture.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.jskierbi.takepicture.d2.ActivityComponent;

/**
 * Created on 08/12/2015.
 */
public abstract class BaseFragment extends Fragment {

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (!(activity instanceof BaseActivity)) {
      throw new IllegalStateException("This view should be attached to Activity instance implementing ActivityComponentProvider");
    }
    BaseActivity baseActivity = (BaseActivity) activity;
    inject(baseActivity.component());
  }

  protected abstract void inject(ActivityComponent component);

  @Override
  public void onStart() {
    super.onStart();
    EventBus.register(this);
  }

  @Override
  public void onStop() {
    EventBus.unregister(this);
    super.onStop();
  }
}
