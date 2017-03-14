// Generated code from Butter Knife. Do not modify!
package com.security.manager;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ViewHolder$$ViewInjector<T extends com.security.manager.ViewHolder> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findOptionalView(source, 2131689801, null);
    target.icon = finder.castView(view, 2131689801, "field 'icon'");
    view = finder.findOptionalView(source, 2131689584, null);
    target.fakeicon = finder.castView(view, 2131689584, "field 'fakeicon'");
    view = finder.findOptionalView(source, 2131689775, null);
    target.appName = finder.castView(view, 2131689775, "field 'appName'");
    view = finder.findOptionalView(source, 2131689802, null);
    target.encrypted = view;
    view = finder.findOptionalView(source, 2131689799, null);
    target.intrudenewicon = view;
    view = finder.findOptionalView(source, 2131689803, null);
    target.simName = finder.castView(view, 2131689803, "field 'simName'");
    view = finder.findOptionalView(source, 2131689798, null);
    target.lockIcon = finder.castView(view, 2131689798, "field 'lockIcon'");
  }

  @Override public void reset(T target) {
    target.icon = null;
    target.fakeicon = null;
    target.appName = null;
    target.encrypted = null;
    target.intrudenewicon = null;
    target.simName = null;
    target.lockIcon = null;
  }
}
