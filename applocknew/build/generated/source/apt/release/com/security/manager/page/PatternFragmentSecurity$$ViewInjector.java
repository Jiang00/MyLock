// Generated code from Butter Knife. Do not modify!
package com.security.manager.page;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class PatternFragmentSecurity$$ViewInjector<T extends com.security.manager.page.PatternFragmentSecurity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689663, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131689663, "field 'toolbar'");
  }

  @Override public void reset(T target) {
    target.toolbar = null;
  }
}
