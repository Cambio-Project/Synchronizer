package de.unistuttgart.cambio.synchronizer.events;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class DataEventTest {

    private static class DataEventTestHelper {
        private final DataEvent<Integer> dataEvent = new DataEvent<>();
        final AtomicInteger EventTriggerCount = new AtomicInteger(0);

        boolean subscribeToEvent(Consumer<Integer> runnable) {
            return dataEvent.subscribe(runnable);
        }

        boolean unsubscribeFromEvent(Consumer<Integer> runnable) {
            return dataEvent.unsubscribe(runnable);
        }

        void invokeEvent() {
            dataEvent.invoke(42);
        }
    }

    @Test
    void SubscribesSuccessfullyTest() {
        DataEventTestHelper helper = new DataEventTestHelper();
        Consumer<Integer> r = helper.EventTriggerCount::set;
        helper.subscribeToEvent(r);
        helper.invokeEvent();
        Assertions.assertEquals(42, helper.EventTriggerCount.get());
    }

    @Test
    void SubscribesOnceTest() {
        DataEventTestHelper helper = new DataEventTestHelper();
        Consumer<Integer> r = (i) -> {
            Assertions.assertEquals(i, 42);
            helper.EventTriggerCount.incrementAndGet();
        };
        Assertions.assertTrue(helper.subscribeToEvent(r));
        Assertions.assertFalse(helper.subscribeToEvent(r)); //returns false if already subscribed

        helper.invokeEvent();

        Assertions.assertEquals(1, helper.EventTriggerCount.get(), "Event was invoked more than once.");
    }


    @Test
    void UnsubscribesTest() {
        DataEventTestHelper helper = new DataEventTestHelper();
        Consumer<Integer> r = (i) -> {
            Assertions.assertEquals(i, 42);
            helper.EventTriggerCount.incrementAndGet();
        };

        Assertions.assertTrue(helper.subscribeToEvent(r));
        Assertions.assertFalse(helper.subscribeToEvent(r)); //returns false if already subscribed

        Assertions.assertTrue(helper.unsubscribeFromEvent(r));
        Assertions.assertFalse(helper.unsubscribeFromEvent(r)); //returns false if already unsubscribed

        helper.invokeEvent();

        Assertions.assertEquals(0, helper.EventTriggerCount.get(), "Event was invoked even tho no one is subscribed.");
    }


    @Test
    void MultipleSubscriptionsTest() {
        DataEventTestHelper helper = new DataEventTestHelper();
        Consumer<Integer> r = (i) -> {
            Assertions.assertEquals(i, 42);
            helper.EventTriggerCount.incrementAndGet();
        };
        Consumer<Integer> r2 = (i) -> {
            Assertions.assertEquals(i, 42);
            helper.EventTriggerCount.incrementAndGet();
        };

        Assertions.assertTrue(helper.subscribeToEvent(r));
        Assertions.assertTrue(helper.subscribeToEvent(r2));

        helper.invokeEvent();

        Assertions.assertEquals(2, helper.EventTriggerCount.get(), "Event was not invoked even tho two objects subscribed.");
    }

}