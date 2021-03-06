package rpc.common;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.DefaultIdStrategy;
import com.dyuproject.protostuff.runtime.Delegate;
import com.dyuproject.protostuff.runtime.RuntimeEnv;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * 序列化工具
 * @author mrh
 *
 */
public class SerializationUtil {

	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();
	private static final Delegate<Timestamp> TIMESTAMP_DELEGETE = new TimestampDelegete();
	public static final DefaultIdStrategy ID_STRATEGY = (DefaultIdStrategy) RuntimeEnv.ID_STRATEGY;
	
    private static Objenesis objenesis = new ObjenesisStd(true);

    private SerializationUtil() {
    }

    static {
    	ID_STRATEGY.registerDelegate(TIMESTAMP_DELEGETE);	
    }
    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }

    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            byte[] data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
            return GZIPCompressUtil.compress(data);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
        	data = GZIPCompressUtil.uncompress(data);
            T message = (T) objenesis.newInstance(cls);
            Schema<T> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
