// Generated code from Butter Knife. Do not modify!
package com.example.nataliasobolewska.androidapp;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity target, View source) {
    this.target = target;

    target.imageView = Utils.findRequiredViewAsType(source, R.id.imageView, "field 'imageView'", ImageView.class);
    target.imageViewOfWarior = Utils.findRequiredViewAsType(source, R.id.imageViewOfWarior, "field 'imageViewOfWarior'", ImageView.class);
    target.imageViewOfWarior2 = Utils.findRequiredViewAsType(source, R.id.imageViewOfWarior2, "field 'imageViewOfWarior2'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.imageView = null;
    target.imageViewOfWarior = null;
    target.imageViewOfWarior2 = null;
  }
}
