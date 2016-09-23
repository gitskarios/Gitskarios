package com.alorma.github.sdk.core.bean.generator;

import com.alorma.github.sdk.core.User;
import com.alorma.github.sdk.core.notifications.NotificationSubject;
import com.sergiandreplace.testabean.annotation.TargetClass;
import com.sergiandreplace.testabean.generator.Generator;

@TargetClass({NotificationSubject.class})
public class NotificationSubjectGenerator implements Generator<NotificationSubject> {
  @Override
  public NotificationSubject next() {
    NotificationSubject subject = new NotificationSubject();
    subject.setLatest_comment_url("fefw");
    subject.setTitle("fwfw");
    subject.setType("lknge");
    return subject;
  }
}
