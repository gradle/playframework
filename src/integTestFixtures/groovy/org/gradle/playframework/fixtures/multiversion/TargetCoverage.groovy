package org.gradle.playframework.fixtures.multiversion

import java.lang.annotation.ElementType
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@interface TargetCoverage {
    Class<Closure> value();
}
