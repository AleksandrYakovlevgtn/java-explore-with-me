package ru.practicum.ewm.subscription.filter;

import com.querydsl.core.BooleanBuilder;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm.event.model.QEvent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SubscriptionFilterHelper {

    public static Optional<BooleanBuilder> makeBooleanBuilder(List<Long> favoriteIds, SubscriptionFilter filter) {
        java.util.function.Predicate<Object> isNullOrEmpty = obj ->
                CollectionUtils.isEmpty((Collection<?>) obj);
        QEvent qEvent = QEvent.event;

        return isNullOrEmpty.test(favoriteIds)
                ? Optional.empty()
                : Optional.of(
                new BooleanBuilder()
                        .and(qEvent.initiator.id.in(favoriteIds))
                        .and(qEvent.initiator.observable.isTrue())
                        .and(!isNullOrEmpty.test(filter.getCategoryIds()) ? qEvent.category.id.in(filter.getCategoryIds()) : null)
                        .and(!isNullOrEmpty.test(filter.getRangeStart()) ? qEvent.eventDate.after(filter.getRangeStart()) : null)
                        .and(!isNullOrEmpty.test(filter.getPaid()) ? qEvent.paid.eq(filter.getPaid()) : null)
                        .and(!isNullOrEmpty.test(filter.getOnlyAvailable())
                                ? qEvent.participantLimit.eq(0).or(qEvent.confirmedRequests.lt(qEvent.participantLimit)) : null));
    }
}