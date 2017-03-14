// Generated code from Butter Knife. Do not modify!
package com.security.manager.page;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class WidgetSwitch$$ViewInjector<T extends com.security.manager.page.WidgetSwitch> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689584, "field 'icon'");
    target.icon = finder.castView(view, 2131689584, "field 'icon'");
    view = finder.findRequiredView(source, 2131689509, "field 'title'");
    target.title = finder.castView(view, 2131689509, "field 'title'");
  }

  @Override public void reset(T target) {
    target.icon = null;
    target.title = null;
  }
}
