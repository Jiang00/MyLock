// Generated code from Butter Knife. Do not modify!
package com.security.manager.page;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class AppFragementSecurity$ViewHolder$$ViewInjector<T extends com.security.manager.page.AppFragementSecurity.ViewHolder> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689584, "field 'icon'");
    target.icon = finder.castView(view, 2131689584, "field 'icon'");
    view = finder.findRequiredView(source, 2131689775, "field 'name'");
    target.name = finder.castView(view, 2131689775, "field 'name'");
    view = finder.findRequiredView(source, 2131689776, "field 'lock'");
    target.lock = finder.castView(view, 2131689776, "field 'lock'");
  }

  @Override public void reset(T target) {
    target.icon = null;
    target.name = null;
    target.lock = null;
  }
}
