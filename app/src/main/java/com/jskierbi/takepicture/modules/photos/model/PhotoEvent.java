package com.jskierbi.takepicture.modules.photos.model;

import android.net.Uri;
import rx.Observable;

/**
 * Created on 09/08/2015.
 */
public class PhotoEvent {

  private final Observable<Uri> imageUriObservable;

  public PhotoEvent(Observable<Uri> imageUriObservable) {
    this.imageUriObservable = imageUriObservable;
  }

  public Observable<Uri> getImageUriObservable() {
    return imageUriObservable;
  }
}
