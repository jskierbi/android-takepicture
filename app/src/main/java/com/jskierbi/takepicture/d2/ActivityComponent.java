package com.jskierbi.takepicture.d2;

import com.jskierbi.takepicture.MainActivity;
import com.jskierbi.takepicture.base.BaseActivity;
import com.jskierbi.takepicture.modules.photos.managers.PickPhotoManager;
import dagger.Subcomponent;

@ActivityLevel
@Subcomponent(modules = {
    ActivityModule.class
})
public interface ActivityComponent {
  void inject(MainActivity mainActivity);

  void inject(BaseActivity baseActivity);

  void inject(PickPhotoManager pickPhotoManager);
}
