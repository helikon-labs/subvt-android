-dontwarn com.google.j2objc.annotations.RetainedWith
-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient
-dontwarn org.slf4j.impl.StaticLoggerBinder

# spongycastle
-keep class org.spongycastle.**
-dontwarn org.spongycastle.jce.provider.X509LDAPCertStoreSpi
-dontwarn org.spongycastle.x509.util.LDAPStoreHelper

# immutable collections
-keep,allowobfuscation,allowshrinking class kotlinx.collections.immutable.** { *; }

# guava
-keep class com.google.common.reflect.** { *; }
-dontwarn com.google.j2objc.annotations.Weak

-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# subvt data
-keep class io.helikon.subvt.data.model.** { *; }
-keep class io.helikon.subvt.data.serde.** { *; }
-keep class io.helikon.subvt.data.service.** { *; }

# retrofit
-keep class retrofit2.** { *; }