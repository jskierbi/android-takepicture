package com.jskierbi.takepicture.d2;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.jskierbi.takepicture.base.BaseActivity;
import com.jskierbi.takepicture.modules.photos.managers.PickPhotoManager;
import dagger.Module;
import dagger.Provides;

import java.util.Locale;

/**
 * Created on 08/12/2015.
 */
@Module
public class ActivityModule {

  private BaseActivity mActivity;

  public ActivityModule(BaseActivity mActivity) {
    this.mActivity = mActivity;
  }

  @Provides
  @ForActivity
  Context provideActivityContext() {
    return mActivity;
  }

  @Provides
  @ForActivity
  @ActivityLevel
  Locale provideAcitivtyLocale(@ForActivity Context context) {
    return context.getResources().getConfiguration().locale;
  }

  @Provides
  FragmentManager provideFragmentManager() {
    return mActivity.getSupportFragmentManager();
  }

  @Provides
  @ActivityLevel
  PickPhotoManager providePickPhotoManager(FragmentManager fragmentManager) {
    Fragment managerFragment = fragmentManager.findFragmentByTag(PickPhotoManager.TAG);
    if (managerFragment == null) {
      managerFragment = new PickPhotoManager();
      fragmentManager.beginTransaction()
          .add(managerFragment, PickPhotoManager.TAG)
          .commit();
    }
    return (PickPhotoManager) managerFragment;
  }
}
