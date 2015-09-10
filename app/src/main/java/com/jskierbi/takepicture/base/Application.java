package com.jskierbi.takepicture.base;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import com.jskierbi.takepicture.d2.*;

import javax.inject.Inject;

/**
 * Created on 08/12/2015.
 */
public class Application extends android.app.Application {

  @VisibleForTesting
  protected ApplicationComponent applicationComponent;

  @Inject
  @ForApplication
  Context context;

  // uu-gly!
  private static Application applicaitonInstance;

  @Override
  public void onCreate() {
    applicaitonInstance = this;
    super.onCreate();
    initDagger2();
  }

  @VisibleForTesting
  protected void initDagger2() {
    applicationComponent = DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .build();
    applicationComponent.inject(this);
  }

  public ApplicationComponent appComponent() {
    return applicationComponent;
  }

  public ActivityComponent activityComponent(BaseActivity activity) {
    return applicationComponent.extendToActivity(new ActivityModule(activity));
  }

  public static Application get(Context context) {
    return (Application) context.getApplicationContext();
  }

  public static Application get() {
    return applicaitonInstance;
  }
}
