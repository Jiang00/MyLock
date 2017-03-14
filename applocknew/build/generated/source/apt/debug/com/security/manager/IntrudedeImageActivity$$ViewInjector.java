// Generated code from Butter Knife. Do not modify!
package com.security.manager;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class IntrudedeImageActivity$$ViewInjector<T extends com.security.manager.IntrudedeImageActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689806, "field 'blockIcon'");
    target.blockIcon = finder.castView(view, 2131689806, "field 'blockIcon'");
    view = finder.findRequiredView(source, 2131689807, "field 'blockImage'");
    target.blockImage = finder.castView(view, 2131689807, "field 'blockImage'");
    view = finder.findRequiredView(source, 2131689809, "field 'dateIcon'");
    target.dateIcon = finder.castView(view, 2131689809, "field 'dateIcon'");
    view = finder.findRequiredView(source, 2131689803, "field 'dateView'");
    target.dateView = finder.castView(view, 2131689803, "field 'dateView'");
    view = finder.findRequiredView(source, 2131689810, "field 'messageView'");
    target.messageView = finder.castView(view, 2131689810, "field 'messageView'");
    view = finder.findRequiredView(source, 2131689786, "field 'title'");
    target.title = finder.castView(view, 2131689786, "field 'title'");
    view = finder.findRequiredView(source, 2131689787, "field 'edit_mode'");
    target.edit_mode = finder.castView(view, 2131689787, "field 'edit_mode'");
    view = finder.findRequiredView(source, 2131689788, "field 'delete'");
    target.delete = finder.castView(view, 2131689788, "field 'delete'");
    view = finder.findRequiredView(source, 2131689663, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131689663, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131689808, "field 'shareImg'");
    target.shareImg = finder.castView(view, 2131689808, "field 'shareImg'");
  }

  @Override public void reset(T target) {
    target.blockIcon = null;
    target.blockImage = null;
    target.dateIcon = null;
    target.dateView = null;
    target.messageView = null;
    target.title = null;
    target.edit_mode = null;
    target.delete = null;
    target.toolbar = null;
    target.shareImg = null;
  }
}
