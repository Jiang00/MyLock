// Generated code from Butter Knife. Do not modify!
package com.security.manager;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SecuritySettings$$ViewInjector<T extends com.security.manager.SecuritySettings> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689850, "field 'normalTitle'");
    target.normalTitle = finder.castView(view, 2131689850, "field 'normalTitle'");
    view = finder.findRequiredView(source, 2131689663, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131689663, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131689880, "field 'facebook'");
    target.facebook = finder.castView(view, 2131689880, "field 'facebook'");
    view = finder.findRequiredView(source, 2131689881, "field 'google'");
    target.google = finder.castView(view, 2131689881, "field 'google'");
    view = finder.findRequiredView(source, 2131689879, "field 'googleplay'");
    target.googleplay = finder.castView(view, 2131689879, "field 'googleplay'");
  }

  @Override public void reset(T target) {
    target.normalTitle = null;
    target.toolbar = null;
    target.facebook = null;
    target.google = null;
    target.googleplay = null;
  }
}