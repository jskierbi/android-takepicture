package com.jskierbi.takepicture.d2;

import android.content.Context;
import com.jskierbi.takepicture.base.Application;
import dagger.Module;
import dagger.Provides;

/**
 * Created on 08/12/2015.
 */
@Module
public class ApplicationModule {

  private Application applicaiton;

  public ApplicationModule(Application applicaiton) {
    this.applicaiton = applicaiton;
  }

  @Provides
  @ForApplication
  Context provideAppContext() {
    return applicaiton.getApplicationContext();
  }
}
