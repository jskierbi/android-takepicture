package com.jskierbi.takepicture.d2;

import com.jskierbi.takepicture.base.Application;
import dagger.Component;

@ApplicationLevel
@Component(modules = {
    ApplicationModule.class,
})
public interface ApplicationComponent {

  ActivityComponent extendToActivity(ActivityModule activityModule);

  void inject(Application application);
}
