import com.kvstore.jedis.tairgis.TairGis;
import com.kvstore.jedis.tairgis.TairGisCluster;
import com.kvstore.jedis.tairgis.TairGisPipeline;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dwan
 * @date 2019/12/26
 */
public class TairGisTestBase extends TestBase {

    public static TairGis tairGis;
    public static TairGisPipeline tairGisPipeline;
    public static TairGisCluster tairGisCluster;

    @BeforeClass
    public static void setUp() {
        tairGis = new TairGis(jedis);
        tairGisPipeline = new TairGisPipeline();
        tairGisPipeline.setClient(jedis.getClient());
        tairGisCluster = new TairGisCluster(jedisCluster);

    }
}
