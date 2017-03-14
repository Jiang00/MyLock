// Generated code from Butter Knife. Do not modify!
package com.security.manager;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class IntruderActivitySecurity$$ViewInjector<T extends com.security.manager.IntruderActivitySecurity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689789, "field 'listView'");
    target.listView = finder.castView(view, 2131689789, "field 'listView'");
    view = finder.findRequiredView(source, 2131689508, "field 'tip'");
    target.tip = finder.castView(view, 2131689508, "field 'tip'");
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
    target.listView = null;
    target.tip = null;
    target.toolbar = null;
    target.facebook = null;
    target.google = null;
    target.googleplay = null;
  }
}
