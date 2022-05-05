package de.unistuttgart.cambio.synchronizer.events;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

class SimpleEventTest {

    private static class SimpleEventTestHelper {
        private final SimpleEvent simpleEvent = new SimpleEvent();
        final AtomicInteger EventTriggerCount = new AtomicInteger(0);

        boolean subscribeToEvent(Runnable runnable) {
            return simpleEvent.subscribe(runnable);
        }

        boolean unsubscribeFromEvent(Runnable runnable) {
            return simpleEvent.unsubscribe(runnable);
        }

        void invokeEvent() {
            simpleEvent.invoke();
        }
    }

    @Test
    void SubscribesSuccessfullyTest() {
        SimpleEventTestHelper helper = new SimpleEventTestHelper();
        Runnable r = helper.EventTriggerCount::incrementAndGet;
        helper.subscribeToEvent(r);
        helper.invokeEvent();
        Assertions.assertTrue(helper.EventTriggerCount.get() > 0);
    }

    @Test
    void SubscribesOnceTest() {
        SimpleEventTestHelper helper = new SimpleEventTestHelper();
        Runnable r = helper.EventTriggerCount::incrementAndGet;
        Assertions.assertTrue(helper.subscribeToEvent(r));
        Assertions.assertFalse(helper.subscribeToEvent(r)); //returns false if already subscribed

        helper.invokeEvent();

        Assertions.assertEquals(1, helper.EventTriggerCount.get(), "Event was invoked more than once.");
    }


    @Test
    void UnsubscribesTest() {
        SimpleEventTestHelper helper = new SimpleEventTestHelper();
        Runnable r = helper.EventTriggerCount::incrementAndGet;

        Assertions.assertTrue(helper.subscribeToEvent(r));
        Assertions.assertFalse(helper.subscribeToEvent(r)); //returns false if already subscribed

        Assertions.assertTrue(helper.unsubscribeFromEvent(r));
        Assertions.assertFalse(helper.unsubscribeFromEvent(r)); //returns false if already unsubscribed

        helper.invokeEvent();

        Assertions.assertEquals(0, helper.EventTriggerCount.get(), "Event was invoked even tho no one is subscribed.");
    }


    @Test
    void MultipleSubscriptionsTest() {
        SimpleEventTestHelper helper = new SimpleEventTestHelper();
        Runnable r = helper.EventTriggerCount::incrementAndGet;
        Runnable r2 = helper.EventTriggerCount::incrementAndGet;

        Assertions.assertTrue(helper.subscribeToEvent(r));
        Assertions.assertTrue(helper.subscribeToEvent(r2));

        helper.invokeEvent();

        Assertions.assertEquals(2, helper.EventTriggerCount.get(), "Event was not invoked even tho two objects subscribed.");
    }

}