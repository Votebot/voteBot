package me.schlaubi.votebot.core.cache;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Param;
import lombok.extern.log4j.Log4j2;
import me.schlaubi.votebot.io.database.Cassandra;
import me.schlaubi.votebot.io.database.SnowflakeDatabaseEntity;
import net.dv8tion.jda.core.entities.ISnowflake;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Cache<T extends SnowflakeDatabaseEntity<T>> {

    private final Map<Long, T> cacheMap;
    private final Class<T> clazz;
    private final DatabaseEntityAccessor<T> accessor;

    public Cache(Class<T> clazz, Class<? extends DatabaseEntityAccessor<T>> accessor, Cassandra cassandra) {
        this.cacheMap = new HashMap<>();
        log.debug(accessor.getCanonicalName());
        this.accessor = cassandra.getMappingManager().createAccessor(accessor);
        this.clazz = clazz;
    }

    /**
     * Returns the instance of an entity
     * @param entityId the entity's id
     * @return The entity
     * @deprecated use {@link this#get(ISnowflake)} instead
     * @see this#get(ISnowflake)
     */
    @Deprecated
    public T get(long entityId) {
        return get(() -> entityId);
    }

    /**
     * Returns the instance of an entity
     * @param entity the the entity
     * @return The entity
     */
    public T get(ISnowflake entity) {
        long entityId = entity.getIdLong();
        if (cacheMap.containsKey(entityId))
            return cacheMap.get(entityId);
        else {
            T instance = accessor.get(entityId).one();
            if (instance == null) {
                try {
                    //Create new entity of none exists
                    instance = clazz.getDeclaredConstructor(Long.class).newInstance(entityId);
                    instance.save(instance);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    log.error("[EntityCache] Error while generating new entity", e);
                }
            }
            cacheMap.put(entityId, instance);
            return instance;
        }
    }

    public void update(T instance) {
        cacheMap.replace(instance.getEntityId(), instance);
        instance.save(instance);
    }

    public interface DatabaseEntityAccessor<T> {
        Result<T> get(@Param("id") long id);
    }
}
