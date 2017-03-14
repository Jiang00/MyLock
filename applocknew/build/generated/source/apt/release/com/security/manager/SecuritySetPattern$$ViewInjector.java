// Generated code from Butter Knife. Do not modify!
package com.security.manager;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SecuritySetPattern$$ViewInjector<T extends com.security.manager.SecuritySetPattern> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689854, "field 'cancel'");
    target.cancel = finder.castView(view, 2131689854, "field 'cancel'");
    view = finder.findRequiredView(source, 2131689508, "field 'tip'");
    target.tip = finder.castView(view, 2131689508, "field 'tip'");
  }

  @Override public void reset(T target) {
    target.cancel = null;
    target.tip = null;
  }
}
