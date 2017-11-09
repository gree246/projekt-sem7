// Generated code from Butter Knife. Do not modify!
package com.gd.etimap;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
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

    target.rotateLeftButton = Utils.findRequiredViewAsType(source, R.id.rotateLeft_id, "field 'rotateLeftButton'", Button.class);
    target.rotateRightButton = Utils.findRequiredViewAsType(source, R.id.rotateRight_id, "field 'rotateRightButton'", Button.class);
    target.gunButton = Utils.findRequiredViewAsType(source, R.id.buttonGun, "field 'gunButton'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rotateLeftButton = null;
    target.rotateRightButton = null;
    target.gunButton = null;
  }
}
