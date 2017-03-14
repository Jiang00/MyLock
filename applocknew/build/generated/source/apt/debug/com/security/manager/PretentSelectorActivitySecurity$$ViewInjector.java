// Generated code from Butter Knife. Do not modify!
package com.security.manager;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class PretentSelectorActivitySecurity$$ViewInjector<T extends com.security.manager.PretentSelectorActivitySecurity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689834, "field 'PretentCoverList' and method 'activeFakeCover'");
    target.PretentCoverList = finder.castView(view, 2131689834, "field 'PretentCoverList'");
    ((android.widget.AdapterView<?>) view).setOnItemClickListener(
      new android.widget.AdapterView.OnItemClickListener() {
        @Override public void onItemClick(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.activeFakeCover(p2);
        }
      });
    view = finder.findRequiredView(source, 2131689835, "field 'pretentIconList' and method 'switchFakeIcon'");
    target.pretentIconList = finder.castView(view, 2131689835, "field 'pretentIconList'");
    ((android.widget.AdapterView<?>) view).setOnItemClickListener(
      new android.widget.AdapterView.OnItemClickListener() {
        @Override public void onItemClick(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.switchFakeIcon(p2);
        }
      });
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
    target.PretentCoverList = null;
    target.pretentIconList = null;
    target.normalTitle = null;
    target.toolbar = null;
    target.facebook = null;
    target.google = null;
    target.googleplay = null;
  }
}
