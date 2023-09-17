package ru.practicum.ewm.workFolder.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.ewm.exception.NotFoundException;

import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validator {

    public static final String S_WITH_ID_S_WAS_NOT_FOUND = "%s c id=%s не найден.";

    @NotNull
    public static <T, I> T getNonNullObject(@NotNull CrudRepository<T, I> storage, I id, Class<T> tClass) throws NotFoundException {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(S_WITH_ID_S_WAS_NOT_FOUND, tClass.getSimpleName(), id)));
    }

    @NotNull
    public static <T, I> T getNonNullObject(@NotNull CrudRepository<T, I> storage, I id) throws NotFoundException {
        return getNonNullObject(storage, id, getTClass(storage));
    }

    public static <T, I> void checkId(@NotNull CrudRepository<T, I> storage, I id, Class<T> tClass) throws NotFoundException {
        if (!storage.existsById(id)) {
            throw new NotFoundException(String.format(S_WITH_ID_S_WAS_NOT_FOUND, tClass.getSimpleName(), id));
        }
    }

    public static <T, I> void checkId(@NotNull CrudRepository<T, I> storage, I id) throws NotFoundException {
        checkId(storage, id, getTClass(storage));
    }

    private static <T, I> Class<T> getTClass(CrudRepository<T, I> storage) {
        ResolvableType resolvableType = ResolvableType.forClass(storage.getClass()).as(CrudRepository.class);
        return (Class<T>) resolvableType.getGeneric(0).toClass();
    }
}