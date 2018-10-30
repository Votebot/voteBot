package me.schlaubi.votebot.io.database;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.annotations.Transient;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.log4j.Log4j2;
import me.schlaubi.votebot.util.NameThreadFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public abstract class DatabaseEntity<T> {

    @Transient
    private final static ExecutorService executor = Executors.newCachedThreadPool(new NameThreadFactory("Database"));
    @Transient
    private final Mapper<T> mapper;
    @Transient
    private final String logPrefix;

    public DatabaseEntity(Class<T> clazz, Cassandra cassandra, String logPrefix) {
        this.logPrefix = logPrefix;
        this.mapper = cassandra.getMappingManager().mapper(clazz);
    }

    public final void delete(T entity) {
        ListenableFuture<Void> future = mapper.deleteAsync(entity);
        addCallbacks(future, "deleted", "deleting", toString());
    }

    public final void save(T entity) {
        ListenableFuture<Void> future = mapper.saveAsync(entity);
        addCallbacks(future, "saved", "saving", toString());
    }

    protected void addCallbacks(ListenableFuture<Void> future, String success, String error, String identifier) {
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                log.debug(String.format("[Database] %s Entity with id %s got %s", logPrefix, identifier, success));
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                log.error(String.format("[Database] %s An error occurred while %s entity %s", logPrefix, error, identifier), throwable);
            }
        }, executor);
    }

}
