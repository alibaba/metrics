package java.lang.management;

import javax.management.ObjectName;

/**
 * Copied from JDK, to be compatible with Java 1.6
 * WispCounterMXBean is a subclass of this interface,
 * which will encounter compilation error on Java 1.6
 * @see ManagementFactory
 * @since 1.7
 */
public interface PlatformManagedObject {
    /**
     * Returns an {@link ObjectName ObjectName} instance representing
     * the object name of this platform managed object.
     *
     * @return an {@link ObjectName ObjectName} instance representing
     * the object name of this platform managed object.
     */
    public ObjectName getObjectName();
}
